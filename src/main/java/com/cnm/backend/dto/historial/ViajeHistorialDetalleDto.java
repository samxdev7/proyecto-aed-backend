package com.cnm.backend.dto.historial;

/**
 * Sub-DTO de viaje dentro de HistorialReservaDetalleDto.
 * Basado en Arquitectura_API_CNM.docx Sección 5.3.
 */
public record ViajeHistorialDetalleDto(
    Long idViaje,
    String titulo
) {}
