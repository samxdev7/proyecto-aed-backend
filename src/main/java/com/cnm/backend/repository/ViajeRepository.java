package com.cnm.backend.repository;

import com.cnm.backend.entity.Rol;
import com.cnm.backend.entity.Usuario;
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
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su correo electrónico (utilizado en AuthService para login y verificación).
     */
    Optional<Usuario> findByCorreo(String correo);

    /**
     * Comprueba si un correo ya se encuentra registrado (útil para validación en registro - RF5).
     */
    boolean existsByCorreo(String correo);

    /**
     * Comprueba si un número de identificación (cédula/pasaporte) ya está en uso.
     */
    boolean existsByNumeroIdentificacion(String numeroIdentificacion);

    /**
     * Busca un usuario por su número de identificación oficial.
     */
    Optional<Usuario> findByNumeroIdentificacion(String numeroIdentificacion);

    /**
     * Obtiene una lista paginada de usuarios filtrando por su rol (Endpoint B5).
     */
    Page<Usuario> findByRol(Rol rol, Pageable pageable);
}
