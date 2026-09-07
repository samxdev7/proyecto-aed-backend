package com.cnm.backend.dto.common;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Formato estándar de respuesta de error de la API REST del CNM.
 * Basado en Arquitectura_API_CNM.docx Sección 5.5.
 */
public record ErrorResponseDto(
    ZonedDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    Map<String, String> validationErrors
) {
    public ErrorResponseDto(int status, String error, String message, String path) {
        this(ZonedDateTime.now(), status, error, message, path, null);
    }

    public ErrorResponseDto(int status, String error, String message, String path, Map<String, String> validationErrors) {
        this(ZonedDateTime.now(), status, error, message, path, validationErrors);
    }
}
