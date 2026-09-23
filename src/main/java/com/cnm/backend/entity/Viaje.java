package com.cnm.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;

@Entity
@Table(name = "viaje")
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_viaje")
    private Long idViaje;

    @Column(name = "id_administrador_creador", nullable = false, columnDefinition = "REFERENCES usuario(id_usuario)")
    private Long idAdministradorCreador;

    @Column(name = "titulo", nullable = false, length = 50)
    private String titulo;

    @Column(name = "descripcion", length = 50)
    private String descripcion;

    @Column(name = "itinerario", length = 50)
    private String itinerario;

    @Column(name = "dificultad", length = 50)
    private String dificultad;

    @Column(name = "fecha_hora_ida", nullable = false)
    private ZonedDateTime fechaHoraIda;

    @Column(name = "fecha_hora_vuelta", nullable = false)
    private ZonedDateTime fechaHoraVuelta;

    @Column(name = "punto_encuentro", nullable = false, length = 50)
    private String puntoEncuentro;

    @Column(name = "inclusiones_adicionales", length = 50)
    private String inclusionesAdicionales;

    @Positive
    @Digits(integer = 12, fraction = 2)
    @Column(name = "monto_total", nullable = false)
    private BigDecimal montoTotal;

    @PositiveOrZero
    @Digits(integer = 12, fraction = 2)
    @Column(name = "monto_reserva", nullable = false)
    private BigDecimal montoReserva;

    @Positive
    @Column(name = "cupos_maximos", nullable = false)
    private Integer cuposMaximos;

    @PositiveOrZero
    @Column(name = "cupos_disponibles", nullable = false)
    private Integer cuposDisponibles;

    @Column(name = "enlace_whatsapp", length = 50)
    private String enlaceWhatsApp;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 50)
    private Estado estado;

    @Column(name = "fecha_creacion", nullable = false)
    private ZonedDateTime fechaCreacion;

    public Viaje() {}

    public Long getIdViaje() { return idViaje; }
    public void setIdViaje(Long idViaje) { this.idViaje = idViaje; }
    public Long getIdAdministradorCreador() { return idAdministradorCreador; }
    public void setIdAdministradorCreador(Long idAdministradorCreador) { this.idAdministradorCreador = idAdministradorCreador; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getItinerario() { return itinerario; }
    public void setItinerario(String itinerario) { this.itinerario = itinerario; }
    public String getDificultad() { return dificultad; }
    public void setDificultad(String dificultad) { this.dificultad = dificultad; }
    public ZonedDateTime getFechaHoraIda() { return fechaHoraIda; }
    public void setFechaHoraIda(ZonedDateTime fechaHoraIda) { this.fechaHoraIda = fechaHoraIda; }
    public ZonedDateTime getFechaHoraVuelta() { return fechaHoraVuelta; }
    public void setFechaHoraVuelta(ZonedDateTime fechaHoraVuelta) { this.fechaHoraVuelta = fechaHoraVuelta; }
    public String getPuntoEncuentro() { return puntoEncuentro; }
    public void setPuntoEncuentro(String puntoEncuentro) { this.puntoEncuentro = puntoEncuentro; }
    public String getInclusionesAdicionales() { return inclusionesAdicionales; }
    public void setInclusionesAdicionales(String inclusionesAdicionales) { this.inclusionesAdicionales = inclusionesAdicionales; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public BigDecimal getMontoReserva() { return montoReserva; }
    public void setMontoReserva(BigDecimal montoReserva) { this.montoReserva = montoReserva; }
    public Integer getCuposMaximos() { return cuposMaximos; }
    public void setCuposMaximos(Integer cuposMaximos) { this.cuposMaximos = cuposMaximos; }
    public Integer getCuposDisponibles() { return cuposDisponibles; }
    public void setCuposDisponibles(Integer cuposDisponibles) { this.cuposDisponibles = cuposDisponibles; }
    public String getEnlaceWhatsApp() { return enlaceWhatsApp; }
    public void setEnlaceWhatsApp(String enlaceWhatsApp) { this.enlaceWhatsApp = enlaceWhatsApp; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public ZonedDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(ZonedDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
