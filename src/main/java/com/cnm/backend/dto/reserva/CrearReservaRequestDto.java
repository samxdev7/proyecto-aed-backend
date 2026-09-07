package com.cnm.backend.dto.reserva;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO de entrada para crear una reserva integral (Endpoint E1 - POST /reservas).
 * Derivado de RF1: formulario único con acompañantes, respuestas y comprobante de pago bancario.
 */
public record CrearReservaRequestDto(
    @NotNull(message = "El identificador del viaje es obligatorio")
    Long idViaje,

    @Valid
    List<AcompananteDto> acompanantes,

    @Valid
    List<RespuestaFormularioDto> respuestasFormulario,

    @NotBlank(message = "El número de referencia del comprobante de pago es obligatorio")
    @Size(max = 50, message = "El número de referencia no debe superar 50 caracteres")
    String numeroReferenciaPago,

    @NotBlank(message = "La URL de la captura del comprobante es obligatoria")
    String capturaComprobanteUrl
) {}
