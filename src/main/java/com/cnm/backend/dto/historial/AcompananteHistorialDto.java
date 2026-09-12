package com.cnm.backend.dto.historial;

/**
 * Sub-DTO para acompañantes dentro del detalle del historial de reservas.
 * Basado en Arquitectura_API_CNM.docx Sección 5.3.
 */
public record AcompananteHistorialDto(
    String nombreCompleto,
    String tipoIdentificacion,
    String numeroIdentificacion
) {}
