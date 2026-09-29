package com.cnm.backend.service;

import com.cnm.backend.dto.campo.ActualizarCampoFormularioRequestDto;
import com.cnm.backend.dto.common.PageResponseDto;
import com.cnm.backend.dto.viaje.ViajeDetalleDto;
import com.cnm.backend.dto.viaje.ViajeResumenDto;
import com.cnm.backend.entity.Viaje;
import com.cnm.backend.repository.ReservaRepository;
import com.cnm.backend.repository.ViajeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Servicio para la gestión de los viajes abiertos a inscripción.
 */
@Service
public class ViajeService {

    private final ViajeRepository viajeRepository;
    private final ReservaRepository reservaRepository;

    public ViajeService(ViajeRepository viajeRepository,
                        ReservaRepository reservaRepository) {
        this.viajeRepository = viajeRepository;
        this.reservaRepository = reservaRepository;
    }

    /**
     * D1: Listar todos los viajes creados abiertos a inscripción.
     */
    @Transactional
    public PageResponseDto<ViajeResumenDto> listarViajes(String dificultad,
                                                         ZonedDateTime fechaDesde,
                                                         String estado,
                                                         int page,
                                                         int size) {
        return null;
    }

    /**
     * Retorna un viaje creado a través de su identificación.
     */
    @Transactional
    public ViajeDetalleDto obtenerViajePorId(Long idViaje) {
        return null;
    }

    /**
     * Crea un viaje a través de los datos pasados por la creación del viaje.
     */
    @Transactional
    public ViajeDetalleDto crearViaje() {
        return null;
    }

    /**
     * Elimina un viaje creado mediante su identificador.
     */
    @Transactional
    public ViajeDetalleDto actualizarEstadoViaje(Long idViaje,
                                                 ActualizarCampoFormularioRequestDto request) {
        return null;
    }

    /**
     * Elimina un viaje creado mediante su identificador.
     */
    @Transactional
    public void eliminarViaje(Long idViaje) {

    }

    private ViajeDetalleDto mapToDto(Viaje viaje) {
        return new ViajeDetalleDto(
                viaje.getIdViaje(),
                viaje.getIdAdministradorCreador(),
                viaje.getTitulo(),
                viaje.getDescripcion(),
                viaje.getItinerario(),
                viaje.getDificultad(),
                viaje.getFechaHoraIda(),
                viaje.getFechaHoraVuelta(),
                viaje.getPuntoEncuentro(),
                viaje.getInclusionesAdicionales(),
                viaje.getMontoTotal(),
                viaje.getMontoReserva(),
                viaje.getCuposMaximos(),
                viaje.getCuposDisponibles(),
                viaje.getEnlaceWhatsApp(),
                viaje.getEstado().name(),
                viaje.getFechaCreacion()
        );
    }
}
