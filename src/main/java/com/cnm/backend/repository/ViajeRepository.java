package com.cnm.backend.repository;

import com.cnm.backend.entity.Rol;
import com.cnm.backend.entity.Viaje;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad Usuario.
 * Gestiona el acceso a datos y consultas derivadas para la tabla 'usuario'.
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
    Page<Viaje> findByEstado(com.cnm.backend.entity.Estado estado, Pageable pageable);
}
