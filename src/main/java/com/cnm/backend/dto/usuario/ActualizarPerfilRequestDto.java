package com.cnm.backend.dto.usuario;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para actualización parcial del perfil del usuario (Endpoint B2 - PATCH /usuarios/me).
 * Derivado de RNF8. Permite actualizar campos editables sin reenviar el perfil completo.
 */
public record ActualizarPerfilRequestDto(
    @Size(max = 50, message = "El primer nombre no debe superar 50 caracteres")
    String primerNombre,

    @Size(max = 50, message = "El segundo nombre no debe superar 50 caracteres")
    String segundoNombre,

    @Size(max = 50, message = "El primer apellido no debe superar 50 caracteres")
    String primerApellido,

    @Size(max = 50, message = "El segundo apellido no debe superar 50 caracteres")
    String segundoApellido,

    @Pattern(regexp = "^[+0-9 -]{8,20}$", message = "El número telefónico no tiene un formato válido")
    String telefono,

    @Pattern(regexp = "^(M|F|Otro)$", message = "El sexo debe ser 'M', 'F' u 'Otro'")
    String sexo,

    @Size(max = 50, message = "La nacionalidad no debe superar 50 caracteres")
    String nacionalidad,

    @Pattern(regexp = "^(cedula|pasaporte)$", message = "El tipo de identificación debe ser 'cedula' o 'pasaporte'")
    String tipoIdentificacion,

    @Size(max = 50, message = "El número de identificación no debe superar 50 caracteres")
    String numeroIdentificacion
) {}
