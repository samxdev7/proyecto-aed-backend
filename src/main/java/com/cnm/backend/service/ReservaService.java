package com.cnm.backend.service;

import com.cnm.backend.dto.common.PageResponseDto;
import com.cnm.backend.dto.reserva.AcompananteDto;
import com.cnm.backend.dto.reserva.CrearReservaRequestDto;
import com.cnm.backend.dto.reserva.RechazarReservaRequestDto;
import com.cnm.backend.dto.reserva.ReservaAdminResumenDto;
import com.cnm.backend.dto.reserva.ReservaDetalleDto;
import com.cnm.backend.dto.reserva.RespuestaFormularioDto;
import com.cnm.backend.entity.Acompanante;
import com.cnm.backend.entity.CampoFormulario;
import com.cnm.backend.entity.Estado;
import com.cnm.backend.entity.Reserva;
import com.cnm.backend.entity.RespuestaFormulario;
import com.cnm.backend.entity.Usuario;
import com.cnm.backend.entity.Viaje;
import com.cnm.backend.repository.AcompananteRepository;
import com.cnm.backend.repository.CampoFormularioRepository;
import com.cnm.backend.repository.ReservaRepository;
import com.cnm.backend.repository.RespuestaFormularioRepository;
import com.cnm.backend.repository.UsuarioRepository;
import com.cnm.backend.repository.ViajeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio transaccional para la gestión de Reservas (RF1, RF4, RF9, RF10, RF11, RNF6).
 * Implementa los casos de uso E1-E5 del Catálogo de Endpoints.
 */
@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final AcompananteRepository acompananteRepository;
    private final RespuestaFormularioRepository respuestaFormularioRepository;
    private final CampoFormularioRepository campoFormularioRepository;
    private final NotificacionService notificacionService;

    public ReservaService(ReservaRepository reservaRepository,
                          ViajeRepository viajeRepository,
                          UsuarioRepository usuarioRepository,
                          AcompananteRepository acompananteRepository,
                          RespuestaFormularioRepository respuestaFormularioRepository,
                          CampoFormularioRepository campoFormularioRepository,
                          NotificacionService notificacionService) {
        this.reservaRepository = reservaRepository;
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.acompananteRepository = acompananteRepository;
        this.respuestaFormularioRepository = respuestaFormularioRepository;
        this.campoFormularioRepository = campoFormularioRepository;
        this.notificacionService = notificacionService;
    }

    /**
     * E1: Creación de reserva integral en un solo paso (RF1, RF9, RNF6).
     */
    @Transactional
    public ReservaDetalleDto crearReserva(Long idUsuario, CrearReservaRequestDto request) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Viaje viaje = viajeRepository.findById(request.idViaje())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Viaje con ID " + request.idViaje() + " no encontrado"));

        if (viaje.getEstado() != Estado.activo) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El viaje seleccionado no se encuentra activo para recibir reservas.");
        }

        if (viaje.getFechaHoraIda().isBefore(ZonedDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se pueden realizar reservas para viajes que ya han iniciado o finalizado.");
        }

        // Regla de negocio RF9: Comprobar no-reincidencia tras rechazo para el titular
        if (reservaRepository.existsByIdUsuarioAndIdViajeAndEstado(idUsuario, viaje.getIdViaje(), "rechazada")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No puedes volver a reservar para este viaje debido a un rechazo previo.");
        }

        // Regla de negocio RF9: Comprobar no-reincidencia para cada acompañante
        if (request.acompanantes() != null) {
            for (AcompananteDto acomp : request.acompanantes()) {
                if (acompananteRepository.existsAcompananteRechazadoEnViaje(acomp.numeroIdentificacion(), viaje.getIdViaje())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "El acompañante con identificación " + acomp.numeroIdentificacion() + " fue previamente rechazado para este viaje.");
                }
            }
        }

        // Regla de control y descuento de cupos (RNF6)
        int totalAcompanantes = request.acompanantes() != null ? request.acompanantes().size() : 0;
        int totalPersonas = 1 + totalAcompanantes;
        int cuposDisponibles = viaje.getCuposDisponibles() != null ? viaje.getCuposDisponibles() : 0;

        if (cuposDisponibles < totalPersonas) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No hay cupos suficientes disponibles para este viaje (Disponibles: " + cuposDisponibles + ", Solicitados: " + totalPersonas + ").");
        }

        viaje.setCuposDisponibles(cuposDisponibles - totalPersonas);
        viajeRepository.save(viaje);

        // Regla de negocio RF1: Ventana de 30 minutos
        ZonedDateTime fechaReserva = ZonedDateTime.now();
        ZonedDateTime fechaLimitePago = fechaReserva.plusMinutes(30);

        Reserva reserva = new Reserva();
        reserva.setIdUsuario(idUsuario);
        reserva.setIdViaje(viaje.getIdViaje());
        reserva.setEstado("pendiente");
        reserva.setFechaReserva(fechaReserva);
        reserva.setFechaLimitePago(fechaLimitePago);
        reserva.setFechaPago(fechaReserva);
        reserva.setNumeroReferenciaPago(request.numeroReferenciaPago());
        reserva.setCapturaComprobanteUrl(request.capturaComprobanteUrl());
        reserva.setEditable(false);

        Reserva reservaGuardada = reservaRepository.save(reserva);

        // Persistir acompañantes
        List<AcompananteDto> acompanantesDto = new ArrayList<>();
        if (request.acompanantes() != null) {
            for (AcompananteDto aDto : request.acompanantes()) {
                Acompanante a = new Acompanante();
                a.setIdReserva(reservaGuardada.getIdReserva());
                a.setPrimerNombre(aDto.primerNombre());
                a.setSegundoNombre(aDto.segundoNombre());
                a.setPrimerApellido(aDto.primerApellido());
                a.setSegundoApellido(aDto.segundoApellido());
                a.setTipoIdentificacion(aDto.tipoIdentificacion());
                a.setNumeroIdentificacion(aDto.numeroIdentificacion());
                acompananteRepository.save(a);
                acompanantesDto.add(aDto);
            }
        }

        // Persistir respuestas dinámicas del formulario
        List<RespuestaFormularioDto> respuestasDto = new ArrayList<>();
        if (request.respuestasFormulario() != null) {
            for (RespuestaFormularioDto rDto : request.respuestasFormulario()) {
                RespuestaFormulario r = new RespuestaFormulario();
                r.setIdReserva(reservaGuardada.getIdReserva());
                r.setIdCampo(rDto.idCampo());
                r.setValorRespuesta(rDto.valorRespuesta());
                respuestaFormularioRepository.save(r);

                CampoFormulario campo = campoFormularioRepository.findById(rDto.idCampo()).orElse(null);
                String etiqueta = (campo != null) ? campo.getEtiquetaPregunta() : rDto.etiquetaPregunta();
                respuestasDto.add(new RespuestaFormularioDto(rDto.idCampo(), etiqueta, rDto.valorRespuesta()));
            }
        }

        return new ReservaDetalleDto(
                reservaGuardada.getIdReserva(),
                usuario.getIdUsuario(),
                usuario.getNombreCompleto(),
                usuario.getCorreo(),
                viaje.getIdViaje(),
                viaje.getTitulo(),
                null,
                reservaGuardada.getEstado(),
                viaje.getMontoReserva(),
                reservaGuardada.getFechaReserva(),
                reservaGuardada.getFechaLimitePago(),
                reservaGuardada.getFechaPago(),
                reservaGuardada.getNumeroReferenciaPago(),
                reservaGuardada.getCapturaComprobanteUrl(),
                null,
                null,
                Boolean.TRUE.equals(reservaGuardada.getEditable()),
                acompanantesDto,
                respuestasDto
        );
    }

    /**
     * E2: Consulta de detalle completo de una reserva (RF1, RF4).
     */
    @Transactional(readOnly = true)
    public ReservaDetalleDto obtenerReservaPorId(Long idReserva, Long idUsuario, boolean esAdmin) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Reserva no encontrada con ID: " + idReserva));

        if (!esAdmin && !reserva.getIdUsuario().equals(idUsuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes autorización para consultar los detalles de esta reserva.");
        }

        Usuario usuario = usuarioRepository.findById(reserva.getIdUsuario()).orElse(null);
        Viaje viaje = viajeRepository.findById(reserva.getIdViaje()).orElse(null);

        List<Acompanante> acompanantes = acompananteRepository.findByIdReserva(idReserva);
        List<AcompananteDto> acompanantesDto = acompanantes.stream()
                .map(this::mapAcompananteToDto)
                .toList();

        List<RespuestaFormulario> respuestas = respuestaFormularioRepository.findByIdReserva(idReserva);
        List<RespuestaFormularioDto> respuestasDto = new ArrayList<>();
        for (RespuestaFormulario r : respuestas) {
            CampoFormulario campo = campoFormularioRepository.findById(r.getIdCampo()).orElse(null);
            String etiqueta = (campo != null) ? campo.getEtiquetaPregunta() : "Pregunta #" + r.getIdCampo();
            respuestasDto.add(new RespuestaFormularioDto(r.getIdCampo(), etiqueta, r.getValorRespuesta()));
        }

        return new ReservaDetalleDto(
                reserva.getIdReserva(),
                reserva.getIdUsuario(),
                usuario != null ? usuario.getNombreCompleto() : "N/A",
                usuario != null ? usuario.getCorreo() : "N/A",
                reserva.getIdViaje(),
                viaje != null ? viaje.getTitulo() : "Viaje no disponible",
                reserva.getIdAdministradorRevisor(),
                reserva.getEstado(),
                viaje != null ? viaje.getMontoReserva() : BigDecimal.ZERO,
                reserva.getFechaReserva(),
                reserva.getFechaLimitePago(),
                reserva.getFechaPago(),
                reserva.getNumeroReferenciaPago(),
                reserva.getCapturaComprobanteUrl(),
                reserva.getMotivoRechazo(),
                reserva.getFechaRevision(),
                Boolean.TRUE.equals(reserva.getEditable()),
                acompanantesDto,
                respuestasDto
        );
    }

    /**
     * E3: Listado paginado para la bandeja administrativa de reservas con filtros opcionales (RF4).
     */
    @Transactional(readOnly = true)
    public PageResponseDto<ReservaAdminResumenDto> listarReservasAdmin(String estadoStr, Long idViaje, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reserva> reservasPage;

        String estado = (estadoStr != null && !estadoStr.isBlank()) ? estadoStr.toLowerCase() : null;

        if (estado != null && idViaje != null) {
            reservasPage = reservaRepository.findByIdViajeAndEstado(idViaje, estado, pageable);
        } else if (estado != null) {
            reservasPage = reservaRepository.findByEstado(estado, pageable);
        } else if (idViaje != null) {
            reservasPage = reservaRepository.findByIdViaje(idViaje, pageable);
        } else {
            reservasPage = reservaRepository.findAll(pageable);
        }

        List<ReservaAdminResumenDto> items = reservasPage.getContent().stream()
                .map(r -> {
                    Usuario u = usuarioRepository.findById(r.getIdUsuario()).orElse(null);
                    Viaje v = viajeRepository.findById(r.getIdViaje()).orElse(null);
                    List<Acompanante> ac = acompananteRepository.findByIdReserva(r.getIdReserva());

                    return new ReservaAdminResumenDto(
                            r.getIdReserva(),
                            r.getIdViaje(),
                            v != null ? v.getTitulo() : "N/A",
                            r.getIdUsuario(),
                            u != null ? u.getNombreCompleto() : "N/A",
                            u != null ? u.getCorreo() : "N/A",
                            r.getEstado(),
                            v != null ? v.getMontoReserva() : BigDecimal.ZERO,
                            r.getFechaReserva(),
                            r.getFechaLimitePago(),
                            r.getFechaPago(),
                            r.getNumeroReferenciaPago(),
                            r.getFechaRevision(),
                            ac.size()
                    );
                })
                .toList();

        return new PageResponseDto<>(items, page, size, reservasPage.getTotalElements(), reservasPage.getTotalPages());
    }

    /**
     * E4: Aprobación administrativa de una reserva pendiente (RF10, RF11, RF9).
     */
    @Transactional
    public ReservaDetalleDto aprobarReserva(Long idReserva, Long idAdmin) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Reserva no encontrada con ID: " + idReserva));

        if (!"pendiente".equalsIgnoreCase(reserva.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Únicamente se pueden aprobar reservas en estado pendiente.");
        }

        reserva.setEstado("aprobada");
        reserva.setIdAdministradorRevisor(idAdmin);
        reserva.setFechaRevision(ZonedDateTime.now());
        reservaRepository.save(reserva);

        // Notificación al usuario con enlace de WhatsApp (RF10, RF11)
        Viaje viaje = viajeRepository.findById(reserva.getIdViaje()).orElse(null);
        String enlaceWa = (viaje != null && viaje.getEnlaceWhatsApp() != null)
                ? " Enlace de grupo: " + viaje.getEnlaceWhatsApp()
                : "";

        notificacionService.crearNotificacion(
                reserva.getIdUsuario(),
                "reserva_aprobada",
                "¡Tu reserva #" + idReserva + " ha sido aprobada!" + enlaceWa
        );

        return obtenerReservaPorId(idReserva, idAdmin, true);
    }

    /**
     * E5: Rechazo administrativo de una reserva pendiente con motivo obligatorio (RF4, RF9).
     * Reintegra los cupos reservados al viaje correspondiente.
     */
    @Transactional
    public ReservaDetalleDto rechazarReserva(Long idReserva, Long idAdmin, RechazarReservaRequestDto request) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Reserva no encontrada con ID: " + idReserva));

        if (!"pendiente".equalsIgnoreCase(reserva.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Únicamente se pueden rechazar reservas en estado pendiente.");
        }

        reserva.setEstado("rechazada");
        reserva.setMotivoRechazo(request.motivoRechazo());
        reserva.setIdAdministradorRevisor(idAdmin);
        reserva.setFechaRevision(ZonedDateTime.now());
        reservaRepository.save(reserva);

        // Reintegrar cupos al viaje
        Viaje viaje = viajeRepository.findById(reserva.getIdViaje()).orElse(null);
        if (viaje != null) {
            int totalAcomp = acompananteRepository.findByIdReserva(idReserva).size();
            int cuposReintegrar = 1 + totalAcomp;
            int nuevosDisponibles = Math.min(viaje.getCuposMaximos(), viaje.getCuposDisponibles() + cuposReintegrar);
            viaje.setCuposDisponibles(nuevosDisponibles);
            viajeRepository.save(viaje);
        }

        // Notificar formalmente al cliente con el motivo del rechazo (RF4, RF9)
        notificacionService.crearNotificacion(
                reserva.getIdUsuario(),
                "reserva_rechazada",
                "Tu reserva #" + idReserva + " ha sido rechazada. Motivo: " + request.motivoRechazo()
        );

        return obtenerReservaPorId(idReserva, idAdmin, true);
    }

    private AcompananteDto mapAcompananteToDto(Acompanante a) {
        return new AcompananteDto(
                a.getPrimerNombre(),
                a.getSegundoNombre(),
                a.getPrimerApellido(),
                a.getSegundoApellido(),
                a.getTipoIdentificacion(),
                a.getNumeroIdentificacion()
        );
    }
}
