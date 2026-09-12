package com.cnm.backend.dto.estadistica;

import java.util.List;

/**
 * DTO de salida agregado para el panel administrativo de estadísticas (Endpoint G1 - GET /estadisticas/panel).
 * Derivado de RF6.
 */
public record EstadisticasPanelResponseDto(
    List<InscritosPorViajeDto> inscritosPorViaje,
    List<RutaPopularDto> rutasMasPopulares,
    ResumenCuposDto cuposReservados
) {}
