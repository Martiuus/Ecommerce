package idat.pe.final_java_shoes.controller;

import idat.pe.final_java_shoes.model.Usuario;
import idat.pe.final_java_shoes.repository.UsuarioRepository;
import idat.pe.final_java_shoes.service.EmailService;
import idat.pe.final_java_shoes.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PasswordResetController {

    private final UserService userService;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    public PasswordResetController(UserService userService, UsuarioRepository usuarioRepository, EmailService emailService) {
        this.userService = userService;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String userEmail, RedirectAttributes redirectAttributes) {
        Optional<Usuario> userOptional = usuarioRepository.findByEmail(userEmail);
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No se encontró una cuenta con ese correo electrónico.");
            return "redirect:/forgot-password";
        }

        Usuario user = userOptional.get();
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);
        emailService.sendPasswordResetEmail(user.getEmail(), token);

        redirectAttributes.addFlashAttribute("message", "Se ha enviado un correo con instrucciones para restablecer tu contraseña.");
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> userOptional = userService.getUserByPasswordResetToken(token);
        if (userOptional.isEmpty() || userOptional.get().getFechaExpiracionToken().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "El enlace de recuperación es inválido o ha expirado.");
            return "redirect:/forgot-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes) {

        Optional<Usuario> userOptional = userService.getUserByPasswordResetToken(token);
        if (userOptional.isEmpty() || userOptional.get().getFechaExpiracionToken().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "El enlace de recuperación es inválido o ha expirado.");
            return "redirect:/forgot-password";
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
            return "redirect:/reset-password?token=" + token;
        }

        userService.changeUserPassword(userOptional.get(), password);
        redirectAttributes.addFlashAttribute("message", "Tu contraseña ha sido actualizada exitosamente.");
        return "redirect:/login";
    }
}
