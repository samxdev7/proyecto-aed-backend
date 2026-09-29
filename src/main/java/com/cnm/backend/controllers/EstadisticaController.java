package com.cnm.backend.controllers;

import com.cnm.backend.dto.estadistica.EstadisticasPanelResponseDto;
import com.cnm.backend.service.EstadisticaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para el Módulo de Estadísticas.
 * Cubre el endpoint G1 del Catálogo de Endpoints (Derivado de RF6).
 */
@RestController
@RequestMapping("/estadisticas")
public class EstadisticaController {

    private final EstadisticaService estadisticaService;

    public EstadisticaController(EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    /**
     * G1: GET /estadisticas/panel [Administrador]
     * Devuelve los indicadores del panel administrativo: inscritos por viaje,
     * rutas más populares y cupos reservados vs disponibles (RF6).
     */
    @GetMapping("/panel")
    public ResponseEntity<EstadisticasPanelResponseDto> obtenerEstadisticasPanel() {
        return ResponseEntity.ok(estadisticaService.obtenerEstadisticasPanel());
    }
}
