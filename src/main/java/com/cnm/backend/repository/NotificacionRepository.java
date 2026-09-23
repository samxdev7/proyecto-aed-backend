package com.cnm.backend.repository;

import com.cnm.backend.entity.Notificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad Notificacion.
 * Gestiona el acceso al buzón de alertas y notificaciones del usuario (RF8, RF9).
 */
@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    /**
     * Lista paginada de notificaciones de un usuario ordenadas cronológicamente (Endpoint F1).
     */
    Page<Notificacion> findByIdUsuarioOrderByFechaEnvioDesc(Long idUsuario, Pageable pageable);

    /**
     * Cuenta cuántas notificaciones no leídas tiene un usuario para insignias de interfaz.
     */
    long countByIdUsuarioAndLeidaFalse(Long idUsuario);

    /**
     * Busca una notificación específica asegurando que pertenezca al usuario autenticado (Endpoint F2).
     */
    Optional<Notificacion> findByIdNotificacionAndIdUsuario(Long idNotificacion, Long idUsuario);
}
