package com.cnm.backend.repository;

import com.cnm.backend.entity.CampoFormulario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad CampoFormulario.
 * Gestiona los campos dinámicos asociados al formulario de inscripción de cada viaje (RF7).
 */
@Repository
public interface CampoFormularioRepository extends JpaRepository<CampoFormulario, Long> {

    /**
     * Obtiene los campos de un viaje ordenados por su posición para renderizar el formulario (Endpoint D1).
     */
    List<CampoFormulario> findByIdViajeOrderByOrdenAsc(Long idViaje);

    /**
     * Busca un campo específico asegurando que pertenezca al viaje indicado (Endpoints D3, D4).
     */
    Optional<CampoFormulario> findByIdCampoAndIdViaje(Long idCampo, Long idViaje);

    /**
     * Elimina todos los campos personalizados asociados a un viaje.
     */
    void deleteByIdViaje(Long idViaje);
}
