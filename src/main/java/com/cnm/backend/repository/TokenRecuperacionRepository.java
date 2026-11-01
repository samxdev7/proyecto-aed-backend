package com.cnm.backend.repository;

import com.cnm.backend.entity.TokenRecuperacion;
import com.cnm.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Long> {

    Optional<TokenRecuperacion> findByToken(String token);

    List<TokenRecuperacion> findByUsuarioAndUsadoFalse(Usuario usuario);
}
