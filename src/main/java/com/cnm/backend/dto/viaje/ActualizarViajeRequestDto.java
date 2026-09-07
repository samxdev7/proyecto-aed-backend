package com.cnm.backend.dto.viaje;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * DTO de entrada para reemplazo completo de la información de un viaje (Endpoint C4 - PUT /viajes/{idViaje}).
 * Derivado de RF2.
 */
public record ActualizarViajeRequestDto(
    @NotBlank(message = "El título del viaje es obligatorio")
    @Size(max = 200, message = "El título no debe superar 200 caracteres")
    String titulo,

    String descripcion,
    String itinerario,

    @NotBlank(message = "La dificultad es obligatoria")
    @Pattern(regexp = "^(Baja|Media|Alta|Extrema)$", message = "La dificultad debe ser 'Baja', 'Media', 'Alta' o 'Extrema'")
    String dificultad,

    @NotNull(message = "La fecha y hora de ida es obligatoria")
    ZonedDateTime fechaHoraIda,

    @NotNull(message = "La fecha y hora de vuelta es obligatoria")
    ZonedDateTime fechaHoraVuelta,

    @NotBlank(message = "El punto de encuentro es obligatorio")
    String puntoEncuentro,

    String inclusionesAdicionales,

    @NotNull(message = "El monto total es obligatorio")
    @Positive(message = "El monto total debe ser mayor a cero")
    BigDecimal montoTotal,

    @NotNull(message = "El monto de reserva es obligatorio")
    @PositiveOrZero(message = "El monto de reserva debe ser mayor o igual a cero")
    BigDecimal montoReserva,

    @NotNull(message = "La cantidad de cupos máximos es obligatoria")
    @Positive(message = "Los cupos máximos deben ser mayores a cero")
    Integer cuposMaximos,

    String enlaceWhatsApp
) {}
