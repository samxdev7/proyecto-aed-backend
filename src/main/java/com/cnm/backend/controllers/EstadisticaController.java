package com.cnm.backend.controllers;

import com.cnm.backend.dto.estadistica.EstadisticasPanelResponseDto;
import com.cnm.backend.dto.estadistica.InscritosPorViajeDto;
import com.cnm.backend.dto.estadistica.ResumenCuposDto;
import com.cnm.backend.dto.estadistica.RutaPopularDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para el Módulo de Estadísticas.
 * Cubre el endpoint G1 del Catálogo de Endpoints (Derivado de RF6).
 */
@RestController
@RequestMapping("/estadisticas")
public class EstadisticaController {

    /**
     * G1: GET /estadisticas/panel [Administrador]
     * Devuelve los indicadores del panel administrativo: inscritos por viaje,
     * rutas más populares y cupos reservados vs disponibles (RF6).
     */
    @GetMapping("/panel")
    public ResponseEntity<EstadisticasPanelResponseDto> obtenerEstadisticasPanel() {
        List<InscritosPorViajeDto> inscritos = List.of(
                new InscritosPorViajeDto(1L, "Volcán Telica - Campamento", 15, 20, 75.0),
                new InscritosPorViajeDto(2L, "Cañón de Somoto - Aventura Acuática", 18, 20, 90.0)
        );

        List<RutaPopularDto> populares = List.of(
                new RutaPopularDto(2L, "Cañón de Somoto - Aventura Acuática", "Media", 45),
                new RutaPopularDto(1L, "Volcán Telica - Campamento", "Media", 38)
        );

        ResumenCuposDto cupos = new ResumenCuposDto(100, 78, 22);

        EstadisticasPanelResponseDto panel = new EstadisticasPanelResponseDto(
                inscritos, populares, cupos
        );

        return ResponseEntity.ok(panel);
    }
}
