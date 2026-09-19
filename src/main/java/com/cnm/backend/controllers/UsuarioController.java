package com.cnm.backend.controllers;

import com.cnm.backend.dto.common.PageResponseDto;
import com.cnm.backend.dto.historial.HistorialReservaDetalleDto;
import com.cnm.backend.dto.historial.HistorialReservaResumenDto;
import com.cnm.backend.dto.historial.ViajeHistorialDetalleDto;
import com.cnm.backend.dto.historial.ViajeHistorialResumenDto;
import com.cnm.backend.dto.usuario.ActualizarNotificacionesRequestDto;
import com.cnm.backend.dto.usuario.ActualizarPerfilRequestDto;
import com.cnm.backend.dto.usuario.UsuarioPerfilResponseDto;
import com.cnm.backend.entity.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Controlador REST para el Módulo de Usuarios e Historial de Usuarios.
 * Cubre los endpoints B1-B5 del Módulo de Usuarios y H1-H4 del Contrato REST de Historial.
 * Derivado de RF5, RNF8 y Arquitectura_API_CNM.docx Sección 5.
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    /**
     * B1: GET /usuarios/me [Cliente, Administrador]
     * Obtiene los datos del perfil del usuario autenticado para autocompletar formularios (RNF8).
     */
    @GetMapping("/me")
    public ResponseEntity<UsuarioPerfilResponseDto> obtenerMiPerfil(
            @AuthenticationPrincipal Usuario usuario) {
        UsuarioPerfilResponseDto perfil = new UsuarioPerfilResponseDto(
                usuario.getIdUsuario(),
                usuario.getPrimerNombre(),
                usuario.getSegundoNombre(),
                usuario.getPrimerApellido(),
                usuario.getSegundoApellido(),
                usuario.getNombreCompleto(),
                usuario.getCorreo(),
                usuario.getRol().name(),
                usuario.getTelefono(),
                usuario.getSexo(),
                usuario.getNacionalidad(),
                usuario.getTipoIdentificacion(),
                usuario.getNumeroIdentificacion(),
                usuario.isNotificacionesHabilitadas(),
                usuario.getFechaRegistro()
        );
        return ResponseEntity.ok(perfil);
    }

    /**
     * B2: PATCH /usuarios/me [Cliente, Administrador]
     * Actualiza uno o varios campos del perfil sin reenviar el objeto completo (RNF8).
     */
    @PatchMapping("/me")
    public ResponseEntity<UsuarioPerfilResponseDto> actualizarMiPerfil(
            @Valid @RequestBody ActualizarPerfilRequestDto request) {
        UsuarioPerfilResponseDto perfilActualizado = new UsuarioPerfilResponseDto(
                1L,
                request.primerNombre() != null ? request.primerNombre() : "Carlos",
                request.segundoNombre(),
                request.primerApellido() != null ? request.primerApellido() : "González",
                request.segundoApellido(),
                UsuarioPerfilResponseDto.construirNombreCompleto(
                        request.primerNombre() != null ? request.primerNombre() : "Carlos",
                        request.segundoNombre(),
                        request.primerApellido() != null ? request.primerApellido() : "González",
                        request.segundoApellido()),
                "carlos@example.com", "cliente",
                request.telefono(), request.sexo(), request.nacionalidad(),
                request.tipoIdentificacion(), request.numeroIdentificacion(),
                true, ZonedDateTime.now()
        );
        return ResponseEntity.ok(perfilActualizado);
    }

    /**
     * B3: PATCH /usuarios/me/notificaciones [Cliente, Administrador]
     * Habilita o deshabilita notificaciones del navegador para el usuario autenticado (RF8).
     */
    @PatchMapping("/me/notificaciones")
    public ResponseEntity<UsuarioPerfilResponseDto> actualizarNotificaciones(
            @Valid @RequestBody ActualizarNotificacionesRequestDto request) {
        UsuarioPerfilResponseDto perfil = new UsuarioPerfilResponseDto(
                1L, "Carlos", "Alberto", "González", "López",
                "Carlos Alberto González López", "carlos@example.com", "cliente",
                "+505 8888-9999", "M", "Nicaragüense", "cedula",
                "001-150890-0001A", request.notificacionesHabilitadas(), ZonedDateTime.now()
        );
        return ResponseEntity.ok(perfil);
    }

    /**
     * B4: GET /usuarios/{idUsuario} [Administrador]
     * Consulta los datos de un usuario específico durante la revisión administrativa de reservas (RF4).
     */
    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioPerfilResponseDto> obtenerUsuarioPorId(
            @PathVariable Long idUsuario) {
        UsuarioPerfilResponseDto perfil = new UsuarioPerfilResponseDto(
                idUsuario, "Juan", "José", "Pérez", "Martínez",
                "Juan José Pérez Martínez", "juan@example.com", "cliente",
                "+505 8777-6655", "M", "Nicaragüense", "cedula",
                "001-200195-0003B", true, ZonedDateTime.now()
        );
        return ResponseEntity.ok(perfil);
    }

    /**
     * B5: GET /usuarios [Administrador]
     * Lista paginada de usuarios registrados, con filtro opcional por rol.
     */
    @GetMapping
    public ResponseEntity<PageResponseDto<UsuarioPerfilResponseDto>> listarUsuarios(
            @RequestParam(required = false) String rol,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UsuarioPerfilResponseDto mock = new UsuarioPerfilResponseDto(
                1L, "Carlos", "Alberto", "González", "López",
                "Carlos Alberto González López", "carlos@example.com", rol != null ? rol : "cliente",
                "+505 8888-9999", "M", "Nicaragüense", "cedula",
                "001-150890-0001A", true, ZonedDateTime.now()
        );
        PageResponseDto<UsuarioPerfilResponseDto> response = new PageResponseDto<>(
                List.of(mock), page, size, 1L, 1
        );
        return ResponseEntity.ok(response);
    }

    // =========================================================================
    // CONTRATO REST — MÓDULO DE HISTORIAL DE USUARIOS (Endpoints H1 - H4)
    // Basado en Arquitectura_API_CNM.docx Sección 5
    // =========================================================================

    /**
     * H1: GET /usuarios/me/historial [Cliente, Administrador]
     * Historial paginado de reservas del usuario autenticado (filtrable por estado).
     */
    @GetMapping("/me/historial")
    public ResponseEntity<PageResponseDto<HistorialReservaResumenDto>> obtenerMiHistorial(
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        HistorialReservaResumenDto mock = new HistorialReservaResumenDto(
                1042L,
                new ViajeHistorialResumenDto(58L, "Cerro Mogotón - Ruta Norte", "Alta", ZonedDateTime.now()),
                estado != null ? estado.toUpperCase() : "APROBADA",
                new BigDecimal("450.00"),
                ZonedDateTime.now().minusDays(5),
                ZonedDateTime.now().minusDays(4)
        );
        PageResponseDto<HistorialReservaResumenDto> response = new PageResponseDto<>(
                List.of(mock), page, size, 1L, 1
        );
        return ResponseEntity.ok(response);
    }

    /**
     * H2: GET /usuarios/{idUsuario}/historial [Administrador]
     * Historial paginado de reservas de un usuario específico.
     */
    @GetMapping("/{idUsuario}/historial")
    public ResponseEntity<PageResponseDto<HistorialReservaResumenDto>> obtenerHistorialUsuario(
            @PathVariable Long idUsuario,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        HistorialReservaResumenDto mock = new HistorialReservaResumenDto(
                1042L,
                new ViajeHistorialResumenDto(58L, "Cerro Mogotón - Ruta Norte", "Alta", ZonedDateTime.now()),
                estado != null ? estado.toUpperCase() : "APROBADA",
                new BigDecimal("450.00"),
                ZonedDateTime.now().minusDays(5),
                ZonedDateTime.now().minusDays(4)
        );
        PageResponseDto<HistorialReservaResumenDto> response = new PageResponseDto<>(
                List.of(mock), page, size, 1L, 1
        );
        return ResponseEntity.ok(response);
    }

    /**
     * H3: GET /usuarios/me/historial/{idReserva} [Cliente, Administrador]
     * Detalle completo de una reserva del historial del usuario autenticado.
     */
    @GetMapping("/me/historial/{idReserva}")
    public ResponseEntity<HistorialReservaDetalleDto> obtenerMiDetalleHistorial(
            @PathVariable Long idReserva) {
        HistorialReservaDetalleDto detalle = new HistorialReservaDetalleDto(
                idReserva,
                "APROBADA",
                new ViajeHistorialDetalleDto(58L, "Cerro Mogotón - Ruta Norte"),
                new BigDecimal("450.00"),
                ZonedDateTime.now().minusDays(5),
                ZonedDateTime.now().minusDays(4),
                null,
                Collections.emptyList(),
                Collections.emptyList()
        );
        return ResponseEntity.ok(detalle);
    }

    /**
     * H4: GET /usuarios/{idUsuario}/historial/{idReserva} [Administrador]
     * Detalle completo de una reserva del historial de un usuario específico.
     */
    @GetMapping("/{idUsuario}/historial/{idReserva}")
    public ResponseEntity<HistorialReservaDetalleDto> obtenerDetalleHistorialUsuario(
            @PathVariable Long idUsuario,
            @PathVariable Long idReserva) {
        HistorialReservaDetalleDto detalle = new HistorialReservaDetalleDto(
                idReserva,
                "RECHAZADA",
                new ViajeHistorialDetalleDto(58L, "Cerro Mogotón - Ruta Norte"),
                new BigDecimal("450.00"),
                ZonedDateTime.now().minusDays(5),
                ZonedDateTime.now().minusDays(4),
                "Comprobante de pago no coincide con el monto de reserva.",
                List.of(new com.cnm.backend.dto.historial.AcompananteHistorialDto(
                        "Ana María Pérez Gómez", "cedula", "001-120395-0002X")),
                List.of(new com.cnm.backend.dto.historial.RespuestaFormularioHistorialDto(
                        "Contacto de emergencia", "+505 8888-1234"))
        );
        return ResponseEntity.ok(detalle);
    }
}
