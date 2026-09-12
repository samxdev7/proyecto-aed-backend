package com.cnm.backend.controllers;

import com.cnm.backend.dto.auth.LoginRequestDto;
import com.cnm.backend.dto.auth.LoginResponseDto;
import com.cnm.backend.dto.auth.RegistroUsuarioRequestDto;
import com.cnm.backend.dto.usuario.UsuarioPerfilResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para el Módulo de Autenticación.
 * Cubre los endpoints A1 y A2 del Catálogo de Endpoints (Derivado de RF5, RNF2).
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * A1: POST /auth/registro [Público]
     * Crea una cuenta de usuario con rol "cliente" a partir de sus datos personales y credenciales.
     */
    @PostMapping("/registro")
    public ResponseEntity<UsuarioPerfilResponseDto> registrar(
            @Valid @RequestBody RegistroUsuarioRequestDto request) {
        // En una implementación completa, se delega al AuthService que hashea la contraseña y persiste.
        UsuarioPerfilResponseDto response = new UsuarioPerfilResponseDto(
                1L,
                request.primerNombre(),
                request.segundoNombre(),
                request.primerApellido(),
                request.segundoApellido(),
                UsuarioPerfilResponseDto.construirNombreCompleto(
                        request.primerNombre(), request.segundoNombre(),
                        request.primerApellido(), request.segundoApellido()),
                request.correo(),
                "cliente",
                request.telefono(),
                request.sexo(),
                request.nacionalidad(),
                request.tipoIdentificacion(),
                request.numeroIdentificacion(),
                request.notificacionesHabilitadas(),
                java.time.ZonedDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * A2: POST /auth/login [Público]
     * Valida credenciales y devuelve un token JWT junto con el rol del usuario autenticado.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {
        // En una implementación completa, se delega al AuthenticationManager y JwtService.
        LoginResponseDto response = new LoginResponseDto(
                "mocked-jwt-token-placeholder",
                1L,
                request.correo(),
                "Usuario Registrado",
                "cliente",
                true
        );
        return ResponseEntity.ok(response);
    }
}
