package idat.pe.final_java_shoes.service;

import idat.pe.final_java_shoes.dto.CartItemDto;
import idat.pe.final_java_shoes.model.DetallePedido;
import idat.pe.final_java_shoes.model.Producto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private static final String CART_SESSION_KEY = "cart";

    public List<CartItemDto> getCart(HttpSession session) {
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    public void addToCart(Producto producto, HttpSession session) {
        List<CartItemDto> cart = getCart(session);
        for (CartItemDto item : cart) {
            if (item.getProductoId().equals(producto.getId())) {
                item.setCantidad(item.getCantidad() + 1);
                return;
            }
        }
        cart.add(new CartItemDto(producto));
    }

    // --- NUEVO MÉTODO ---
    public void addMultipleToCart(List<DetallePedido> detalles, HttpSession session) {
        for (DetallePedido detalle : detalles) {
            Producto producto = detalle.getProducto();
            List<CartItemDto> cart = getCart(session);
            boolean found = false;
            // Si el producto ya existe en el carrito, suma la cantidad
            for (CartItemDto item : cart) {
                if (item.getProductoId().equals(producto.getId())) {
                    item.setCantidad(item.getCantidad() + detalle.getCantidad());
                    found = true;
                    break;
                }
            }
            // Si no existe, lo añade como un nuevo ítem
            if (!found) {
                CartItemDto newItem = new CartItemDto(producto);
                newItem.setCantidad(detalle.getCantidad());
                cart.add(newItem);
            }
        }
    }

    public void updateCart(Long productoId, int cantidad, HttpSession session) {
        if (cantidad <= 0) {
            removeFromCart(productoId, session);
            return;
        }
        List<CartItemDto> cart = getCart(session);
        for (CartItemDto item : cart) {
            if (item.getProductoId().equals(productoId)) {
                item.setCantidad(cantidad);
                return;
            }
        }
    }

    public void removeFromCart(Long productoId, HttpSession session) {
        List<CartItemDto> cart = getCart(session);
        cart.removeIf(item -> item.getProductoId().equals(productoId));
    }

    public BigDecimal getTotal(HttpSession session) {
        return getCart(session).stream()
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getCartItemCount(HttpSession session) {
        return getCart(session).stream()
                .mapToInt(CartItemDto::getCantidad)
                .sum();
    }
}
