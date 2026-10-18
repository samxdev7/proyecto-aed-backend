package com.cnm.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para registro de nuevo usuario cliente (Endpoint A1 - POST /auth/registro).
 * Registro simplificado: solo se exige correo y contraseña.
 * Los datos del perfil se completan después desde PATCH /usuarios/me.
 */
public record RegistroUsuarioRequestDto(
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato de correo no es válido")
    @Size(max = 150, message = "El correo no debe superar 150 caracteres")
    String correo,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    String contrasena
) {}
