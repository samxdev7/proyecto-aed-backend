package com.cnm.backend.dto.reserva;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * DTO de salida para consulta de detalle completo de una reserva (Endpoints E2, E4, E5).
 * Derivado de RF1, RF4, RF9.
 */
public record ReservaDetalleDto(
    Long idReserva,
    Long idUsuario,
    String nombreUsuario,
    String correoUsuario,
    Long idViaje,
    String tituloViaje,
    Long idAdministradorRevisor,
    String estado,
    BigDecimal montoReserva,
    ZonedDateTime fechaReserva,
    ZonedDateTime fechaLimitePago,
    ZonedDateTime fechaPago,
    String numeroReferenciaPago,
    String capturaComprobanteUrl,
    String motivoRechazo,
    ZonedDateTime fechaRevision,
    boolean editable,
    List<AcompananteDto> acompanantes,
    List<RespuestaFormularioDto> respuestasFormulario
) {}
