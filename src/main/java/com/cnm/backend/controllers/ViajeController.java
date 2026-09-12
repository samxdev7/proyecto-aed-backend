package com.cnm.backend.controllers;

import com.cnm.backend.dto.common.PageResponseDto;
import com.cnm.backend.dto.viaje.ActualizarEstadoViajeRequestDto;
import com.cnm.backend.dto.viaje.ActualizarViajeRequestDto;
import com.cnm.backend.dto.viaje.CrearViajeRequestDto;
import com.cnm.backend.dto.viaje.ViajeDetalleDto;
import com.cnm.backend.dto.viaje.ViajeResumenDto;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Controlador REST para el Módulo de Viajes.
 * Cubre los endpoints C1-C6 del Catálogo de Endpoints (Derivado de RF2, RF3, Historia 1-5).
 */
@RestController
@RequestMapping("/viajes")
public class ViajeController {

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
        ViajeResumenDto mock = new ViajeResumenDto(
                1L,
                "Volcán Telica - Ascenso y Campamento Nocturno",
                "Aventura de 2 días explorando el cráter activo del volcán Telica.",
                dificultad != null ? dificultad : "Media",
                ZonedDateTime.now().plusDays(10),
                ZonedDateTime.now().plusDays(11),
                "Gasolinera Puma Las Mercedes, Managua",
                new BigDecimal("1200.00"),
                new BigDecimal("400.00"),
                20,
                15,
                estado != null ? estado : "activo"
        );
        PageResponseDto<ViajeResumenDto> response = new PageResponseDto<>(
                List.of(mock), page, size, 1L, 1
        );
        return ResponseEntity.ok(response);
    }

    /**
     * C2: GET /viajes/{idViaje} [Público]
     * Ficha detallada de un viaje: itinerario, cupos, precios, ruta GPS, inclusiones (RF3).
     */
    @GetMapping("/{idViaje}")
    public ResponseEntity<ViajeDetalleDto> obtenerViajePorId(@PathVariable Long idViaje) {
        ViajeDetalleDto detalle = new ViajeDetalleDto(
                idViaje,
                1L,
                "Volcán Telica - Ascenso y Campamento Nocturno",
                "Aventura de 2 días explorando el cráter activo del volcán Telica.",
                "Día 1: Salida 06:00 AM, llegada a faldas 10:00 AM, caminata 4h. Día 2: Amanecer y descenso.",
                "Media",
                ZonedDateTime.now().plusDays(10),
                ZonedDateTime.now().plusDays(11),
                "Gasolinera Puma Las Mercedes, Managua",
                "Transporte ida y vuelta, guía certificado, botiquín de primeros auxilios, café de bienvenida.",
                new BigDecimal("1200.00"),
                new BigDecimal("400.00"),
                20,
                15,
                "https://chat.whatsapp.com/mockInviteTelica2026",
                "activo",
                ZonedDateTime.now().minusDays(2)
        );
        return ResponseEntity.ok(detalle);
    }

    /**
     * C3: POST /viajes [Administrador]
     * Crea un nuevo viaje con todos sus atributos (RF2).
     */
    @PostMapping
    public ResponseEntity<ViajeDetalleDto> crearViaje(
            @Valid @RequestBody CrearViajeRequestDto request) {
        ViajeDetalleDto nuevoViaje = new ViajeDetalleDto(
                100L,
                1L,
                request.titulo(),
                request.descripcion(),
                request.itinerario(),
                request.dificultad(),
                request.fechaHoraIda(),
                request.fechaHoraVuelta(),
                request.puntoEncuentro(),
                request.inclusionesAdicionales(),
                request.montoTotal(),
                request.montoReserva(),
                request.cuposMaximos(),
                request.cuposMaximos(),
                request.enlaceWhatsApp(),
                "activo",
                ZonedDateTime.now()
        );
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
        ViajeDetalleDto viajeActualizado = new ViajeDetalleDto(
                idViaje,
                1L,
                request.titulo(),
                request.descripcion(),
                request.itinerario(),
                request.dificultad(),
                request.fechaHoraIda(),
                request.fechaHoraVuelta(),
                request.puntoEncuentro(),
                request.inclusionesAdicionales(),
                request.montoTotal(),
                request.montoReserva(),
                request.cuposMaximos(),
                request.cuposMaximos(),
                request.enlaceWhatsApp(),
                "activo",
                ZonedDateTime.now()
        );
        return ResponseEntity.ok(viajeActualizado);
    }

    /**
     * C5: PATCH /viajes/{idViaje}/estado [Administrador]
     * Cambia el estado del viaje entre "activo" y "cerrado" puntualmente (RF2).
     */
    @PatchMapping("/{idViaje}/estado")
    public ResponseEntity<ViajeDetalleDto> actualizarEstadoViaje(
            @PathVariable Long idViaje,
            @Valid @RequestBody ActualizarEstadoViajeRequestDto request) {
        ViajeDetalleDto viajeActualizado = new ViajeDetalleDto(
                idViaje,
                1L,
                "Volcán Telica - Ascenso y Campamento Nocturno",
                "Aventura de 2 días explorando el cráter activo.",
                "Itinerario...",
                "Media",
                ZonedDateTime.now().plusDays(10),
                ZonedDateTime.now().plusDays(11),
                "Gasolinera Puma Las Mercedes",
                "Transporte y guías",
                new BigDecimal("1200.00"),
                new BigDecimal("400.00"),
                20,
                15,
                "https://chat.whatsapp.com/mockInviteTelica2026",
                request.estado(),
                ZonedDateTime.now()
        );
        return ResponseEntity.ok(viajeActualizado);
    }

    /**
     * C6: DELETE /viajes/{idViaje} [Administrador]
     * Elimina un viaje, solo si no tiene reservas pendientes o aprobadas asociadas (RF2).
     */
    @DeleteMapping("/{idViaje}")
    public ResponseEntity<Void> eliminarViaje(@PathVariable Long idViaje) {
        // En Service se verifica que no existan reservas activas; si existen lanza conflicto 409.
        return ResponseEntity.noContent().build();
    }
}
