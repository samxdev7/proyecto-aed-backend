package com.cnm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Entidad JPA que representa la tabla 'reserva' en PostgreSQL.
 * Mapea el núcleo transaccional del sistema (RF1, RF4, RF9).
 */
@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "id_viaje", nullable = false)
    private Long idViaje;

    @Column(name = "id_administrador_revisor")
    private Long idAdministradorRevisor;

    @Column(name = "estado", nullable = false, length = 15)
    private String estado = "pendiente";

    @Column(name = "fecha_reserva", nullable = false)
    private ZonedDateTime fechaReserva = ZonedDateTime.now();

    @Column(name = "fecha_limite_pago", nullable = false)
    private ZonedDateTime fechaLimitePago;

    @Column(name = "fecha_pago")
    private ZonedDateTime fechaPago;

    @Column(name = "numero_referencia_pago", length = 50)
    private String numeroReferenciaPago;

    @Column(name = "captura_comprobante_url")
    private String capturaComprobanteUrl;

    @Column(name = "motivo_rechazo")
    private String motivoRechazo;

    @Column(name = "fecha_revision")
    private ZonedDateTime fechaRevision;

    @Column(name = "editable", nullable = false)
    private Boolean editable = false;

    public Reserva() {}

    public Reserva(Long idReserva, Long idUsuario, Long idViaje,
                   Long idAdministradorRevisor, String estado,
                   ZonedDateTime fechaReserva, ZonedDateTime fechaLimitePago,
                   ZonedDateTime fechaPago, String numeroReferenciaPago,
                   String capturaComprobanteUrl, String motivoRechazo,
                   ZonedDateTime fechaRevision, Boolean editable) {
        this.idReserva = idReserva;
        this.idUsuario = idUsuario;
        this.idViaje = idViaje;
        this.idAdministradorRevisor = idAdministradorRevisor;
        this.estado = estado != null ? estado : "pendiente";
        this.fechaReserva = fechaReserva != null ? fechaReserva : ZonedDateTime.now();
        this.fechaLimitePago = fechaLimitePago;
        this.fechaPago = fechaPago;
        this.numeroReferenciaPago = numeroReferenciaPago;
        this.capturaComprobanteUrl = capturaComprobanteUrl;
        this.motivoRechazo = motivoRechazo;
        this.fechaRevision = fechaRevision;
        this.editable = editable != null ? editable : false;
    }

    // Getters y Setters

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdViaje() {
        return idViaje;
    }

    public void setIdViaje(Long idViaje) {
        this.idViaje = idViaje;
    }

    public Long getIdAdministradorRevisor() {
        return idAdministradorRevisor;
    }

    public void setIdAdministradorRevisor(Long idAdministradorRevisor) {
        this.idAdministradorRevisor = idAdministradorRevisor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public ZonedDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(ZonedDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public ZonedDateTime getFechaLimitePago() {
        return fechaLimitePago;
    }

    public void setFechaLimitePago(ZonedDateTime fechaLimitePago) {
        this.fechaLimitePago = fechaLimitePago;
    }

    public ZonedDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(ZonedDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getNumeroReferenciaPago() {
        return numeroReferenciaPago;
    }

    public void setNumeroReferenciaPago(String numeroReferenciaPago) {
        this.numeroReferenciaPago = numeroReferenciaPago;
    }

    public String getCapturaComprobanteUrl() {
        return capturaComprobanteUrl;
    }

    public void setCapturaComprobanteUrl(String capturaComprobanteUrl) {
        this.capturaComprobanteUrl = capturaComprobanteUrl;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public ZonedDateTime getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(ZonedDateTime fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }
}
