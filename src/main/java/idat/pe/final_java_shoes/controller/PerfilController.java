package idat.pe.final_java_shoes.controller;

import idat.pe.final_java_shoes.dto.PasswordChangeDto;
import idat.pe.final_java_shoes.dto.ProfileUpdateDto;
import idat.pe.final_java_shoes.model.Pedido;
import idat.pe.final_java_shoes.model.Usuario;
import idat.pe.final_java_shoes.repository.PedidoRepository;
import idat.pe.final_java_shoes.repository.UsuarioRepository;
import idat.pe.final_java_shoes.service.CartService;
import idat.pe.final_java_shoes.service.PedidoService;
import idat.pe.final_java_shoes.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final UserService userService;
    private final PedidoService pedidoService;
    private final CartService cartService;

    public PerfilController(UsuarioRepository usuarioRepository, PedidoRepository pedidoRepository, UserService userService, PedidoService pedidoService, CartService cartService) {
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.userService = userService;
        this.pedidoService = pedidoService;
        this.cartService = cartService;
    }

    @GetMapping
    public String verPerfil(Authentication authentication, Model model) {
        String email = authentication.getName();
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            model.addAttribute("usuario", usuario);

            if (usuario.getCliente() != null) {
                List<Pedido> pedidos = pedidoRepository.findByCliente_IdOrderByFechaPedidoDesc(usuario.getCliente().getId());
                model.addAttribute("pedidos", pedidos);
            }

            return "perfil";
        }
        return "redirect:/login";
    }

    @GetMapping("/editar")
    public String showEditProfileForm(Authentication authentication, Model model) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ProfileUpdateDto profileDto = new ProfileUpdateDto();
        profileDto.setNombre(usuario.getNombre());
        profileDto.setDireccion(usuario.getDireccion());
        profileDto.setTelefono(usuario.getTelefono());

        model.addAttribute("profileDto", profileDto);
        return "perfil-editar";
    }

    @PostMapping("/editar")
    public String updateProfile(@ModelAttribute("profileDto") @Valid ProfileUpdateDto profileDto,
                                BindingResult result, Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "perfil-editar";
        }

        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        userService.updateProfile(usuario, profileDto);
        redirectAttributes.addFlashAttribute("successMessage", "¡Tu perfil ha sido actualizado exitosamente!");
        return "redirect:/perfil";
    }

    @GetMapping("/pedidos/cancelar/{id}")
    public String cancelarPedido(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Pedido pedido = pedidoRepository.findById(id).orElse(null);

        if (pedido != null && pedido.getCliente().getUsuario().getEmail().equals(email)) {
            pedidoService.cancelarPedido(id);
            redirectAttributes.addFlashAttribute("successMessage", "¡Pedido #" + id + " cancelado exitosamente!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No tienes permiso para cancelar este pedido.");
        }

        return "redirect:/perfil";
    }

    @GetMapping("/pedidos/repetir/{id}")
    public String repetirPedido(@PathVariable Long id, Authentication authentication, HttpSession session, RedirectAttributes redirectAttributes) {
        String email = authentication.getName();
        Pedido pedido = pedidoRepository.findById(id).orElse(null);

        if (pedido != null && pedido.getCliente().getUsuario().getEmail().equals(email)) {
            cartService.addMultipleToCart(pedido.getDetalles(), session);
            redirectAttributes.addFlashAttribute("successMessage", "¡Los productos del pedido #" + id + " han sido añadidos a tu carrito!");
            return "redirect:/carrito";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo encontrar el pedido.");
            return "redirect:/perfil";
        }
    }

    // --- NUEVOS MÉTODOS PARA CAMBIAR CONTRASEÑA ---
    @GetMapping("/cambiar-contrasena")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("passwordChangeDto", new PasswordChangeDto());
        return "perfil-cambiar-contrasena"; // Nueva vista que crearemos
    }

    @PostMapping("/cambiar-contrasena")
    public String changePassword(@ModelAttribute("passwordChangeDto") @Valid PasswordChangeDto passwordChangeDto,
                                 BindingResult result, Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "perfil-cambiar-contrasena";
        }

        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!userService.checkCurrentPassword(usuario, passwordChangeDto.getCurrentPassword())) {
            result.rejectValue("currentPassword", "error.passwordChangeDto", "La contraseña actual es incorrecta.");
            return "perfil-cambiar-contrasena";
        }

        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getConfirmNewPassword())) {
            result.rejectValue("confirmNewPassword", "error.passwordChangeDto", "Las nuevas contraseñas no coinciden.");
            return "perfil-cambiar-contrasena";
        }

        userService.changeUserPassword(usuario, passwordChangeDto.getNewPassword());
        redirectAttributes.addFlashAttribute("successMessage", "¡Tu contraseña ha sido actualizada exitosamente!");
        return "redirect:/perfil";
    }
}