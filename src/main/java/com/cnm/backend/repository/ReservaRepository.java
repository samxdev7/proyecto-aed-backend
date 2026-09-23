package com.cnm.backend.repository;

import com.cnm.backend.entity.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad Reserva.
 * Cubre operaciones de historial del usuario (H1, H3), bandeja de revisión admin (E2, E3, E4, E5)
 * y regla de no-reincidencia tras rechazo (RF9).
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /**
     * Historial de reservas de un cliente específico, paginado (Endpoints H1, H2).
     */
    Page<Reserva> findByIdUsuario(Long idUsuario, Pageable pageable);

    /**
     * Historial de reservas de un cliente filtrado por estado (Endpoints H1, H2 con filtro).
     */
    Page<Reserva> findByIdUsuarioAndEstado(Long idUsuario, String estado, Pageable pageable);

    /**
     * Bandeja administrativa: listar reservas filtrando por estado (Endpoint E3).
     */
    Page<Reserva> findByEstado(String estado, Pageable pageable);

    /**
     * Bandeja administrativa: listar reservas filtrando por viaje (Endpoint E3).
     */
    Page<Reserva> findByIdViaje(Long idViaje, Pageable pageable);

    /**
     * Bandeja administrativa: listar reservas filtrando por viaje y estado (Endpoint E3).
     */
    Page<Reserva> findByIdViajeAndEstado(Long idViaje, String estado, Pageable pageable);

    /**
     * Consulta una reserva verificando que pertenezca al usuario cliente (Endpoint E2, H3).
     */
    Optional<Reserva> findByIdReservaAndIdUsuario(Long idReserva, Long idUsuario);

    /**
     * Regla de negocio RF9: Verifica si un usuario ya tiene una reserva en estado 'rechazada'
     * para un viaje específico, impidiendo que vuelva a reservar para esa misma actividad.
     */
    boolean existsByIdUsuarioAndIdViajeAndEstado(Long idUsuario, Long idViaje, String estado);

    /**
     * Indicador para estadísticas (RF6): cuenta reservas según estado en un viaje.
     */
    long countByIdViajeAndEstado(Long idViaje, String estado);
}
