package com.cnm.backend.service;

import com.cnm.backend.dto.auth.LoginRequestDto;
import com.cnm.backend.dto.auth.LoginResponseDto;
import com.cnm.backend.dto.auth.RegistroUsuarioRequestDto;
import com.cnm.backend.dto.usuario.UsuarioPerfilResponseDto;
import com.cnm.backend.entity.Rol;
import com.cnm.backend.entity.Usuario;
import com.cnm.backend.repository.UsuarioRepository;
import com.cnm.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDto login(LoginRequestDto request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.correo())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.contrasena(), usuario.getContrasenaHash())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(usuario.getCorreo(), usuario.getRol().name());

        return new LoginResponseDto(
                token,
                usuario.getIdUsuario(),
                usuario.getCorreo(),
                usuario.getNombreCompleto(),
                usuario.getRol().name(),
                usuario.isNotificacionesHabilitadas()
        );
    }

    public UsuarioPerfilResponseDto registro(RegistroUsuarioRequestDto request) {
        if (usuarioRepository.findByCorreo(request.correo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setPrimerNombre(request.primerNombre());
        usuario.setSegundoNombre(request.segundoNombre());
        usuario.setPrimerApellido(request.primerApellido());
        usuario.setSegundoApellido(request.segundoApellido());
        usuario.setCorreo(request.correo());
        usuario.setContrasenaHash(passwordEncoder.encode(request.contrasena()));
        usuario.setRol(Rol.cliente);
        usuario.setTelefono(request.telefono());
        usuario.setSexo(request.sexo());
        usuario.setNacionalidad(request.nacionalidad());
        usuario.setTipoIdentificacion(request.tipoIdentificacion());
        usuario.setNumeroIdentificacion(request.numeroIdentificacion());
        usuario.setNotificacionesHabilitadas(request.notificacionesHabilitadas());
        usuario.setFechaRegistro(ZonedDateTime.now());

        usuarioRepository.save(usuario);

        return new UsuarioPerfilResponseDto(
                usuario.getIdUsuario(),
                usuario.getPrimerNombre(),
                usuario.getSegundoNombre(),
                usuario.getPrimerApellido(),
                usuario.getSegundoApellido(),
                usuario.getNombreCompleto(),
                usuario.getCorreo(),
                usuario.getRol().name(),
                usuario.getTelefono(),
                usuario.getSexo(),
                usuario.getNacionalidad(),
                usuario.getTipoIdentificacion(),
                usuario.getNumeroIdentificacion(),
                usuario.isNotificacionesHabilitadas(),
                usuario.getFechaRegistro()
        );
    }
}
