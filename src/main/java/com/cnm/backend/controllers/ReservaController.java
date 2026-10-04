package com.cnm.backend.controllers;

import com.cnm.backend.dto.common.PageResponseDto;
import com.cnm.backend.dto.reserva.CrearReservaRequestDto;
import com.cnm.backend.dto.reserva.RechazarReservaRequestDto;
import com.cnm.backend.dto.reserva.ReservaAdminResumenDto;
import com.cnm.backend.dto.reserva.ReservaDetalleDto;
import com.cnm.backend.entity.Rol;
import com.cnm.backend.entity.Usuario;
import com.cnm.backend.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para el Módulo de Reservas.
 * Núcleo del sistema: cubre los endpoints E1-E5 del Catálogo de Endpoints (Derivado de RF1, RF4, RF9, RF10, RF11).
 */
@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /**
     * E1: POST /reservas [Cliente]
     * Crea una reserva completa en un solo envío: datos de reserva, acompañantes,
     * respuestas de formulario y comprobante de pago bancario (RF1, Historia 9).
     */
    @PostMapping
    public ResponseEntity<ReservaDetalleDto> crearReserva(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody CrearReservaRequestDto request) {
        Long idUsuario = (usuario != null) ? usuario.getIdUsuario() : 1L;
        ReservaDetalleDto reservaCreada = reservaService.crearReserva(idUsuario, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaCreada);
    }

    /**
     * E2: GET /reservas/{idReserva} [Cliente (dueño), Administrador]
     * Consulta el detalle completo de una reserva específica (RF1, RF4).
     */
    @GetMapping("/{idReserva}")
    public ResponseEntity<ReservaDetalleDto> obtenerReservaPorId(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idReserva) {
        Long idUsuario = (usuario != null) ? usuario.getIdUsuario() : 1L;
        boolean esAdmin = usuario != null && Rol.administrador.equals(usuario.getRol());
        return ResponseEntity.ok(reservaService.obtenerReservaPorId(idReserva, idUsuario, esAdmin));
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
        return ResponseEntity.ok(reservaService.listarReservasAdmin(estado, idViaje, page, size));
    }

    /**
     * E4: PATCH /reservas/{idReserva}/aprobar [Administrador]
     * Aprueba una reserva pendiente, dispara correo de confirmación con enlace de WhatsApp (RF10, RF11)
     * y notificación web (RF9).
     */
    @PatchMapping("/{idReserva}/aprobar")
    public ResponseEntity<ReservaDetalleDto> aprobarReserva(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idReserva) {
        Long idAdmin = (usuario != null) ? usuario.getIdUsuario() : 1L;
        return ResponseEntity.ok(reservaService.aprobarReserva(idReserva, idAdmin));
    }

    /**
     * E5: PATCH /reservas/{idReserva}/rechazar [Administrador]
     * Rechaza una reserva pendiente con justificación obligatoria y activa la regla de no-reincidencia
     * para usuario y acompañantes sobre este viaje (RF4, RF9).
     */
    @PatchMapping("/{idReserva}/rechazar")
    public ResponseEntity<ReservaDetalleDto> rechazarReserva(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idReserva,
            @Valid @RequestBody RechazarReservaRequestDto request) {
        Long idAdmin = (usuario != null) ? usuario.getIdUsuario() : 1L;
        return ResponseEntity.ok(reservaService.rechazarReserva(idReserva, idAdmin, request));
    }
}
