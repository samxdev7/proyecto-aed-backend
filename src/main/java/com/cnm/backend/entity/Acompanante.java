package com.cnm.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa la tabla 'acompanante' en PostgreSQL.
 * Mapea a los acompañantes de una reserva con nombres normalizados en 1FN (RF1, RF9).
 */
@Entity
@Table(name = "acompanante")
public class Acompanante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acompanante")
    private Long idAcompanante;

    @Column(name = "id_reserva", nullable = false)
    private Long idReserva;

    @Column(name = "primer_nombre", nullable = false, length = 50)
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 50)
    private String segundoNombre;

    @Column(name = "primer_apellido", nullable = false, length = 50)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 50)
    private String segundoApellido;

    @Column(name = "tipo_identificacion", nullable = false, length = 20)
    private String tipoIdentificacion = "cedula";

    @Column(name = "numero_identificacion", nullable = false, length = 50)
    private String numeroIdentificacion;

    public Acompanante() {}

    public Acompanante(Long idAcompanante, Long idReserva, String primerNombre,
                       String segundoNombre, String primerApellido,
                       String segundoApellido, String tipoIdentificacion,
                       String numeroIdentificacion) {
        this.idAcompanante = idAcompanante;
        this.idReserva = idReserva;
        this.primerNombre = primerNombre;
        this.segundoNombre = segundoNombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.tipoIdentificacion = tipoIdentificacion != null ? tipoIdentificacion : "cedula";
        this.numeroIdentificacion = numeroIdentificacion;
    }

    // Getters y Setters

    public Long getIdAcompanante() {
        return idAcompanante;
    }

    public void setIdAcompanante(Long idAcompanante) {
        this.idAcompanante = idAcompanante;
    }

    public Long getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }

    public String getPrimerNombre() {
        return primerNombre;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public String getSegundoNombre() {
        return segundoNombre;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(String tipoIdentificacion) {
        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder();
        if (primerNombre != null) sb.append(primerNombre);
        if (segundoNombre != null && !segundoNombre.isBlank()) sb.append(" ").append(segundoNombre);
        if (primerApellido != null) sb.append(" ").append(primerApellido);
        if (segundoApellido != null && !segundoApellido.isBlank()) sb.append(" ").append(segundoApellido);
        return sb.toString().trim();
    }
}
