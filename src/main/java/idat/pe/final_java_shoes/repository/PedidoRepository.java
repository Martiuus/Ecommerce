package idat.pe.final_java_shoes.repository;

import idat.pe.final_java_shoes.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // Asegúrate de que esta importación exista

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // --- NUEVO MÉTODO ---
    List<Pedido> findByCliente_IdOrderByFechaPedidoDesc(Long clienteId);
}