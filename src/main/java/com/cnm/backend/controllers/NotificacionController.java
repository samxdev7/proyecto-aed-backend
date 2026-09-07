package com.cnm.backend.controllers;

import com.cnm.backend.dto.common.PageResponseDto;
import com.cnm.backend.dto.notificacion.NotificacionResponseDto;
import com.cnm.backend.dto.notificacion.SuscripcionPushRequestDto;
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

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Controlador REST para el Módulo de Notificaciones.
 * Cubre los endpoints F1-F3 del Catálogo de Endpoints (Derivado de RF8, RF9).
 */
@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    /**
     * F1: GET /notificaciones/me [Cliente, Administrador]
     * Lista paginada de notificaciones recibidas por el usuario autenticado (RF8, RF9).
     */
    @GetMapping("/me")
    public ResponseEntity<PageResponseDto<NotificacionResponseDto>> listarMisNotificaciones(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        NotificacionResponseDto notif1 = new NotificacionResponseDto(
                1L, 1L, "nuevo_viaje",
                "¡Nuevo viaje publicado! Ascenso al Volcán Telica ya tiene inscripciones abiertas.",
                ZonedDateTime.now().minusDays(1), true
        );
        NotificacionResponseDto notif2 = new NotificacionResponseDto(
                2L, 1L, "pocos_cupos",
                "Quedan únicamente 2 cupos para la expedición al Cañón de Somoto.",
                ZonedDateTime.now().minusHours(3), false
        );
        PageResponseDto<NotificacionResponseDto> response = new PageResponseDto<>(
                List.of(notif1, notif2), page, size, 2L, 1
        );
        return ResponseEntity.ok(response);
    }

    /**
     * F2: PATCH /notificaciones/{idNotificacion}/leida [Cliente, Administrador]
     * Marca una notificación como leída (RF8, RF9).
     */
    @PatchMapping("/{idNotificacion}/leida")
    public ResponseEntity<NotificacionResponseDto> marcarComoLeida(
            @PathVariable Long idNotificacion) {
        NotificacionResponseDto notificacionLeida = new NotificacionResponseDto(
                idNotificacion, 1L, "pocos_cupos",
                "Quedan únicamente 2 cupos para la expedición al Cañón de Somoto.",
                ZonedDateTime.now().minusHours(3), true
        );
        return ResponseEntity.ok(notificacionLeida);
    }

    /**
     * F3: POST /notificaciones/suscripcion [Cliente, Administrador]
     * Registra la suscripción push del navegador del usuario para Web Push API (RF8).
     */
    @PostMapping("/suscripcion")
    public ResponseEntity<Void> registrarSuscripcionPush(
            @Valid @RequestBody SuscripcionPushRequestDto request) {
        // En Service se almacena la clave p256dh y auth asociada al idUsuario autenticado.
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
