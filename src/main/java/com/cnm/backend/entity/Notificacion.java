package com.cnm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.ZonedDateTime;

/**
 * Entidad JPA que representa la tabla 'notificacion' en PostgreSQL.
 * Gestiona los mensajes del sistema y alertas al usuario (RF8, RF9).
 */
@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo = "nuevo_viaje";

    @Column(name = "mensaje", nullable = false, columnDefinition = "text")
    private String mensaje;

    @Column(name = "fecha_envio", nullable = false)
    private ZonedDateTime fechaEnvio = ZonedDateTime.now();

    @Column(name = "leida", nullable = false)
    private Boolean leida = false;

    public Notificacion() {}

    public Notificacion(Long idNotificacion, Long idUsuario, String tipo,
                        String mensaje, ZonedDateTime fechaEnvio, Boolean leida) {
        this.idNotificacion = idNotificacion;
        this.idUsuario = idUsuario;
        this.tipo = tipo != null ? tipo : "nuevo_viaje";
        this.mensaje = mensaje;
        this.fechaEnvio = fechaEnvio != null ? fechaEnvio : ZonedDateTime.now();
        this.leida = leida != null ? leida : false;
    }

    // Getters y Setters

    public Long getIdNotificacion() {
        return idNotificacion;
    }

    public void setIdNotificacion(Long idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public ZonedDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(ZonedDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }
}
