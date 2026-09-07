package com.cnm.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para registro de nuevo usuario cliente (Endpoint A1 - POST /auth/registro).
 * Derivado de RF5, RNF2, RNF8.
 */
public record RegistroUsuarioRequestDto(
    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(max = 50, message = "El primer nombre no debe superar 50 caracteres")
    String primerNombre,

    @Size(max = 50, message = "El segundo nombre no debe superar 50 caracteres")
    String segundoNombre,

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 50, message = "El primer apellido no debe superar 50 caracteres")
    String primerApellido,

    @Size(max = 50, message = "El segundo apellido no debe superar 50 caracteres")
    String segundoApellido,

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato de correo no es válido")
    @Size(max = 150, message = "El correo no debe superar 150 caracteres")
    String correo,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    String contrasena,

    @Pattern(regexp = "^[+0-9 -]{8,20}$", message = "El número telefónico no tiene un formato válido")
    String telefono,

    @Pattern(regexp = "^(M|F|Otro)$", message = "El sexo debe ser 'M', 'F' u 'Otro'")
    String sexo,

    @NotBlank(message = "La nacionalidad es obligatoria")
    String nacionalidad,

    @NotBlank(message = "El tipo de identificación es obligatorio")
    @Pattern(regexp = "^(cedula|pasaporte)$", message = "El tipo de identificación debe ser 'cedula' o 'pasaporte'")
    String tipoIdentificacion,

    @NotBlank(message = "El número de identificación es obligatorio")
    String numeroIdentificacion,

    boolean notificacionesHabilitadas
) {}
