package idat.pe.final_java_shoes.controller;

import idat.pe.final_java_shoes.model.Producto;
import idat.pe.final_java_shoes.service.CategoriaService;
import idat.pe.final_java_shoes.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CatalogoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService; // <-- INYECTADO

    public CatalogoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService; // <-- INYECTADO
    }

    @GetMapping("/catalogo")
    public String verCatalogo(@RequestParam(value = "q", required = false) String query,
                              @RequestParam(value = "categoria", required = false) Long categoriaId,
                              Model model) {

        List<Producto> productos;

        if (query != null && !query.isEmpty()) {
            productos = productoService.search(query);
            model.addAttribute("titulo", "Resultados para: '" + query + "'");
        } else if (categoriaId != null) {
            productos = productoService.findByCategoriaId(categoriaId);
            // Opcional: Poner el nombre de la categoría en el título
            categoriaService.findAll().stream()
                    .filter(c -> c.getId().equals(categoriaId))
                    .findFirst()
                    .ifPresent(cat -> model.addAttribute("titulo", "Categoría: " + cat.getNombre()));
        } else {
            productos = productoService.findAll();
            model.addAttribute("titulo", "Todos nuestros productos");
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.findAll()); // <-- Se envía la lista de categorías
        return "catalogo";
    }
}