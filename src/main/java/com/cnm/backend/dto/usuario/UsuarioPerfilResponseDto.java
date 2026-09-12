package com.cnm.backend.dto.usuario;

import java.time.ZonedDateTime;

/**
 * DTO de salida para consultar el perfil de usuario (Endpoints B1, B4, B5).
 * Derivado de RF5, RNF8. Oculta deliberadamente credenciales sensibles (hash).
 */
public record UsuarioPerfilResponseDto(
    Long idUsuario,
    String primerNombre,
    String segundoNombre,
    String primerApellido,
    String segundoApellido,
    String nombreCompleto,
    String correo,
    String rol,
    String telefono,
    String sexo,
    String nacionalidad,
    String tipoIdentificacion,
    String numeroIdentificacion,
    boolean notificacionesHabilitadas,
    ZonedDateTime fechaRegistro
) {
    public static String construirNombreCompleto(String primerNombre, String segundoNombre, String primerApellido, String segundoApellido) {
        StringBuilder sb = new StringBuilder();
        if (primerNombre != null) sb.append(primerNombre);
        if (segundoNombre != null && !segundoNombre.isBlank()) sb.append(" ").append(segundoNombre);
        if (primerApellido != null) sb.append(" ").append(primerApellido);
        if (segundoApellido != null && !segundoApellido.isBlank()) sb.append(" ").append(segundoApellido);
        return sb.toString().trim();
    }
}
