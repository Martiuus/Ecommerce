package idat.pe.final_java_shoes.controller;

import idat.pe.final_java_shoes.model.Producto;
import idat.pe.final_java_shoes.service.CartService;
import idat.pe.final_java_shoes.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/carrito")
public class CartController {

    private final CartService cartService;
    private final ProductoService productoService;

    public CartController(CartService cartService, ProductoService productoService) {
        this.cartService = cartService;
        this.productoService = productoService;
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        model.addAttribute("cartItems", cartService.getCart(session));
        model.addAttribute("total", cartService.getTotal(session));
        return "carrito"; // Nueva vista que crearemos
    }

    @GetMapping("/agregar/{productoId}")
    public String addToCart(@PathVariable Long productoId, HttpSession session) {
        Optional<Producto> productoOpt = productoService.findById(productoId);
        productoOpt.ifPresent(producto -> cartService.addToCart(producto, session));
        return "redirect:/carrito";
    }

    @PostMapping("/actualizar")
    @ResponseBody // Importante: para que no intente redirigir en la llamada AJAX
    public void updateCart(@RequestParam Long productoId, @RequestParam int cantidad, HttpSession session) {
        cartService.updateCart(productoId, cantidad, session);
    }

    @GetMapping("/eliminar/{productoId}")
    public String removeFromCart(@PathVariable Long productoId, HttpSession session) {
        cartService.removeFromCart(productoId, session);
        return "redirect:/carrito";
    }
}

