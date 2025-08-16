package idat.pe.final_java_shoes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserRegistrationDto {
    @NotEmpty(message = "El nombre no puede estar vacío.")
    private String nombre;

    @NotEmpty(message = "El email no puede estar vacío.")
    @Email(message = "Debe ser una dirección de email válida.")
    private String email;

    @NotEmpty(message = "La contraseña no puede estar vacía.")
    private String password;
}