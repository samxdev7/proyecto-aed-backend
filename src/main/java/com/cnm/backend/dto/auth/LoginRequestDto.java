package com.cnm.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para inicio de sesión (Endpoint A2 - POST /auth/login).
 * Derivado de RF5.
 */
public record LoginRequestDto(
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato de correo no es válido")
    String correo,

    @NotBlank(message = "La contraseña es obligatoria")
    String contrasena
) {}
