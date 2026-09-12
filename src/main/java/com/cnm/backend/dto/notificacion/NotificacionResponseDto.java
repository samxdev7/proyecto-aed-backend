package com.cnm.backend.dto.notificacion;

import java.time.ZonedDateTime;

/**
 * DTO de salida para notificaciones del usuario (Endpoints F1, F2).
 * Derivado de RF8, RF9.
 */
public record NotificacionResponseDto(
    Long idNotificacion,
    Long idUsuario,
    String tipo,
    String mensaje,
    ZonedDateTime fechaEnvio,
    boolean leida
) {}
