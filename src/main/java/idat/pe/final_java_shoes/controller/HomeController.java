package idat.pe.final_java_shoes.controller;

import idat.pe.final_java_shoes.model.Producto;
import idat.pe.final_java_shoes.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    private final ProductoService productoService;

    public HomeController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Producto> productos = productoService.findAll();
        logger.info("Número de productos encontrados: " + productos.size());
        model.addAttribute("products", productos);
        return "index";
    }
}