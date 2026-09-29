package com.cnm.backend.controllers;

import com.cnm.backend.dto.common.PageResponseDto;
import com.cnm.backend.dto.viaje.ActualizarEstadoViajeRequestDto;
import com.cnm.backend.dto.viaje.ActualizarViajeRequestDto;
import com.cnm.backend.dto.viaje.CrearViajeRequestDto;
import com.cnm.backend.dto.viaje.ViajeDetalleDto;
import com.cnm.backend.dto.viaje.ViajeResumenDto;
import com.cnm.backend.entity.Usuario;
import com.cnm.backend.service.ViajeService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

/**
 * Controlador REST para el Módulo de Viajes.
 * Cubre los endpoints C1-C6 del Catálogo de Endpoints (Derivado de RF2, RF3, Historia 1-5).
 */
@RestController
@RequestMapping("/viajes")
public class ViajeController {

    private final ViajeService viajeService;

    public ViajeController(ViajeService viajeService) {
        this.viajeService = viajeService;
    }

    /**
     * C1: GET /viajes [Público]
     * Catálogo público de viajes, paginado y filtrable por dificultad, fecha o estado (RF3).
     */
    @GetMapping
    public ResponseEntity<PageResponseDto<ViajeResumenDto>> listarViajes(
            @RequestParam(required = false) String dificultad,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime fechaDesde,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(viajeService.listarViajes(dificultad, fechaDesde, estado, page, size));
    }

    /**
     * C2: GET /viajes/{idViaje} [Público]
     * Ficha detallada de un viaje: itinerario, cupos, precios, ruta GPS, inclusiones (RF3).
     */
    @GetMapping("/{idViaje}")
    public ResponseEntity<ViajeDetalleDto> obtenerViajePorId(@PathVariable Long idViaje) {
        return ResponseEntity.ok(viajeService.obtenerViajePorId(idViaje));
    }

    /**
     * C3: POST /viajes [Administrador]
     * Crea un nuevo viaje con todos sus atributos (RF2).
     */
    @PostMapping
    public ResponseEntity<ViajeDetalleDto> crearViaje(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody CrearViajeRequestDto request) {
        Long idAdmin = (usuario != null) ? usuario.getIdUsuario() : 1L;
        ViajeDetalleDto nuevoViaje = viajeService.crearViaje(idAdmin, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoViaje);
    }

    /**
     * C4: PUT /viajes/{idViaje} [Administrador]
     * Reemplaza la información completa de un viaje existente (edición general, RF2).
     */
    @PutMapping("/{idViaje}")
    public ResponseEntity<ViajeDetalleDto> actualizarViaje(
            @PathVariable Long idViaje,
            @Valid @RequestBody ActualizarViajeRequestDto request) {
        return ResponseEntity.ok(viajeService.actualizarViaje(idViaje, request));
    }

    /**
     * C5: PATCH /viajes/{idViaje}/estado [Administrador]
     * Cambia el estado del viaje entre "activo" y "cerrado" puntualmente (RF2).
     */
    @PatchMapping("/{idViaje}/estado")
    public ResponseEntity<ViajeDetalleDto> actualizarEstadoViaje(
            @PathVariable Long idViaje,
            @Valid @RequestBody ActualizarEstadoViajeRequestDto request) {
        return ResponseEntity.ok(viajeService.actualizarEstadoViaje(idViaje, request));
    }

    /**
     * C6: DELETE /viajes/{idViaje} [Administrador]
     * Elimina un viaje, solo si no tiene reservas pendientes o aprobadas asociadas (RF2).
     */
    @DeleteMapping("/{idViaje}")
    public ResponseEntity<Void> eliminarViaje(@PathVariable Long idViaje) {
        viajeService.eliminarViaje(idViaje);
        return ResponseEntity.noContent().build();
    }
}
