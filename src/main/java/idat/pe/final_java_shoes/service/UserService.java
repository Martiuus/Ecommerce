package idat.pe.final_java_shoes.service;

import idat.pe.final_java_shoes.dto.ProfileUpdateDto;
import idat.pe.final_java_shoes.dto.UserRegistrationDto;
import idat.pe.final_java_shoes.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Usuario save(UserRegistrationDto registrationDto);
    void createPasswordResetTokenForUser(Usuario usuario, String token);
    Optional<Usuario> getUserByPasswordResetToken(String token);
    void changeUserPassword(Usuario usuario, String newPassword);
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Usuario updateUser(Usuario usuario);
    void updateProfile(Usuario usuario, ProfileUpdateDto profileUpdateDto);
    boolean checkCurrentPassword(Usuario usuario, String currentPassword);

    // --- NUEVO MÉTODO ---
    void deleteUserById(Long id);
}
