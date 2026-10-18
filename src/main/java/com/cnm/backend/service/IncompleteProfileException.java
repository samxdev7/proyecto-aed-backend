package com.cnm.backend.service;

import java.util.List;

/**
 * Lanzada cuando un usuario con perfil incompleto intenta realizar una acción
 * que requiere datos completos (p. ej. crear una reserva).
 * <p>
 * Contiene la lista de campos faltantes para que el controlador o el
 * manejador global de excepciones devuelva un JSON estructurado al frontend.
 */
public class IncompleteProfileException extends RuntimeException {

    private final List<String> requiredFieldsMissing;

    public IncompleteProfileException(List<String> requiredFieldsMissing) {
        super("Debes completar tu perfil antes de reservar un cupo.");
        this.requiredFieldsMissing = requiredFieldsMissing;
    }

    public List<String> getRequiredFieldsMissing() {
        return requiredFieldsMissing;
    }
}
