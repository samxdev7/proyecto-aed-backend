package com.cnm.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para autenticación / registro mediante cuenta de Google.
 * Admite el idToken emitido por Google Identity Services o el token de sesión de Supabase Auth OAuth.
 */
public record GoogleAuthRequestDto(
    @NotBlank(message = "El idToken o credencial de Google es obligatorio")
    String idToken
) {}
