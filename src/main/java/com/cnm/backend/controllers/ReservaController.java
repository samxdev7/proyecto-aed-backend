package com.cnm.backend.controllers;

import com.cnm.backend.dto.common.PageResponseDto;
import com.cnm.backend.dto.reserva.CrearReservaRequestDto;
import com.cnm.backend.dto.reserva.RechazarReservaRequestDto;
import com.cnm.backend.dto.reserva.ReservaAdminResumenDto;
import com.cnm.backend.dto.reserva.ReservaDetalleDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Controlador REST para el Módulo de Reservas.
 * Núcleo del sistema: cubre los endpoints E1-E5 del Catálogo de Endpoints (Derivado de RF1, RF4, RF9, RF10, RF11).
 */
@RestController
@RequestMapping("/reservas")
public class ReservaController {

    /**
     * E1: POST /reservas [Cliente]
     * Crea una reserva completa en un solo envío: datos de reserva, acompañantes,
     * respuestas de formulario y comprobante de pago bancario (RF1, Historia 9).
     */
    @PostMapping
    public ResponseEntity<ReservaDetalleDto> crearReserva(
            @Valid @RequestBody CrearReservaRequestDto request) {
        // En Service se valida bloqueo transaccional si cupos <= 2 (RNF6) y no-reincidencia si fue rechazado (RF9).
        ReservaDetalleDto reservaCreada = new ReservaDetalleDto(
                500L,
                1L,
                "Carlos Alberto González López",
                "carlos@example.com",
                request.idViaje(),
                "Volcán Telica - Ascenso y Campamento Nocturno",
                null,
                "pendiente",
                new BigDecimal("400.00"),
                ZonedDateTime.now(),
                ZonedDateTime.now().plusMinutes(30),
                ZonedDateTime.now(),
                request.numeroReferenciaPago(),
                request.capturaComprobanteUrl(),
                null,
                null,
                false,
                request.acompanantes() != null ? request.acompanantes() : Collections.emptyList(),
                request.respuestasFormulario() != null ? request.respuestasFormulario() : Collections.emptyList()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaCreada);
    }

    /**
     * E2: GET /reservas/{idReserva} [Cliente (dueño), Administrador]
     * Consulta el detalle completo de una reserva específica (RF1, RF4).
     */
    @GetMapping("/{idReserva}")
    public ResponseEntity<ReservaDetalleDto> obtenerReservaPorId(
            @PathVariable Long idReserva) {
        ReservaDetalleDto detalle = new ReservaDetalleDto(
                idReserva,
                1L,
                "Carlos Alberto González López",
                "carlos@example.com",
                1L,
                "Volcán Telica - Ascenso y Campamento Nocturno",
                2L,
                "aprobada",
                new BigDecimal("400.00"),
                ZonedDateTime.now().minusDays(3),
                ZonedDateTime.now().minusDays(3).plusMinutes(30),
                ZonedDateTime.now().minusDays(3).plusMinutes(10),
                "REF-BAC-987654321",
                "https://storage.cnm.org.ni/comprobantes/ref987654321.png",
                null,
                ZonedDateTime.now().minusDays(2),
                false,
                Collections.emptyList(),
                Collections.emptyList()
        );
        return ResponseEntity.ok(detalle);
    }

    /**
     * E3: GET /reservas [Administrador]
     * Lista reservas paginadas para la bandeja de revisión, filtrables por estado y viaje (RF4).
     */
    @GetMapping
    public ResponseEntity<PageResponseDto<ReservaAdminResumenDto>> listarReservas(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long idViaje,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ReservaAdminResumenDto mock = new ReservaAdminResumenDto(
                500L,
                idViaje != null ? idViaje : 1L,
                "Volcán Telica - Ascenso y Campamento Nocturno",
                1L,
                "Carlos Alberto González López",
                "carlos@example.com",
                estado != null ? estado : "pendiente",
                new BigDecimal("400.00"),
                ZonedDateTime.now().minusHours(1),
                ZonedDateTime.now().minusHours(1).plusMinutes(30),
                ZonedDateTime.now().minusHours(1).plusMinutes(15),
                "REF-BAC-987654321",
                null,
                0
        );
        PageResponseDto<ReservaAdminResumenDto> response = new PageResponseDto<>(
                List.of(mock), page, size, 1L, 1
        );
        return ResponseEntity.ok(response);
    }

    /**
     * E4: PATCH /reservas/{idReserva}/aprobar [Administrador]
     * Aprueba una reserva pendiente, dispara correo de confirmación con enlace de WhatsApp (RF10, RF11)
     * y notificación web (RF9).
     */
    @PatchMapping("/{idReserva}/aprobar")
    public ResponseEntity<ReservaDetalleDto> aprobarReserva(@PathVariable Long idReserva) {
        ReservaDetalleDto reservaAprobada = new ReservaDetalleDto(
                idReserva,
                1L,
                "Carlos Alberto González López",
                "carlos@example.com",
                1L,
                "Volcán Telica - Ascenso y Campamento Nocturno",
                2L,
                "aprobada",
                new BigDecimal("400.00"),
                ZonedDateTime.now().minusHours(2),
                ZonedDateTime.now().minusHours(2).plusMinutes(30),
                ZonedDateTime.now().minusHours(2).plusMinutes(12),
                "REF-BAC-987654321",
                "https://storage.cnm.org.ni/comprobantes/ref987654321.png",
                null,
                ZonedDateTime.now(),
                false,
                Collections.emptyList(),
                Collections.emptyList()
        );
        return ResponseEntity.ok(reservaAprobada);
    }

    /**
     * E5: PATCH /reservas/{idReserva}/rechazar [Administrador]
     * Rechaza una reserva pendiente con justificación obligatoria y activa la regla de no-reincidencia
     * para usuario y acompañantes sobre este viaje (RF4, RF9).
     */
    @PatchMapping("/{idReserva}/rechazar")
    public ResponseEntity<ReservaDetalleDto> rechazarReserva(
            @PathVariable Long idReserva,
            @Valid @RequestBody RechazarReservaRequestDto request) {
        ReservaDetalleDto reservaRechazada = new ReservaDetalleDto(
                idReserva,
                1L,
                "Carlos Alberto González López",
                "carlos@example.com",
                1L,
                "Volcán Telica - Ascenso y Campamento Nocturno",
                2L,
                "rechazada",
                new BigDecimal("400.00"),
                ZonedDateTime.now().minusHours(2),
                ZonedDateTime.now().minusHours(2).plusMinutes(30),
                ZonedDateTime.now().minusHours(2).plusMinutes(12),
                "REF-BAC-987654321",
                "https://storage.cnm.org.ni/comprobantes/ref987654321.png",
                request.motivoRechazo(),
                ZonedDateTime.now(),
                false,
                Collections.emptyList(),
                Collections.emptyList()
        );
        return ResponseEntity.ok(reservaRechazada);
    }
}
