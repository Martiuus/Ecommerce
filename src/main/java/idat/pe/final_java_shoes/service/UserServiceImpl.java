package idat.pe.final_java_shoes.service;

import idat.pe.final_java_shoes.dto.ProfileUpdateDto;
import idat.pe.final_java_shoes.dto.UserRegistrationDto;
import idat.pe.final_java_shoes.model.Cliente;
import idat.pe.final_java_shoes.model.Usuario;
import idat.pe.final_java_shoes.repository.ClienteRepository;
import idat.pe.final_java_shoes.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UsuarioRepository usuarioRepository, ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Usuario save(UserRegistrationDto registrationDto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(registrationDto.getNombre());
        usuario.setEmail(registrationDto.getEmail());
        usuario.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        usuario.setRol("ROLE_USER");
        usuario.setActivo(true);
        Usuario savedUsuario = usuarioRepository.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setNombre(savedUsuario.getNombre());
        cliente.setEmail(savedUsuario.getEmail());
        cliente.setUsuario(savedUsuario);
        clienteRepository.save(cliente);

        return savedUsuario;
    }

    @Override
    public void createPasswordResetTokenForUser(Usuario usuario, String token) {
        usuario.setTokenRecuperacion(token);
        usuario.setFechaExpiracionToken(LocalDateTime.now().plusHours(1));
        usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> getUserByPasswordResetToken(String token) {
        return usuarioRepository.findByTokenRecuperacion(token);
    }

    @Override
    public void changeUserPassword(Usuario usuario, String newPassword) {
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuario.setTokenRecuperacion(null);
        usuario.setFechaExpiracionToken(null);
        usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario updateUser(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void updateProfile(Usuario usuario, ProfileUpdateDto profileUpdateDto) {
        usuario.setNombre(profileUpdateDto.getNombre());
        usuario.setDireccion(profileUpdateDto.getDireccion());
        usuario.setTelefono(profileUpdateDto.getTelefono());
        usuarioRepository.save(usuario);

        Cliente cliente = usuario.getCliente();
        if (cliente != null) {
            cliente.setNombre(profileUpdateDto.getNombre());
            cliente.setDireccion(profileUpdateDto.getDireccion());
            cliente.setTelefono(profileUpdateDto.getTelefono());
            clienteRepository.save(cliente);
        }
    }

    @Override
    public boolean checkCurrentPassword(Usuario usuario, String currentPassword) {
        return passwordEncoder.matches(currentPassword, usuario.getPassword());
    }

    // --- NUEVO MÉTODO IMPLEMENTADO ---
    @Override
    public void deleteUserById(Long id) {
        // Realizamos un "soft delete" para no perder el historial de pedidos
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}