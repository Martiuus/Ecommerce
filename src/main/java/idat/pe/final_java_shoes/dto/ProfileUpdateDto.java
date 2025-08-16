package idat.pe.final_java_shoes.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProfileUpdateDto {
    @NotEmpty(message = "El nombre no puede estar vacío.")
    private String nombre;

    private String direccion;
    private String telefono;
}
