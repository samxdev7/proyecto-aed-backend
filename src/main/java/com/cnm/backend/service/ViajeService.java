package com.cnm.backend.service;

import com.cnm.backend.dto.common.PageResponseDto;
import com.cnm.backend.dto.viaje.ActualizarEstadoViajeRequestDto;
import com.cnm.backend.dto.viaje.ActualizarViajeRequestDto;
import com.cnm.backend.dto.viaje.CrearViajeRequestDto;
import com.cnm.backend.dto.viaje.ViajeDetalleDto;
import com.cnm.backend.dto.viaje.ViajeResumenDto;
import com.cnm.backend.entity.Estado;
import com.cnm.backend.entity.Viaje;
import com.cnm.backend.repository.ReservaRepository;
import com.cnm.backend.repository.ViajeRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de negocio para la gestión completa del ciclo de vida de Viajes (RF2, RF3).
 * Implementa los casos de uso C1-C6 del Catálogo de Endpoints.
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
     * C1: Listado público paginado de viajes con filtros combinados opcionales (RF3).
     */
    @Transactional(readOnly = true)
    public PageResponseDto<ViajeResumenDto> listarViajes(String dificultad,
                                                         ZonedDateTime fechaDesde,
                                                         String estadoStr,
                                                         int page,
                                                         int size) {
        Estado estado = null;
        if (estadoStr != null && !estadoStr.isBlank()) {
            try {
                estado = Estado.valueOf(estadoStr.toLowerCase());
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado inválido especificado: " + estadoStr);
            }
        }

        // ponytail: Specification dinámica en vez de JPQL con ":param IS NULL": Hibernate 6 +
        // PostgreSQL no tipa parámetros null y el listado público caía con 500
        // ("could not determine data type of parameter"). Si se necesitan filtros compuestos
        // más complejos, crecer aquí; el patrón ya soporta cualquier combinación.
        final String dif = dificultad;
        final Estado est = estado;
        Specification<Viaje> spec = (root, query, cb) -> {
            List<Predicate> condiciones = new ArrayList<>();
            // Catálogo público solo muestra viajes no archivados
            condiciones.add(cb.isFalse(root.get("archivado")));
            if (dif != null && !dif.isBlank()) {
                condiciones.add(cb.equal(root.get("dificultad"), dif));
            }
            if (fechaDesde != null) {
                condiciones.add(cb.greaterThanOrEqualTo(root.get("fechaHoraIda"), fechaDesde));
            }
            if (est != null) {
                condiciones.add(cb.equal(root.get("estado"), est));
            }
            return cb.and(condiciones.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page, size);
        Page<Viaje> viajesPage = viajeRepository.findAll(spec, pageable);

        List<ViajeResumenDto> items = viajesPage.getContent().stream()
                .map(this::mapToResumenDto)
                .toList();

        return new PageResponseDto<>(items, page, size, viajesPage.getTotalElements(), viajesPage.getTotalPages());
    }

    /**
     * C2: Consulta detallada de una ficha de viaje por su identificador primario (RF3).
     */
    @Transactional(readOnly = true)
    public ViajeDetalleDto obtenerViajePorId(Long idViaje) {
        Viaje viaje = buscarViajePorId(idViaje);
        return mapToDetalleDto(viaje);
    }

    /**
     * C3: Creación de un nuevo viaje por un usuario con rol de Administrador (RF2).
     */
    @Transactional
    public ViajeDetalleDto crearViaje(Long idAdministrador, CrearViajeRequestDto request) {
        validarCoherenciaFechas(request.fechaHoraIda(), request.fechaHoraVuelta());

        if (request.montoReserva().compareTo(request.montoTotal()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El monto de reserva no puede ser superior al monto total del viaje.");
        }

        Viaje viaje = new Viaje();
        viaje.setIdAdministradorCreador(idAdministrador);
        viaje.setTitulo(request.titulo());
        viaje.setDescripcion(request.descripcion());
        viaje.setItinerario(request.itinerario());
        viaje.setDificultad(request.dificultad());
        viaje.setFechaHoraIda(request.fechaHoraIda());
        viaje.setFechaHoraVuelta(request.fechaHoraVuelta());
        viaje.setPuntoEncuentro(request.puntoEncuentro());
        viaje.setInclusionesAdicionales(request.inclusionesAdicionales());
        viaje.setMontoTotal(request.montoTotal());
        viaje.setMontoReserva(request.montoReserva());
        viaje.setCuposMaximos(request.cuposMaximos());
        viaje.setCuposDisponibles(request.cuposMaximos());
        viaje.setEnlaceWhatsApp(request.enlaceWhatsApp());
        viaje.setImagenUrl(request.imagenUrl());
        viaje.setEquipo(request.equipo());
        viaje.setEstado(Estado.activo);
        viaje.setFechaCreacion(ZonedDateTime.now());
        viaje.setArchivado(false);

        Viaje guardado = viajeRepository.save(viaje);
        return mapToDetalleDto(guardado);
    }

    /**
     * C4: Actualización integral de los datos de un viaje existente (RF2).
     */
    @Transactional
    public ViajeDetalleDto actualizarViaje(Long idViaje, ActualizarViajeRequestDto request) {
        Viaje viaje = buscarViajePorId(idViaje);
        validarCoherenciaFechas(request.fechaHoraIda(), request.fechaHoraVuelta());

        if (request.montoReserva().compareTo(request.montoTotal()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El monto de reserva no puede ser superior al monto total del viaje.");
        }

        // Validación de coherencia de cupos respecto a las inscripciones actuales
        int cuposOcupados = viaje.getCuposMaximos() - viaje.getCuposDisponibles();
        if (request.cuposMaximos() < cuposOcupados) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se pueden reducir los cupos máximos a un valor inferior a las reservas ya adquiridas (" + cuposOcupados + ").");
        }

        viaje.setTitulo(request.titulo());
        viaje.setDescripcion(request.descripcion());
        viaje.setItinerario(request.itinerario());
        viaje.setDificultad(request.dificultad());
        viaje.setFechaHoraIda(request.fechaHoraIda());
        viaje.setFechaHoraVuelta(request.fechaHoraVuelta());
        viaje.setPuntoEncuentro(request.puntoEncuentro());
        viaje.setInclusionesAdicionales(request.inclusionesAdicionales());
        viaje.setMontoTotal(request.montoTotal());
        viaje.setMontoReserva(request.montoReserva());
        viaje.setCuposMaximos(request.cuposMaximos());
        viaje.setCuposDisponibles(request.cuposMaximos() - cuposOcupados);
        viaje.setEnlaceWhatsApp(request.enlaceWhatsApp());
        viaje.setImagenUrl(request.imagenUrl());
        viaje.setEquipo(request.equipo());

        Viaje guardado = viajeRepository.save(viaje);
        return mapToDetalleDto(guardado);
    }

    /**
     * C5: Cambio de estado puntual de un viaje (activo <-> cerrado) (RF2).
     */
    @Transactional
    public ViajeDetalleDto actualizarEstadoViaje(Long idViaje, ActualizarEstadoViajeRequestDto request) {
        Viaje viaje = buscarViajePorId(idViaje);
        try {
            Estado nuevoEstado = Estado.valueOf(request.estado().toLowerCase());
            viaje.setEstado(nuevoEstado);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado de viaje inválido: " + request.estado());
        }

        Viaje guardado = viajeRepository.save(viaje);
        return mapToDetalleDto(guardado);
    }

    /**
     * C6: Archiva un viaje existente (borrado lógico) (RF2).
     * Regla de negocio: El viaje DEBE estar en estado 'cerrado' para poder archivarse.
     */
    @Transactional
    public ViajeDetalleDto archivarViaje(Long idViaje) {
        Viaje viaje = buscarViajePorId(idViaje);

        if (viaje.getEstado() != Estado.cerrado) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Para archivar un viaje, su estado debe ser 'cerrado'.");
        }

        viaje.setArchivado(true);
        Viaje guardado = viajeRepository.save(viaje);
        return mapToDetalleDto(guardado);
    }

    /**
     * Desarchiva un viaje previamente archivado, devolviéndolo al catálogo/panel administrativo (RF2).
     */
    @Transactional
    public ViajeDetalleDto desarchivarViaje(Long idViaje) {
        Viaje viaje = buscarViajePorId(idViaje);
        viaje.setArchivado(false);
        Viaje guardado = viajeRepository.save(viaje);
        return mapToDetalleDto(guardado);
    }

    /**
     * Obtiene la lista completa de viajes que no han sido archivados (activos o cerrados).
     * Utilizado en el panel de administrador para visualización y gestión en lista directa.
     */
    @Transactional(readOnly = true)
    public List<ViajeResumenDto> listarViajesNoArchivados() {
        return viajeRepository.findByArchivadoFalseOrderByFechaHoraIdaDesc().stream()
                .map(this::mapToResumenDto)
                .toList();
    }

    /**
     * Obtiene la lista completa de viajes que han sido archivados.
     * Utilizado en la sección de viajes archivados del Frontend.
     */
    @Transactional(readOnly = true)
    public List<ViajeResumenDto> listarViajesArchivados() {
        return viajeRepository.findByArchivadoTrueOrderByFechaHoraIdaDesc().stream()
                .map(this::mapToResumenDto)
                .toList();
    }

    private Viaje buscarViajePorId(Long idViaje) {
        return viajeRepository.findById(idViaje)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Viaje no encontrado con ID: " + idViaje));
    }

    private void validarCoherenciaFechas(ZonedDateTime fechaHoraIda, ZonedDateTime fechaHoraVuelta) {
        if (fechaHoraIda == null || fechaHoraVuelta == null || !fechaHoraVuelta.isAfter(fechaHoraIda)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha y hora de vuelta debe ser posterior a la fecha y hora de ida.");
        }
    }

    private ViajeResumenDto mapToResumenDto(Viaje viaje) {
        return new ViajeResumenDto(
                viaje.getIdViaje(),
                viaje.getTitulo(),
                viaje.getDescripcion(),
                viaje.getDificultad(),
                viaje.getFechaHoraIda(),
                viaje.getFechaHoraVuelta(),
                viaje.getPuntoEncuentro(),
                viaje.getMontoTotal(),
                viaje.getMontoReserva(),
                viaje.getCuposMaximos(),
                viaje.getCuposDisponibles(),
                viaje.getEstado() != null ? viaje.getEstado().name() : "activo",
                viaje.getImagenUrl(),
                viaje.getArchivado() != null ? viaje.getArchivado() : false
        );
    }

    private ViajeDetalleDto mapToDetalleDto(Viaje viaje) {
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
                viaje.getEstado() != null ? viaje.getEstado().name() : "activo",
                viaje.getFechaCreacion(),
                viaje.getImagenUrl(),
                viaje.getEquipo(),
                viaje.getArchivado() != null ? viaje.getArchivado() : false
        );
    }
}
