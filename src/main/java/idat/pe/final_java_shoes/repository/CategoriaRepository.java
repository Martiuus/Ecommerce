package idat.pe.final_java_shoes.repository;

import idat.pe.final_java_shoes.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}