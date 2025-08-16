package idat.pe.final_java_shoes.service;

import idat.pe.final_java_shoes.model.Pedido;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendOrderConfirmationEmail(Pedido pedido) {
        try {
            Context context = new Context();
            context.setVariable("pedido", pedido);

            String process = templateEngine.process("email/confirmacion-pedido", context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setSubject("Confirmación de tu pedido en ZapShoes #" + pedido.getId());
            helper.setText(process, true);
            helper.setTo(pedido.getCliente().getEmail());

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendPasswordResetEmail(String to, String token) {
        try {
            String resetUrl = "http://localhost:8080/reset-password?token=" + token;

            Context context = new Context();
            context.setVariable("resetUrl", resetUrl);

            String process = templateEngine.process("email/recuperacion-contrasena", context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setSubject("Recupera tu contraseña de ZapShoes");
            helper.setText(process, true);
            helper.setTo(to);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendOrderCancellationEmail(Pedido pedido) {
        try {
            Context context = new Context();
            context.setVariable("pedido", pedido);

            String process = templateEngine.process("email/cancelacion-pedido", context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setSubject("Tu pedido #" + pedido.getId() + " en ZapShoes ha sido cancelado");
            helper.setText(process, true);
            helper.setTo(pedido.getCliente().getEmail());

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // --- NUEVO MÉTODO ---
    public void sendOrderShippedEmail(Pedido pedido) {
        try {
            Context context = new Context();
            context.setVariable("pedido", pedido);

            String process = templateEngine.process("email/pedido-enviado", context);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setSubject("¡Tu pedido #" + pedido.getId() + " está en camino!");
            helper.setText(process, true);
            helper.setTo(pedido.getCliente().getEmail());

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}