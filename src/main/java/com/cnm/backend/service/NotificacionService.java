package com.cnm.backend.service;

import com.cnm.backend.dto.common.PageResponseDto;
import com.cnm.backend.dto.notificacion.NotificacionResponseDto;
import com.cnm.backend.dto.notificacion.SuscripcionPushRequestDto;
import com.cnm.backend.entity.Notificacion;
import com.cnm.backend.entity.Usuario;
import com.cnm.backend.repository.NotificacionRepository;
import com.cnm.backend.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Servicio para el buzón de notificaciones y gestión de suscripciones push.
 * Implementa los casos de uso F1-F3 del Catálogo de Endpoints (Derivado de RF8, RF9).
 */
@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacionService(NotificacionRepository notificacionRepository,
                               UsuarioRepository usuarioRepository) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * F1: Lista paginada de notificaciones para el usuario autenticado, ordenadas cronológicamente.
     */
    @Transactional(readOnly = true)
    public PageResponseDto<NotificacionResponseDto> listarMisNotificaciones(Long idUsuario, int page, int size) {
        validarUsuario(idUsuario);
        Pageable pageable = PageRequest.of(page, size);
        Page<Notificacion> notificacionesPage = notificacionRepository.findByIdUsuarioOrderByFechaEnvioDesc(idUsuario, pageable);

        List<NotificacionResponseDto> items = notificacionesPage.getContent().stream()
                .map(this::mapToDto)
                .toList();

        return new PageResponseDto<>(items, page, size, notificacionesPage.getTotalElements(), notificacionesPage.getTotalPages());
    }

    /**
     * F2: Marca una notificación como leída asegurando que pertenezca al usuario autenticado.
     */
    @Transactional
    public NotificacionResponseDto marcarComoLeida(Long idUsuario, Long idNotificacion) {
        validarUsuario(idUsuario);
        Notificacion notificacion = notificacionRepository.findByIdNotificacionAndIdUsuario(idNotificacion, idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Notificación con ID " + idNotificacion + " no encontrada para este usuario"));

        notificacion.setLeida(true);
        Notificacion guardada = notificacionRepository.save(notificacion);
        return mapToDto(guardada);
    }

    /**
     * F3: Registra o renueva la suscripción push del navegador del usuario (Web Push API).
     */
    @Transactional
    public void registrarSuscripcionPush(Long idUsuario, SuscripcionPushRequestDto request) {
        Usuario usuario = validarUsuario(idUsuario);

        // Habilita las notificaciones en el perfil del usuario si no estaban activas
        if (!usuario.isNotificacionesHabilitadas()) {
            usuario.setNotificacionesHabilitadas(true);
            usuarioRepository.save(usuario);
        }
        // En una infraestructura push completa (VAPID/WebPush), aquí se persiste el endpoint y claves
    }

    /**
     * Utilidad de dominio para emitir nuevas notificaciones hacia usuarios (usado en ReservaService y ViajeService).
     */
    @Transactional
    public NotificacionResponseDto crearNotificacion(Long idUsuario, String tipo, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setIdUsuario(idUsuario);
        notificacion.setTipo(tipo != null ? tipo : "nuevo_viaje");
        notificacion.setMensaje(mensaje);
        notificacion.setFechaEnvio(ZonedDateTime.now());
        notificacion.setLeida(false);

        Notificacion guardada = notificacionRepository.save(notificacion);
        return mapToDto(guardada);
    }

    private Usuario validarUsuario(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuario con ID " + idUsuario + " no encontrado"));
    }

    private NotificacionResponseDto mapToDto(Notificacion n) {
        return new NotificacionResponseDto(
                n.getIdNotificacion(),
                n.getIdUsuario(),
                n.getTipo(),
                n.getMensaje(),
                n.getFechaEnvio(),
                Boolean.TRUE.equals(n.getLeida())
        );
    }
}
