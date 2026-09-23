package com.cnm.backend.repository;

import com.cnm.backend.entity.RespuestaFormulario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad RespuestaFormulario.
 * Almacena y consulta las respuestas a los campos dinámicos de las reservas (RF7).
 */
@Repository
public interface RespuestaFormularioRepository extends JpaRepository<RespuestaFormulario, Long> {

    /**
     * Obtiene todas las respuestas de formulario correspondientes a una reserva (Endpoints E2, H3, H4).
     */
    List<RespuestaFormulario> findByIdReserva(Long idReserva);

    /**
     * Verifica si ya existen respuestas registradas para un campo específico
     * (útil para validar en Service antes de permitir eliminar o mutar el tipo del campo en D4).
     */
    boolean existsByIdCampo(Long idCampo);

    /**
     * Elimina todas las respuestas asociadas a una reserva.
     */
    void deleteByIdReserva(Long idReserva);
}
