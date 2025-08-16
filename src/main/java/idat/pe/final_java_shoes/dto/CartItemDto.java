package idat.pe.final_java_shoes.dto;

import idat.pe.final_java_shoes.model.Producto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CartItemDto {

    private Long productoId;
    private String nombre;
    private BigDecimal precio;
    private String imagenUrl;
    private int cantidad;

    public CartItemDto(Producto producto) {
        this.productoId = producto.getId();
        this.nombre = producto.getNombre();
        this.precio = producto.getPrecio();
        this.imagenUrl = producto.getImagenUrl();
        this.cantidad = 1; // Por defecto se añade 1
    }

    public BigDecimal getSubtotal() {
        return precio.multiply(new BigDecimal(cantidad));
    }
}

