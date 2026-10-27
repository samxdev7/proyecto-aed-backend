package com.cnm.backend.service;

import com.cnm.backend.dto.auth.MensajeResponseDto;
import com.cnm.backend.dto.auth.RecuperarContrasenaRequestDto;
import com.cnm.backend.dto.auth.RestablecerContrasenaRequestDto;
import com.cnm.backend.entity.Rol;
import com.cnm.backend.entity.TokenRecuperacion;
import com.cnm.backend.entity.Usuario;
import com.cnm.backend.repository.TokenRecuperacionRepository;
import com.cnm.backend.repository.UsuarioRepository;
import com.cnm.backend.security.GoogleTokenVerifierService;
import com.cnm.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceRecuperacionTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenRecuperacionRepository tokenRecuperacionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private GoogleTokenVerifierService googleTokenVerifierService;

    @Mock
    private EmailService emailService;

    private AuthService authService;

    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                usuarioRepository,
                tokenRecuperacionRepository,
                passwordEncoder,
                jwtService,
                googleTokenVerifierService,
                emailService,
                "https://cnm-inscripcion.vercel.app"
        );

        usuarioPrueba = new Usuario();
        usuarioPrueba.setIdUsuario(1L);
        usuarioPrueba.setCorreo("montanista@ejemplo.com");
        usuarioPrueba.setPrimerNombre("Carlos");
        usuarioPrueba.setPrimerApellido("Pérez");
        usuarioPrueba.setRol(Rol.cliente);
        usuarioPrueba.setContrasenaHash("hash_antiguo");
    }

    @Test
    void solicitarRecuperacion_usuarioExistente_invalidaPreviosGeneraTokenYEnviaCorreo() {
        when(usuarioRepository.findByCorreoIgnoreCase("montanista@ejemplo.com")).thenReturn(Optional.of(usuarioPrueba));
        
        TokenRecuperacion tokenPrevio = new TokenRecuperacion(usuarioPrueba, "token-previo", ZonedDateTime.now().plusHours(1));
        when(tokenRecuperacionRepository.findByUsuarioAndUsadoFalse(usuarioPrueba))
                .thenReturn(List.of(tokenPrevio));

        RecuperarContrasenaRequestDto request = new RecuperarContrasenaRequestDto("montanista@ejemplo.com");
        MensajeResponseDto response = authService.solicitarRecuperacion(request);

        assertNotNull(response);
        assertTrue(response.mensaje().contains("enviado un enlace"));

        // Se invalidó el token previo
        assertTrue(tokenPrevio.getUsado());
        verify(tokenRecuperacionRepository).saveAll(any());

        // Se guardó el nuevo token
        ArgumentCaptor<TokenRecuperacion> captor = ArgumentCaptor.forClass(TokenRecuperacion.class);
        verify(tokenRecuperacionRepository).save(captor.capture());
        TokenRecuperacion nuevo = captor.getValue();
        assertEquals(usuarioPrueba, nuevo.getUsuario());
        assertFalse(nuevo.getUsado());
        assertTrue(nuevo.getFechaExpiracion().isAfter(ZonedDateTime.now()));

        // Se envió el correo institucional con el enlace
        verify(emailService).enviarCorreoRecuperacionContrasena(
                eq("montanista@ejemplo.com"),
                eq("Carlos"),
                contains("https://cnm-inscripcion.vercel.app/restablecer-contrasena?token=")
        );
    }

    @Test
    void solicitarRecuperacion_usuarioNoExiste_lanzaExcepcion() {
        when(usuarioRepository.findByCorreoIgnoreCase("desconocido@ejemplo.com")).thenReturn(Optional.empty());

        RecuperarContrasenaRequestDto request = new RecuperarContrasenaRequestDto("desconocido@ejemplo.com");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.solicitarRecuperacion(request));

        assertTrue(ex.getMessage().contains("no se encuentra registrado"));
        verify(tokenRecuperacionRepository, never()).save(any());
        verify(emailService, never()).enviarCorreoRecuperacionContrasena(any(), any(), any());
    }

    @Test
    void restablecerContrasena_tokenValido_actualizaHashYMarcaTokenUsado() {
        TokenRecuperacion tokenValido = new TokenRecuperacion(
                usuarioPrueba,
                "token-seguro-123",
                ZonedDateTime.now().plusHours(1)
        );
        tokenValido.setUsado(false);

        when(tokenRecuperacionRepository.findByToken("token-seguro-123")).thenReturn(Optional.of(tokenValido));
        when(passwordEncoder.encode("NuevaPassword123!")).thenReturn("hash_nuevo_bcrypt");

        RestablecerContrasenaRequestDto request = new RestablecerContrasenaRequestDto("token-seguro-123", "NuevaPassword123!");
        MensajeResponseDto response = authService.restablecerContrasena(request);

        assertNotNull(response);
        assertTrue(response.mensaje().contains("restablecida exitosamente"));

        assertEquals("hash_nuevo_bcrypt", usuarioPrueba.getContrasenaHash());
        assertTrue(tokenValido.getUsado());

        verify(usuarioRepository).save(usuarioPrueba);
        verify(tokenRecuperacionRepository).save(tokenValido);
    }

    @Test
    void restablecerContrasena_tokenExpirado_lanzaExcepcion() {
        TokenRecuperacion tokenExpirado = new TokenRecuperacion(
                usuarioPrueba,
                "token-expirado",
                ZonedDateTime.now().minusMinutes(5)
        );
        tokenExpirado.setUsado(false);

        when(tokenRecuperacionRepository.findByToken("token-expirado")).thenReturn(Optional.of(tokenExpirado));

        RestablecerContrasenaRequestDto request = new RestablecerContrasenaRequestDto("token-expirado", "NuevaPassword123!");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.restablecerContrasena(request));

        assertTrue(ex.getMessage().contains("expirado"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void restablecerContrasena_tokenYaUsado_lanzaExcepcion() {
        TokenRecuperacion tokenUsado = new TokenRecuperacion(
                usuarioPrueba,
                "token-usado",
                ZonedDateTime.now().plusHours(1)
        );
        tokenUsado.setUsado(true);

        when(tokenRecuperacionRepository.findByToken("token-usado")).thenReturn(Optional.of(tokenUsado));

        RestablecerContrasenaRequestDto request = new RestablecerContrasenaRequestDto("token-usado", "NuevaPassword123!");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.restablecerContrasena(request));

        assertTrue(ex.getMessage().contains("ya fue utilizado") || ex.getMessage().contains("expirado"));
        verify(usuarioRepository, never()).save(any());
    }
}
