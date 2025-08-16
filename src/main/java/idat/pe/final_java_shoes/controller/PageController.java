package idat.pe.final_java_shoes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/sobre-nosotros")
    public String sobreNosotros() {
        return "pages/sobre-nosotros";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "pages/contacto";
    }

    @GetMapping("/terminos")
    public String terminos() {
        return "pages/terminos";
    }

    // --- NUEVO MÉTODO ---
    @GetMapping("/politica-privacidad")
    public String politicaPrivacidad() {
        return "pages/politica-privacidad"; // Nueva vista que crearemos
    }
}
