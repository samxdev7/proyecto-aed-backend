package com.cnm.backend.repository;

import com.cnm.backend.entity.Estado;
import com.cnm.backend.entity.Viaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad Viaje.
 * Gestiona el acceso a datos y consultas para la tabla 'viaje' (RF2, RF3).
 */
@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Long> {

    /**
     * Busca un viaje por su identificador primario.
     */
    Optional<Viaje> findByIdViaje(Long idViaje);

    /**
     * Obtiene una lista paginada de viajes filtrando por su estado (activo/cerrado).
     */
    Page<Viaje> findByEstado(Estado estado, Pageable pageable);

    /**
     * Consulta con filtros combinados opcionales para el catálogo de viajes (Endpoint C1).
     */
    @Query("SELECT v FROM Viaje v WHERE " +
           "(:dificultad IS NULL OR v.dificultad = :dificultad) AND " +
           "(:fechaDesde IS NULL OR v.fechaHoraIda >= :fechaDesde) AND " +
           "(:estado IS NULL OR v.estado = :estado)")
    Page<Viaje> buscarConFiltros(
            @Param("dificultad") String dificultad,
            @Param("fechaDesde") ZonedDateTime fechaDesde,
            @Param("estado") Estado estado,
            Pageable pageable);
}
