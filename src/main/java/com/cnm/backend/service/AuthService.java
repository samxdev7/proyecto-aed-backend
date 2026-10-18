package com.cnm.backend.service;

import com.cnm.backend.dto.auth.GoogleAuthRequestDto;
import com.cnm.backend.dto.auth.LoginRequestDto;
import com.cnm.backend.dto.auth.LoginResponseDto;
import com.cnm.backend.dto.auth.RegistroUsuarioRequestDto;
import com.cnm.backend.dto.usuario.UsuarioPerfilResponseDto;
import com.cnm.backend.entity.Rol;
import com.cnm.backend.entity.Usuario;
import com.cnm.backend.repository.UsuarioRepository;
import com.cnm.backend.security.GoogleTokenVerifierService;
import com.cnm.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoogleTokenVerifierService googleTokenVerifierService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       GoogleTokenVerifierService googleTokenVerifierService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.googleTokenVerifierService = googleTokenVerifierService;
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
        // Solo credenciales — el perfil se completa después vía PATCH /usuarios/me
        usuario.setCorreo(request.correo());
        usuario.setContrasenaHash(passwordEncoder.encode(request.contrasena()));
        usuario.setRol(Rol.cliente);
        usuario.setNotificacionesHabilitadas(true);
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

    public LoginResponseDto autenticarConGoogle(GoogleAuthRequestDto request) {
        GoogleTokenVerifierService.GoogleUserInfo userInfo = googleTokenVerifierService.verificar(request.idToken());

        Usuario usuario = usuarioRepository.findByCorreo(userInfo.email())
                .orElseGet(() -> {
                    // Si el usuario no existe, se registra automáticamente
                    Usuario nuevo = new Usuario();
                    nuevo.setCorreo(userInfo.email());
                    nuevo.setPrimerNombre(userInfo.givenName() != null && !userInfo.givenName().isBlank()
                            ? userInfo.givenName() : "Usuario");
                    nuevo.setPrimerApellido(userInfo.familyName() != null ? userInfo.familyName() : "");
                    // Hash seguro e inusable para satisfacer la restricción NOT NULL de la columna
                    nuevo.setContrasenaHash(passwordEncoder.encode(UUID.randomUUID().toString()));
                    nuevo.setRol(Rol.cliente);
                    nuevo.setNotificacionesHabilitadas(true);
                    nuevo.setFechaRegistro(ZonedDateTime.now());
                    return usuarioRepository.save(nuevo);
                });

        // Si el usuario ya existía pero no tenía nombre registrado, completar con datos de Google
        boolean modificado = false;
        if ((usuario.getPrimerNombre() == null || usuario.getPrimerNombre().isBlank()) && userInfo.givenName() != null) {
            usuario.setPrimerNombre(userInfo.givenName());
            modificado = true;
        }
        if ((usuario.getPrimerApellido() == null || usuario.getPrimerApellido().isBlank()) && userInfo.familyName() != null) {
            usuario.setPrimerApellido(userInfo.familyName());
            modificado = true;
        }
        if (modificado) {
            usuario = usuarioRepository.save(usuario);
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
}
