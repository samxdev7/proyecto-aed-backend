package com.cnm.backend.repository;

import com.cnm.backend.entity.Estado;
import com.cnm.backend.entity.Viaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad Viaje.
 * Gestiona el acceso a datos y consultas para la tabla 'viaje' (RF2, RF3).
 * Los filtros combinados del catálogo (C1) se resuelven con Specification
 * (Hibernate 6 no tipa parámetros null en JPQL ":param IS NULL").
 */
@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Long>, JpaSpecificationExecutor<Viaje> {

    /**
     * Busca un viaje por su identificador primario.
     */
    Optional<Viaje> findByIdViaje(Long idViaje);

    /**
     * Obtiene una lista paginada de viajes filtrando por su estado (activo/cerrado).
     */
    Page<Viaje> findByEstado(Estado estado, Pageable pageable);
}
