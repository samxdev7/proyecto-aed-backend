package com.cnm.backend.dto.reserva;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para rechazo de reserva por administrador (Endpoint E5 - PATCH /reservas/{idReserva}/rechazar).
 * Derivado de RF4, RF9. Requiere justificación obligatoria del motivo de rechazo.
 */
public record RechazarReservaRequestDto(
    @NotBlank(message = "El motivo de rechazo es obligatorio para notificar formalmente al cliente")
    String motivoRechazo
) {}
