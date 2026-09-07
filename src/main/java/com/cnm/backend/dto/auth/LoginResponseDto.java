package com.cnm.backend.dto.auth;

/**
 * DTO de respuesta para autenticación exitosa (Endpoint A2 - POST /auth/login).
 * Devuelve el token JWT y los datos de contexto del usuario sin exponer credenciales.
 */
public record LoginResponseDto(
    String token,
    String tipoToken,
    Long idUsuario,
    String correo,
    String nombreCompleto,
    String rol,
    boolean notificacionesHabilitadas
) {
    public LoginResponseDto(String token, Long idUsuario, String correo, String nombreCompleto, String rol, boolean notificacionesHabilitadas) {
        this(token, "Bearer", idUsuario, correo, nombreCompleto, rol, notificacionesHabilitadas);
    }
}
