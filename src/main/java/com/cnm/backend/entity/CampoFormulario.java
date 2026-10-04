package com.cnm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa la tabla 'campo_formulario' en PostgreSQL.
 * Mapea los campos personalizados definidos por el administrador para cada viaje (RF7).
 */
@Entity
@Table(name = "campo_formulario")
public class CampoFormulario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_campo")
    private Long idCampo;

    @Column(name = "id_viaje", nullable = false)
    private Long idViaje;

    @Column(name = "tipo_campo", nullable = false, length = 20)
    private String tipoCampo = "texto";

    @Column(name = "etiqueta_pregunta", nullable = false, length = 100)
    private String etiquetaPregunta;

    @Column(name = "opciones_respuesta", columnDefinition = "json")
    private String opcionesRespuesta;

    @Column(name = "orden", nullable = false)
    private Integer orden = 0;

    @Column(name = "obligatorio", nullable = false)
    private Boolean obligatorio = false;

    public CampoFormulario() {}

    public CampoFormulario(Long idCampo, Long idViaje, String tipoCampo,
                           String etiquetaPregunta, String opcionesRespuesta,
                           Integer orden, Boolean obligatorio) {
        this.idCampo = idCampo;
        this.idViaje = idViaje;
        this.tipoCampo = tipoCampo != null ? tipoCampo : "texto";
        this.etiquetaPregunta = etiquetaPregunta;
        this.opcionesRespuesta = opcionesRespuesta;
        this.orden = orden != null ? orden : 0;
        this.obligatorio = obligatorio != null ? obligatorio : false;
    }

    // Getters y Setters

    public Long getIdCampo() {
        return idCampo;
    }

    public void setIdCampo(Long idCampo) {
        this.idCampo = idCampo;
    }

    public Long getIdViaje() {
        return idViaje;
    }

    public void setIdViaje(Long idViaje) {
        this.idViaje = idViaje;
    }

    public String getTipoCampo() {
        return tipoCampo;
    }

    public void setTipoCampo(String tipoCampo) {
        this.tipoCampo = tipoCampo;
    }

    public String getEtiquetaPregunta() {
        return etiquetaPregunta;
    }

    public void setEtiquetaPregunta(String etiquetaPregunta) {
        this.etiquetaPregunta = etiquetaPregunta;
    }

    public String getOpcionesRespuesta() {
        return opcionesRespuesta;
    }

    public void setOpcionesRespuesta(String opcionesRespuesta) {
        this.opcionesRespuesta = opcionesRespuesta;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Boolean getObligatorio() {
        return obligatorio;
    }

    public void setObligatorio(Boolean obligatorio) {
        this.obligatorio = obligatorio;
    }
}
