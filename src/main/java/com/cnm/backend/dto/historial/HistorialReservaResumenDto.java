package com.cnm.backend.dto.historial;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * DTO de salida para ítem del listado de historial de reservas (Endpoints H1, H2).
 * Basado en Arquitectura_API_CNM.docx Sección 5.3.
 */
public record HistorialReservaResumenDto(
    Long idReserva,
    ViajeHistorialResumenDto viaje,
    String estado,
    BigDecimal montoReserva,
    ZonedDateTime fechaReserva,
    ZonedDateTime fechaRevision
) {}
