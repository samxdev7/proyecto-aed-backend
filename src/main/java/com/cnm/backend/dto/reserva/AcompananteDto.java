package com.cnm.backend.dto.reserva;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para acompañantes incluidos en una reserva.
 * Derivado de RF1, RF9 y normalización 1FN.
 */
public record AcompananteDto(
    @NotBlank(message = "El primer nombre del acompañante es obligatorio")
    @Size(max = 50, message = "El primer nombre no debe superar 50 caracteres")
    String primerNombre,

    @Size(max = 50, message = "El segundo nombre no debe superar 50 caracteres")
    String segundoNombre,

    @NotBlank(message = "El primer apellido del acompañante es obligatorio")
    @Size(max = 50, message = "El primer apellido no debe superar 50 caracteres")
    String primerApellido,

    @Size(max = 50, message = "El segundo apellido no debe superar 50 caracteres")
    String segundoApellido,

    @NotBlank(message = "El tipo de identificación es obligatorio")
    @Pattern(regexp = "^(cedula|pasaporte)$", message = "El tipo de identificación debe ser 'cedula' o 'pasaporte'")
    String tipoIdentificacion,

    @NotBlank(message = "El número de identificación es obligatorio")
    @Size(max = 50, message = "El número de identificación no debe superar 50 caracteres")
    String numeroIdentificacion
) {
    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder();
        if (primerNombre != null) sb.append(primerNombre);
        if (segundoNombre != null && !segundoNombre.isBlank()) sb.append(" ").append(segundoNombre);
        if (primerApellido != null) sb.append(" ").append(primerApellido);
        if (segundoApellido != null && !segundoApellido.isBlank()) sb.append(" ").append(segundoApellido);
        return sb.toString().trim();
    }
}
