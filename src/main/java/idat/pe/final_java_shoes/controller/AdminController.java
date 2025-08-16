package idat.pe.final_java_shoes.controller;

import idat.pe.final_java_shoes.model.Pedido;
import idat.pe.final_java_shoes.model.Producto;
import idat.pe.final_java_shoes.model.Usuario;
import idat.pe.final_java_shoes.repository.PedidoRepository;
import idat.pe.final_java_shoes.service.CategoriaService;
import idat.pe.final_java_shoes.service.EmailService;
import idat.pe.final_java_shoes.service.PedidoService;
import idat.pe.final_java_shoes.service.ProductoService;
import idat.pe.final_java_shoes.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final UserService userService;
    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;
    private final EmailService emailService;

    public AdminController(ProductoService productoService, CategoriaService categoriaService, UserService userService, PedidoService pedidoService, PedidoRepository pedidoRepository, EmailService emailService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.userService = userService;
        this.pedidoService = pedidoService;
        this.pedidoRepository = pedidoRepository;
        this.emailService = emailService;
    }

    @GetMapping
    public String adminHome() {
        return "redirect:/admin/productos";
    }

    @GetMapping("/productos")
    public String adminProductos(Model model) {
        model.addAttribute("productos", productoService.findAll());
        return "admin/productos";
    }

    @GetMapping("/productos/nuevo")
    public String formNuevoProducto(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.findAll());
        return "admin/producto-form";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(@ModelAttribute Producto producto) {
        productoService.save(producto);
        return "redirect:/admin/productos";
    }

    @GetMapping("/productos/editar/{id}")
    public String formEditarProducto(@PathVariable Long id, Model model) {
        Optional<Producto> productoOptional = productoService.findById(id);
        if (productoOptional.isPresent()) {
            model.addAttribute("producto", productoOptional.get());
            model.addAttribute("categorias", categoriaService.findAll());
            return "admin/producto-form";
        }
        return "redirect:/admin/productos";
    }

    @GetMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoService.deleteById(id);
        return "redirect:/admin/productos";
    }

    @GetMapping("/usuarios")
    public String adminUsuarios(Model model) {
        model.addAttribute("usuarios", userService.findAll());
        return "admin/usuarios";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String formEditarUsuario(@PathVariable Long id, Model model) {
        Optional<Usuario> usuarioOptional = userService.findById(id);
        if (usuarioOptional.isPresent()) {
            model.addAttribute("usuario", usuarioOptional.get());
            return "admin/usuario-form";
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        Usuario usuarioExistente = userService.findById(usuario.getId()).orElseThrow();
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setEmail(usuario.getEmail());
        usuarioExistente.setRol(usuario.getRol());
        usuarioExistente.setActivo(usuario.isActivo());

        userService.updateUser(usuarioExistente);
        return "redirect:/admin/usuarios";
    }

    // --- NUEVO MÉTODO PARA DESACTIVAR USUARIO ---
    @GetMapping("/usuarios/desactivar/{id}")
    public String desactivarUsuario(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/pedidos")
    public String adminPedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.findAll());
        return "admin/pedidos";
    }

    @GetMapping("/pedidos/cancelar/{id}")
    public String cancelarPedido(@PathVariable Long id) {
        pedidoService.cancelarPedido(id);
        return "redirect:/admin/pedidos";
    }

    @GetMapping("/pedidos/enviar/{id}")
    public String enviarPedido(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if ("PAGADO".equals(pedido.getEstado())) {
            pedido.setEstado("ENVIADO");
            pedidoRepository.save(pedido);
            emailService.sendOrderShippedEmail(pedido);
        }
        return "redirect:/admin/pedidos";
    }
}