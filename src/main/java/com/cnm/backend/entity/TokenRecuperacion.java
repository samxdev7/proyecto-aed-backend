package com.cnm.backend.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "token_recuperacion")
public class TokenRecuperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token")
    private Long idToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    @Column(name = "fecha_expiracion", nullable = false)
    private ZonedDateTime fechaExpiracion;

    @Column(name = "usado", nullable = false)
    private Boolean usado = false;

    @Column(name = "fecha_creacion", nullable = false)
    private ZonedDateTime fechaCreacion = ZonedDateTime.now();

    public TokenRecuperacion() {}

    public TokenRecuperacion(Usuario usuario, String token, ZonedDateTime fechaExpiracion) {
        this.usuario = usuario;
        this.token = token;
        this.fechaExpiracion = fechaExpiracion;
        this.usado = false;
        this.fechaCreacion = ZonedDateTime.now();
    }

    public Long getIdToken() {
        return idToken;
    }

    public void setIdToken(Long idToken) {
        this.idToken = idToken;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ZonedDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(ZonedDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Boolean getUsado() {
        return usado;
    }

    public void setUsado(Boolean usado) {
        this.usado = usado;
    }

    public ZonedDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(ZonedDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean esValido() {
        return !Boolean.TRUE.equals(this.usado) && this.fechaExpiracion.isAfter(ZonedDateTime.now());
    }
}
