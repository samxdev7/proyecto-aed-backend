package com.cnm.backend.dto.common;

import java.util.List;

/**
 * Respuesta de error estructurada para el caso de perfil de usuario incompleto.
 * El frontend usa el campo {@code code} ("INCOMPLETE_PROFILE") para decidir
 * si redirigir al usuario a completar su perfil antes de reservar.
 */
public record IncompleteProfileErrorDto(
    boolean success,
    String code,
    String message,
    List<String> requiredFieldsMissing
) {
    public IncompleteProfileErrorDto(List<String> requiredFieldsMissing) {
        this(false, "INCOMPLETE_PROFILE",
             "Debes completar tu perfil antes de reservar un cupo.",
             requiredFieldsMissing);
    }
}
