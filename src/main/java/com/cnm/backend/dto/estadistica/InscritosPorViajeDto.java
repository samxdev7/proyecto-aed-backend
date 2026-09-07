package com.cnm.backend.dto.estadistica;

/**
 * Indicador de inscritos por viaje en el panel de estadísticas.
 * Derivado de RF6.
 */
public record InscritosPorViajeDto(
    Long idViaje,
    String titulo,
    int totalInscritos,
    int cuposMaximos,
    double porcentajeOcupacion
) {}
