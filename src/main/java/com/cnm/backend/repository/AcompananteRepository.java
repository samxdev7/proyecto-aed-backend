package com.cnm.backend.repository;

import com.cnm.backend.entity.Acompanante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad Acompanante.
 * Cubre la consulta de acompañantes por reserva (E2, H3, H4) y la verificación
 * de la regla de no-reincidencia tras rechazo de acompañantes en el mismo viaje (RF9).
 */
@Repository
public interface AcompananteRepository extends JpaRepository<Acompanante, Long> {

    /**
     * Lista todos los acompañantes registrados en una reserva (Endpoints E2, H3, H4).
     */
    List<Acompanante> findByIdReserva(Long idReserva);

    /**
     * Elimina todos los acompañantes de una reserva.
     */
    void deleteByIdReserva(Long idReserva);

    /**
     * Comprueba si un número de identificación de acompañante existe en el sistema.
     */
    boolean existsByNumeroIdentificacion(String numeroIdentificacion);

    /**
     * Regla de negocio RF9: Comprueba si un acompañante (por su número de identificación)
     * formó parte de una reserva que fue rechazada para un viaje determinado.
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM Acompanante a, Reserva r " +
           "WHERE a.idReserva = r.idReserva " +
           "AND a.numeroIdentificacion = :numeroIdentificacion " +
           "AND r.idViaje = :idViaje " +
           "AND r.estado = 'rechazada'")
    boolean existsAcompananteRechazadoEnViaje(
            @Param("numeroIdentificacion") String numeroIdentificacion,
            @Param("idViaje") Long idViaje);
}
