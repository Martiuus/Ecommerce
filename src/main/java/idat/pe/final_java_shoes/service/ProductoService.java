package idat.pe.final_java_shoes.service;

import idat.pe.final_java_shoes.model.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> findAll();
    Optional<Producto> findById(Long id);
    Producto save(Producto producto);
    void deleteById(Long id);
    List<Producto> search(String keyword);
    // --- NUEVO MÉTODO ---
    List<Producto> findByCategoriaId(Long categoriaId);
}
