package com.cnm.backend.controllers;

import com.cnm.backend.dto.common.PageResponseDto;
import com.cnm.backend.dto.historial.HistorialReservaDetalleDto;
import com.cnm.backend.dto.historial.HistorialReservaResumenDto;
import com.cnm.backend.dto.usuario.ActualizarNotificacionesRequestDto;
import com.cnm.backend.dto.usuario.ActualizarPerfilRequestDto;
import com.cnm.backend.dto.usuario.UsuarioPerfilResponseDto;
import com.cnm.backend.entity.Usuario;
import com.cnm.backend.service.UsuarioService;
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

/**
 * Controlador REST para el Módulo de Usuarios e Historial de Usuarios.
 * Cubre los endpoints B1-B5 del Módulo de Usuarios y H1-H4 del Contrato REST de Historial.
 * Derivado de RF5, RNF8 y Arquitectura_API_CNM.docx Sección 5.
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * B1: GET /usuarios/me [Cliente, Administrador]
     * Obtiene los datos del perfil del usuario autenticado para autocompletar formularios (RNF8).
     */
    @GetMapping("/me")
    public ResponseEntity<UsuarioPerfilResponseDto> obtenerMiPerfil(
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(usuarioService.obtenerPerfil(usuario.getIdUsuario()));
    }

    /**
     * B2: PATCH /usuarios/me [Cliente, Administrador]
     * Actualiza uno o varios campos del perfil sin reenviar el objeto completo (RNF8).
     */
    @PatchMapping("/me")
    public ResponseEntity<UsuarioPerfilResponseDto> actualizarMiPerfil(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody ActualizarPerfilRequestDto request) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(usuario.getIdUsuario(), request));
    }

    /**
     * B3: PATCH /usuarios/me/notificaciones [Cliente, Administrador]
     * Habilita o deshabilita notificaciones del navegador para el usuario autenticado (RF8).
     */
    @PatchMapping("/me/notificaciones")
    public ResponseEntity<UsuarioPerfilResponseDto> actualizarNotificaciones(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody ActualizarNotificacionesRequestDto request) {
        return ResponseEntity.ok(usuarioService.actualizarNotificaciones(usuario.getIdUsuario(), request));
    }

    /**
     * B4: GET /usuarios/{idUsuario} [Administrador]
     * Consulta los datos de un usuario específico durante la revisión administrativa de reservas (RF4).
     */
    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioPerfilResponseDto> obtenerUsuarioPorId(
            @PathVariable Long idUsuario) {
        return ResponseEntity.ok(usuarioService.obtenerPerfil(idUsuario));
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
        return ResponseEntity.ok(usuarioService.listarUsuarios(rol, page, size));
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
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(usuarioService.obtenerHistorial(usuario.getIdUsuario(), estado, page, size));
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
        return ResponseEntity.ok(usuarioService.obtenerHistorial(idUsuario, estado, page, size));
    }

    /**
     * H3: GET /usuarios/me/historial/{idReserva} [Cliente, Administrador]
     * Detalle completo de una reserva del historial del usuario autenticado.
     */
    @GetMapping("/me/historial/{idReserva}")
    public ResponseEntity<HistorialReservaDetalleDto> obtenerMiDetalleHistorial(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long idReserva) {
        return ResponseEntity.ok(usuarioService.obtenerDetalleHistorial(usuario.getIdUsuario(), idReserva, false));
    }

    /**
     * H4: GET /usuarios/{idUsuario}/historial/{idReserva} [Administrador]
     * Detalle completo de una reserva del historial de un usuario específico.
     */
    @GetMapping("/{idUsuario}/historial/{idReserva}")
    public ResponseEntity<HistorialReservaDetalleDto> obtenerDetalleHistorialUsuario(
            @PathVariable Long idUsuario,
            @PathVariable Long idReserva) {
        return ResponseEntity.ok(usuarioService.obtenerDetalleHistorial(idUsuario, idReserva, true));
    }
}
