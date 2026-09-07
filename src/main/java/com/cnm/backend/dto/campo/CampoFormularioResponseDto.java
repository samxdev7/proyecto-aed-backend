package com.cnm.backend.dto.campo;

/**
 * DTO de salida para listar o consultar campos de formulario de un viaje (Endpoints D1, D2, D3).
 * Derivado de RF7. Utilizado por el frontend para renderizar dinámicamente el formulario.
 */
public record CampoFormularioResponseDto(
    Long idCampo,
    Long idViaje,
    String tipoCampo,
    String etiquetaPregunta,
    String opcionesRespuesta,
    Integer orden,
    boolean obligatorio
) {}
