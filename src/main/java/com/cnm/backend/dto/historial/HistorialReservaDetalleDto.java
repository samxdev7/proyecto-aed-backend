package com.cnm.backend.dto.historial;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * DTO de salida para el detalle completo de una reserva del historial (Endpoints H3, H4).
 * Basado en Arquitectura_API_CNM.docx Sección 5.3.
 */
public record HistorialReservaDetalleDto(
    Long idReserva,
    String estado,
    ViajeHistorialDetalleDto viaje,
    BigDecimal montoReserva,
    ZonedDateTime fechaReserva,
    ZonedDateTime fechaRevision,
    String motivoRechazo,
    List<AcompananteHistorialDto> acompanantes,
    List<RespuestaFormularioHistorialDto> respuestasFormulario
) {}
