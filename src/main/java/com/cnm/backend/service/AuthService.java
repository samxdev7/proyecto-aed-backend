package com.cnm.backend.service;

import com.cnm.backend.dto.auth.GoogleAuthRequestDto;
import com.cnm.backend.dto.auth.LoginRequestDto;
import com.cnm.backend.dto.auth.LoginResponseDto;
import com.cnm.backend.dto.auth.MensajeResponseDto;
import com.cnm.backend.dto.auth.RecuperarContrasenaRequestDto;
import com.cnm.backend.dto.auth.RegistroUsuarioRequestDto;
import com.cnm.backend.dto.auth.RestablecerContrasenaRequestDto;
import com.cnm.backend.dto.usuario.UsuarioPerfilResponseDto;
import com.cnm.backend.entity.Rol;
import com.cnm.backend.entity.TokenRecuperacion;
import com.cnm.backend.entity.Usuario;
import com.cnm.backend.repository.TokenRecuperacionRepository;
import com.cnm.backend.repository.UsuarioRepository;
import com.cnm.backend.security.GoogleTokenVerifierService;
import com.cnm.backend.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionRepository tokenRecuperacionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoogleTokenVerifierService googleTokenVerifierService;
    private final EmailService emailService;
    private final String frontendUrl;

    public AuthService(UsuarioRepository usuarioRepository,
                       TokenRecuperacionRepository tokenRecuperacionRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       GoogleTokenVerifierService googleTokenVerifierService,
                       EmailService emailService,
                       @Value("${app.frontend.url:http://localhost:3000}") String frontendUrl) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRecuperacionRepository = tokenRecuperacionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.googleTokenVerifierService = googleTokenVerifierService;
        this.emailService = emailService;
        this.frontendUrl = frontendUrl;
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

    @Transactional
    public MensajeResponseDto solicitarRecuperacion(RecuperarContrasenaRequestDto request) {
        String correoLimpio = request.correo().trim().toLowerCase();
        var optUsuario = usuarioRepository.findByCorreo(correoLimpio);

        // Seguridad / Anti-enumeración: si el usuario no existe, devolvemos el mismo mensaje de éxito
        if (optUsuario.isEmpty()) {
            log.info("Solicitud de recuperación para correo no registrado: {}", correoLimpio);
            return new MensajeResponseDto(
                    "Si el correo está registrado en nuestra plataforma, recibirás un enlace para restablecer tu contraseña."
            );
        }

        Usuario usuario = optUsuario.get();

        // Invalidar tokens previos no utilizados para este usuario
        var tokensPrevios = tokenRecuperacionRepository.findByUsuarioAndUsadoFalse(usuario);
        for (TokenRecuperacion tp : tokensPrevios) {
            tp.setUsado(true);
        }
        tokenRecuperacionRepository.saveAll(tokensPrevios);

        // Generar nuevo token criptográfico seguro
        String tokenValor = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
        TokenRecuperacion nuevoToken = new TokenRecuperacion(
                usuario,
                tokenValor,
                ZonedDateTime.now().plusHours(1)
        );
        tokenRecuperacionRepository.save(nuevoToken);

        // Construir enlace de restablecimiento
        String baseFrontend = (frontendUrl != null && !frontendUrl.isBlank())
                ? frontendUrl.replaceAll("/+$", "")
                : "http://localhost:3000";
        String enlaceRestablecimiento = baseFrontend + "/restablecer-contrasena?token=" + tokenValor;

        // Enviar correo electrónico institucional
        emailService.enviarCorreoRecuperacionContrasena(
                usuario.getCorreo(),
                usuario.getPrimerNombre(),
                enlaceRestablecimiento
        );

        return new MensajeResponseDto(
                "Si el correo está registrado en nuestra plataforma, recibirás un enlace para restablecer tu contraseña."
        );
    }

    @Transactional
    public MensajeResponseDto restablecerContrasena(RestablecerContrasenaRequestDto request) {
        TokenRecuperacion tokenRecuperacion = tokenRecuperacionRepository.findByToken(request.token())
                .orElseThrow(() -> new RuntimeException("El enlace de recuperación es inválido o no existe."));

        if (!tokenRecuperacion.esValido()) {
            throw new RuntimeException("El enlace de recuperación ha expirado o ya fue utilizado. Por favor solicita uno nuevo.");
        }

        Usuario usuario = tokenRecuperacion.getUsuario();
        usuario.setContrasenaHash(passwordEncoder.encode(request.nuevaContrasena()));
        usuarioRepository.save(usuario);

        tokenRecuperacion.setUsado(true);
        tokenRecuperacionRepository.save(tokenRecuperacion);

        return new MensajeResponseDto("Contraseña restablecida exitosamente. Ya puedes iniciar sesión con tu nueva contraseña.");
    }
}
