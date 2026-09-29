package com.cnm.backend.controllers;

import com.cnm.backend.dto.common.PageResponseDto;
import com.cnm.backend.dto.notificacion.NotificacionResponseDto;
import com.cnm.backend.dto.notificacion.SuscripcionPushRequestDto;
import com.cnm.backend.entity.Usuario;
import com.cnm.backend.service.NotificacionService;
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
 * Controlador REST para el Módulo de Notificaciones.
 * Cubre los endpoints F1-F3 del Catálogo de Endpoints (Derivado de RF8, RF9).
 */
@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    /**
     * F1: GET /notificaciones/me [Cliente, Administrador]
     * Lista paginada de notificaciones recibidas por el usuario autenticado (RF8, RF9).
     */
    @GetMapping("/me")
    public ResponseEntity<PageResponseDto<NotificacionResponseDto>> listarMisNotificaciones(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificacionService.listarMisNotificaciones(usuario.getIdUsuario(), page, size));
    }

    /**
     * F2: PATCH /notificaciones/{idNotificacion}/leida [Cliente, Administrador]
     * Marca una notificación como leída (RF8, RF9).
     */
    @PatchMapping("/{idNotificacion}/leida")
    public ResponseEntity<NotificacionResponseDto> marcarComoLeida(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idNotificacion) {
        return ResponseEntity.ok(notificacionService.marcarComoLeida(usuario.getIdUsuario(), idNotificacion));
    }

    /**
     * F3: POST /notificaciones/suscripcion [Cliente, Administrador]
     * Registra la suscripción push del navegador del usuario para Web Push API (RF8).
     */
    @PostMapping("/suscripcion")
    public ResponseEntity<Void> registrarSuscripcionPush(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody SuscripcionPushRequestDto request) {
        notificacionService.registrarSuscripcionPush(usuario.getIdUsuario(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
