package com.cnm.backend.dto.notificacion;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para suscripción push del navegador (Endpoint F3 - POST /notificaciones/suscripcion).
 * Derivado de RF8 y Web Push API.
 */
public record SuscripcionPushRequestDto(
    @NotBlank(message = "El endpoint del servicio push es obligatorio")
    String endpoint,

    @NotBlank(message = "La clave p256dh de cifrado es obligatoria")
    String p256dhKey,

    @NotBlank(message = "La clave auth de autenticación es obligatoria")
    String authKey
) {}
