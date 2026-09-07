package com.cnm.backend.dto.usuario;

import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para habilitar o deshabilitar notificaciones del navegador (Endpoint B3 - PATCH /usuarios/me/notificaciones).
 * Derivado de RF8.
 */
public record ActualizarNotificacionesRequestDto(
    @NotNull(message = "El estado de las notificaciones es obligatorio")
    Boolean notificacionesHabilitadas
) {}
