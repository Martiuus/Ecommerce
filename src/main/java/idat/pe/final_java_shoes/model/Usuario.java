package idat.pe.final_java_shoes.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime; // Importamos LocalDateTime

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long id;

    private String nombre;
    private String email;

    @Column(name = "contrasena")
    private String password;

    private String rol;
    private boolean activo;
    private String direccion;
    private String telefono;

    // --- NUEVOS CAMPOS ---
    @Column(name = "token_recuperacion")
    private String tokenRecuperacion;

    @Column(name = "fecha_expiracion_token")
    private LocalDateTime fechaExpiracionToken;
    // --- FIN NUEVOS CAMPOS ---

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Cliente cliente;
}