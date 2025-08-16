package idat.pe.final_java_shoes.repository;

import idat.pe.final_java_shoes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}