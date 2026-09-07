package com.cnm.backend.dto.historial;

import java.time.ZonedDateTime;

/**
 * Sub-DTO de viaje dentro de HistorialReservaResumenDto.
 * Basado en Arquitectura_API_CNM.docx Sección 5.3.
 */
public record ViajeHistorialResumenDto(
    Long idViaje,
    String titulo,
    String dificultad,
    ZonedDateTime fechaHoraIda
) {}
