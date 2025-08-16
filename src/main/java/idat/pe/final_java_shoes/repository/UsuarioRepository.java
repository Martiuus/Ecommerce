package idat.pe.final_java_shoes.repository;

import idat.pe.final_java_shoes.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    // --- NUEVO MÉTODO ---
    Optional<Usuario> findByTokenRecuperacion(String token);
}