// --- ARCHIVO: Pedido.java (CORREGIDO) ---
// Ruta: final-java-shoes/src/main/java/idat/pe/final_java_shoes/model/Pedido.java

package idat.pe.final_java_shoes.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pedido_id")
    private Long id;

    // --- CORRECCIÓN AQUÍ: Se añade FetchType.EAGER ---
    // Esto asegura que el Cliente siempre se cargue junto con el Pedido.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido = LocalDateTime.now();

    private BigDecimal total;
    private String estado;

    @Column(name = "metodo_envio")
    private String metodoEnvio;

    @Column(name = "direccion_envio")
    private String direccionEnvio;

    @Column(name = "costo_envio")
    private BigDecimal costoEnvio;

    // Se añade FetchType.EAGER aquí también para asegurar que los detalles se carguen
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DetallePedido> detalles;
}
