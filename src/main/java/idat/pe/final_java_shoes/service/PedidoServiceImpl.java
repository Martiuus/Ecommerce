package idat.pe.final_java_shoes.service;

import idat.pe.final_java_shoes.dto.CartItemDto;
import idat.pe.final_java_shoes.model.DetallePedido;
import idat.pe.final_java_shoes.model.Pedido;
import idat.pe.final_java_shoes.model.Producto;
import idat.pe.final_java_shoes.model.Usuario;
import idat.pe.final_java_shoes.repository.PedidoRepository;
import idat.pe.final_java_shoes.repository.ProductoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final CartService cartService;
    private final EmailService emailService; // <-- INYECTADO

    public PedidoServiceImpl(PedidoRepository pedidoRepository, ProductoRepository productoRepository, CartService cartService, EmailService emailService) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.cartService = cartService;
        this.emailService = emailService; // <-- INYECTADO
    }

    @Override
    @Transactional
    public Pedido crearPedido(Usuario usuario, HttpSession session) {
        Map<String, Object> shippingInfo = (Map<String, Object>) session.getAttribute("shippingInfo");
        String metodoEnvio = (String) shippingInfo.get("method");
        String direccionEnvio = (String) shippingInfo.get("address");
        BigDecimal costoEnvio = (BigDecimal) shippingInfo.get("cost");
        BigDecimal totalFinal = (BigDecimal) shippingInfo.get("total");

        Pedido pedido = new Pedido();
        pedido.setCliente(usuario.getCliente());

        pedido.setTotal(totalFinal);
        pedido.setEstado("PAGADO");
        pedido.setMetodoEnvio(metodoEnvio);
        pedido.setDireccionEnvio(direccionEnvio);
        pedido.setCostoEnvio(costoEnvio);

        List<DetallePedido> detalles = new ArrayList<>();
        for (CartItemDto itemDto : cartService.getCart(session)) {
            DetallePedido detalle = new DetallePedido();
            Producto producto = productoRepository.findById(itemDto.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStock() < itemDto.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            producto.setStock(producto.getStock() - itemDto.getCantidad());
            productoRepository.save(producto);

            detalle.setProducto(producto);
            detalle.setCantidad(itemDto.getCantidad());
            detalle.setPrecioUnitario(itemDto.getPrecio());
            detalle.setPedido(pedido);
            detalles.add(detalle);
        }

        pedido.setDetalles(detalles);
        return pedidoRepository.save(pedido);
    }

    @Override
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    @Override
    @Transactional
    public void cancelarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (!"CANCELADO".equals(pedido.getEstado())) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }

            pedido.setEstado("CANCELADO");
            pedidoRepository.save(pedido);

            // --- LLAMADA AL SERVICIO DE CORREO ---
            emailService.sendOrderCancellationEmail(pedido);
        }
    }
}