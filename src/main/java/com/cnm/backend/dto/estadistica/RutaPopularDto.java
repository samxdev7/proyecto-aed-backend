package com.cnm.backend.dto.estadistica;

/**
 * Indicador de rutas más populares en el panel de estadísticas.
 * Derivado de RF6.
 */
public record RutaPopularDto(
    Long idViaje,
    String titulo,
    String dificultad,
    int totalReservasAprobadas
) {}
