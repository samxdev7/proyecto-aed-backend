package com.cnm.backend.dto.viaje;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO de entrada para cambiar el estado del viaje (Endpoint C5 - PATCH /viajes/{idViaje}/estado).
 * Derivado de RF2.
 */
public record ActualizarEstadoViajeRequestDto(
    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^(activo|cerrado)$", message = "El estado debe ser 'activo' o 'cerrado'")
    String estado
) {}
