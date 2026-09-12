package com.cnm.backend.dto.campo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para editar la pregunta u opciones de un campo existente (Endpoint D3 - PUT /viajes/{idViaje}/campos-formulario/{idCampo}).
 * Derivado de RF7 (reemplaza pregunta, opciones, orden y obligatoriedad, preservando integridad de respuestas preexistentes).
 */
public record ActualizarCampoFormularioRequestDto(
    @NotBlank(message = "La etiqueta o pregunta del campo es obligatoria")
    @Size(max = 100, message = "La etiqueta no debe superar 100 caracteres")
    String etiquetaPregunta,

    String opcionesRespuesta,

    @NotNull(message = "El orden es obligatorio")
    @PositiveOrZero(message = "El orden debe ser mayor o igual a cero")
    Integer orden,

    boolean obligatorio
) {}
