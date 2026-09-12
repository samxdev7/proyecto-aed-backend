package com.cnm.backend.dto.viaje;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * DTO de salida para el catálogo público de viajes (Endpoint C1 - GET /viajes).
 * Derivado de RF3. Proporciona los datos esenciales para la visualización en tarjetas/listados.
 */
public record ViajeResumenDto(
    Long idViaje,
    String titulo,
    String descripcion,
    String dificultad,
    ZonedDateTime fechaHoraIda,
    ZonedDateTime fechaHoraVuelta,
    String puntoEncuentro,
    BigDecimal montoTotal,
    BigDecimal montoReserva,
    Integer cuposMaximos,
    Integer cuposDisponibles,
    String estado
) {}
