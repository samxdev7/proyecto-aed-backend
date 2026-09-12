package com.cnm.backend.dto.reserva;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * DTO de salida para la bandeja de administración de reservas (Endpoint E3 - GET /reservas).
 * Derivado de RF4.
 */
public record ReservaAdminResumenDto(
    Long idReserva,
    Long idViaje,
    String tituloViaje,
    Long idUsuario,
    String nombreUsuario,
    String correoUsuario,
    String estado,
    BigDecimal montoReserva,
    ZonedDateTime fechaReserva,
    ZonedDateTime fechaLimitePago,
    ZonedDateTime fechaPago,
    String numeroReferenciaPago,
    ZonedDateTime fechaRevision,
    int totalAcompanantes
) {}
