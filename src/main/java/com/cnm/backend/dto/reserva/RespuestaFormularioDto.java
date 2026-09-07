package com.cnm.backend.dto.reserva;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para la respuesta individual a un campo dinámico de formulario en una reserva.
 * Derivado de RF1, RF7.
 */
public record RespuestaFormularioDto(
    @NotNull(message = "El identificador del campo es obligatorio")
    Long idCampo,

    String etiquetaPregunta,
    String valorRespuesta
) {}
