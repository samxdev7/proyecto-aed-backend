package com.cnm.backend.dto.common;

import java.util.List;

/**
 * Sobre de paginación estándar para listados de la API REST del CNM.
 * Basado en Arquitectura_API_CNM.docx Sección 5.3.
 */
public record PageResponseDto<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages
) {}
