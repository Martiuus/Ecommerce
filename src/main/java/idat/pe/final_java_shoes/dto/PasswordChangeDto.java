package idat.pe.final_java_shoes.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PasswordChangeDto {
    @NotEmpty(message = "La contraseña actual no puede estar vacía.")
    private String currentPassword;

    @NotEmpty(message = "La nueva contraseña no puede estar vacía.")
    private String newPassword;

    @NotEmpty(message = "La confirmación no puede estar vacía.")
    private String confirmNewPassword;
}
