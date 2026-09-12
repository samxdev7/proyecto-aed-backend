package com.cnm.backend.dto.viaje;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * DTO de salida para ficha detallada de un viaje (Endpoint C2 - GET /viajes/{idViaje}).
 * Derivado de RF3. Incluye itinerario completo, inclusiones y metadatos.
 */
public record ViajeDetalleDto(
    Long idViaje,
    Long idAdministradorCreador,
    String titulo,
    String descripcion,
    String itinerario,
    String dificultad,
    ZonedDateTime fechaHoraIda,
    ZonedDateTime fechaHoraVuelta,
    String puntoEncuentro,
    String inclusionesAdicionales,
    BigDecimal montoTotal,
    BigDecimal montoReserva,
    Integer cuposMaximos,
    Integer cuposDisponibles,
    String enlaceWhatsApp,
    String estado,
    ZonedDateTime fechaCreacion
) {}
