package com.cnm.backend.dto.estadistica;

/**
 * Resumen global de cupos reservados vs disponibles.
 * Derivado de RF6.
 */
public record ResumenCuposDto(
    int totalCuposOfrecidos,
    int totalCuposReservados,
    int totalCuposDisponibles
) {}
