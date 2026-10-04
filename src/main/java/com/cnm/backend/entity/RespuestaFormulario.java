package com.cnm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa la tabla 'respuesta_formulario' en PostgreSQL.
 * Almacena las respuestas a las preguntas dinámicas de una reserva (RF7).
 */
@Entity
@Table(name = "respuesta_formulario")
public class RespuestaFormulario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_respuesta")
    private Long idRespuesta;

    @Column(name = "id_reserva", nullable = false)
    private Long idReserva;

    @Column(name = "id_campo", nullable = false)
    private Long idCampo;

    @Column(name = "valor_respuesta", columnDefinition = "text")
    private String valorRespuesta;

    public RespuestaFormulario() {}

    public RespuestaFormulario(Long idRespuesta, Long idReserva, Long idCampo, String valorRespuesta) {
        this.idRespuesta = idRespuesta;
        this.idReserva = idReserva;
        this.idCampo = idCampo;
        this.valorRespuesta = valorRespuesta;
    }

    // Getters y Setters

    public Long getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(Long idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    public Long getIdCampo() {
        return idCampo;
    }

    public void setIdCampo(Long idCampo) {
        this.idCampo = idCampo;
    }

    public String getValorRespuesta() {
        return valorRespuesta;
    }

    public void setValorRespuesta(String valorRespuesta) {
        this.valorRespuesta = valorRespuesta;
    }
}
