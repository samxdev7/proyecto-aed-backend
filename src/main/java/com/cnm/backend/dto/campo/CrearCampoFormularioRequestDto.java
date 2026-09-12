package com.cnm.backend.dto.campo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para agregar un nuevo campo personalizado a un viaje (Endpoint D2 - POST /viajes/{idViaje}/campos-formulario).
 * Derivado de RF7.
 */
public record CrearCampoFormularioRequestDto(
    @NotBlank(message = "El tipo de campo es obligatorio")
    @Pattern(
        regexp = "^(texto|seleccion_unica|seleccion_multiple|fecha|archivo)$",
        message = "El tipo de campo debe ser 'texto', 'seleccion_unica', 'seleccion_multiple', 'fecha' o 'archivo'"
    )
    String tipoCampo,

    @NotBlank(message = "La etiqueta o pregunta del campo es obligatoria")
    @Size(max = 100, message = "La etiqueta no debe superar 100 caracteres")
    String etiquetaPregunta,

    String opcionesRespuesta,

    @NotNull(message = "El orden es obligatorio")
    @PositiveOrZero(message = "El orden debe ser mayor o igual a cero")
    Integer orden,

    boolean obligatorio
) {}
