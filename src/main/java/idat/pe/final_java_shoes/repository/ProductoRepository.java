package idat.pe.final_java_shoes.repository;

import idat.pe.final_java_shoes.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByNombreContainingIgnoreCase(String keyword);

    // --- NUEVO MÉTODO ---
    List<Producto> findByCategoria_Id(Long categoriaId);
}