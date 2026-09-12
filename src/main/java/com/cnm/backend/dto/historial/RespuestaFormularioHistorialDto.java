package com.cnm.backend.dto.historial;

/**
 * Sub-DTO para respuestas de formulario dentro del detalle del historial de reservas.
 * Basado en Arquitectura_API_CNM.docx Sección 5.3.
 */
public record RespuestaFormularioHistorialDto(
    String pregunta,
    String respuesta
) {}
