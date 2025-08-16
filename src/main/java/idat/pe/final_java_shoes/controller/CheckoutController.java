package idat.pe.final_java_shoes.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import idat.pe.final_java_shoes.model.Pedido;
import idat.pe.final_java_shoes.model.Usuario;
import idat.pe.final_java_shoes.repository.UsuarioRepository;
import idat.pe.final_java_shoes.service.CartService;
import idat.pe.final_java_shoes.service.EmailService;
import idat.pe.final_java_shoes.service.PedidoService;
import idat.pe.final_java_shoes.service.StripeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CheckoutController {

    @Value("${stripe.api.publicKey}")
    private String stripePublicKey;

    private final StripeService stripeService;
    private final CartService cartService;
    private final PedidoService pedidoService;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService; // <-- INYECTADO

    public CheckoutController(StripeService stripeService, CartService cartService, PedidoService pedidoService, UsuarioRepository usuarioRepository, EmailService emailService) {
        this.stripeService = stripeService;
        this.cartService = cartService;
        this.pedidoService = pedidoService;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService; // <-- INYECTADO
    }

    // ... (métodos /checkout/envio y /checkout/pago sin cambios) ...

    @GetMapping("/checkout/envio")
    public String shippingForm(HttpSession session, Model model, Authentication authentication) {
        String email = authentication.getName();
        Usuario cliente = usuarioRepository.findByEmail(email).orElse(null);

        model.addAttribute("subtotal", cartService.getTotal(session));
        model.addAttribute("cliente", cliente);
        return "checkout-envio";
    }

    @PostMapping("/checkout/pago")
    public String paymentForm(@RequestParam String shippingMethod,
                              @RequestParam(required = false) String shippingAddress,
                              HttpSession session, Model model) {

        BigDecimal subtotal = cartService.getTotal(session);
        BigDecimal shippingCost = shippingMethod.equals("domicilio") ? new BigDecimal("10.00") : BigDecimal.ZERO;
        BigDecimal total = subtotal.add(shippingCost);

        Map<String, Object> shippingInfo = new HashMap<>();
        shippingInfo.put("method", shippingMethod);
        shippingInfo.put("address", shippingAddress);
        shippingInfo.put("cost", shippingCost);
        shippingInfo.put("total", total);
        session.setAttribute("shippingInfo", shippingInfo);

        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("total", total);
        model.addAttribute("currency", "PEN");
        return "checkout-pago";
    }

    @PostMapping("/charge")
    public String charge(@RequestParam("stripeToken") String token, HttpSession session, Model model, Authentication authentication) {
        Map<String, Object> shippingInfo = (Map<String, Object>) session.getAttribute("shippingInfo");
        BigDecimal totalFinal = (BigDecimal) shippingInfo.get("total");

        try {
            Charge charge = stripeService.charge(token, totalFinal, "PEN");

            String email = authentication.getName();
            Usuario cliente = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

            Pedido pedidoGuardado = pedidoService.crearPedido(cliente, session); // <-- Obtenemos el pedido

            // --- LLAMADA AL SERVICIO DE CORREO ---
            emailService.sendOrderConfirmationEmail(pedidoGuardado);
            // --- FIN LLAMADA ---

            session.removeAttribute("cart");
            session.removeAttribute("shippingInfo");

            model.addAttribute("status", charge.getStatus());
            return "pago-exitoso";

        } catch (StripeException e) {
            model.addAttribute("error", e.getMessage());
            return "pago-fallido";
        }
    }
}