package com.cnm.backend.service;

import com.cnm.backend.dto.common.PageResponseDto;
import com.cnm.backend.dto.historial.AcompananteHistorialDto;
import com.cnm.backend.dto.historial.HistorialReservaDetalleDto;
import com.cnm.backend.dto.historial.HistorialReservaResumenDto;
import com.cnm.backend.dto.historial.RespuestaFormularioHistorialDto;
import com.cnm.backend.dto.historial.ViajeHistorialDetalleDto;
import com.cnm.backend.dto.historial.ViajeHistorialResumenDto;
import com.cnm.backend.dto.usuario.ActualizarNotificacionesRequestDto;
import com.cnm.backend.dto.usuario.ActualizarPerfilRequestDto;
import com.cnm.backend.dto.usuario.UsuarioPerfilResponseDto;
import com.cnm.backend.entity.Acompanante;
import com.cnm.backend.entity.CampoFormulario;
import com.cnm.backend.entity.Reserva;
import com.cnm.backend.entity.RespuestaFormulario;
import com.cnm.backend.entity.Rol;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para la gestión de usuarios, actualización de perfiles e historial de reservas.
 * Implementa los casos de uso B1-B5 del Módulo de Usuarios y H1-H4 del Módulo de Historial.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final ViajeRepository viajeRepository;
    private final AcompananteRepository acompananteRepository;
    private final RespuestaFormularioRepository respuestaFormularioRepository;
    private final CampoFormularioRepository campoFormularioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          ReservaRepository reservaRepository,
                          ViajeRepository viajeRepository,
                          AcompananteRepository acompananteRepository,
                          RespuestaFormularioRepository respuestaFormularioRepository,
                          CampoFormularioRepository campoFormularioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.viajeRepository = viajeRepository;
        this.acompananteRepository = acompananteRepository;
        this.respuestaFormularioRepository = respuestaFormularioRepository;
        this.campoFormularioRepository = campoFormularioRepository;
    }

    /**
     * B1 / B4: Obtiene el perfil completo de un usuario por su identificador.
     */
    @Transactional(readOnly = true)
    public UsuarioPerfilResponseDto obtenerPerfil(Long idUsuario) {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        return mapToPerfilDto(usuario);
    }

    /**
     * B2: Actualiza parcialmente los datos de perfil de un usuario.
     */
    @Transactional
    public UsuarioPerfilResponseDto actualizarPerfil(Long idUsuario, ActualizarPerfilRequestDto request) {
        Usuario usuario = buscarUsuarioPorId(idUsuario);

        // Validación de unicidad de identificación si se modificó
        if (request.numeroIdentificacion() != null &&
                !request.numeroIdentificacion().equals(usuario.getNumeroIdentificacion())) {
            if (usuarioRepository.existsByNumeroIdentificacion(request.numeroIdentificacion())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "El número de identificación ya se encuentra registrado por otro usuario");
            }
            usuario.setNumeroIdentificacion(request.numeroIdentificacion());
        }

        if (request.primerNombre() != null) usuario.setPrimerNombre(request.primerNombre());
        if (request.segundoNombre() != null) usuario.setSegundoNombre(request.segundoNombre());
        if (request.primerApellido() != null) usuario.setPrimerApellido(request.primerApellido());
        if (request.segundoApellido() != null) usuario.setSegundoApellido(request.segundoApellido());
        if (request.telefono() != null) usuario.setTelefono(request.telefono());
        if (request.sexo() != null) usuario.setSexo(request.sexo());
        if (request.nacionalidad() != null) usuario.setNacionalidad(request.nacionalidad());
        if (request.tipoIdentificacion() != null) usuario.setTipoIdentificacion(request.tipoIdentificacion());

        usuarioRepository.save(usuario);
        return mapToPerfilDto(usuario);
    }

    /**
     * B3: Actualiza la preferencia de notificaciones push/navegador del usuario.
     */
    @Transactional
    public UsuarioPerfilResponseDto actualizarNotificaciones(Long idUsuario, ActualizarNotificacionesRequestDto request) {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        usuario.setNotificacionesHabilitadas(request.notificacionesHabilitadas());
        usuarioRepository.save(usuario);
        return mapToPerfilDto(usuario);
    }

    /**
     * B5: Lista usuarios registrados de forma paginada con filtro opcional por rol.
     */
    @Transactional(readOnly = true)
    public PageResponseDto<UsuarioPerfilResponseDto> listarUsuarios(String rolStr, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Usuario> usuariosPage;

        if (rolStr != null && !rolStr.isBlank()) {
            try {
                Rol rol = Rol.valueOf(rolStr.toLowerCase());
                usuariosPage = usuarioRepository.findByRol(rol, pageable);
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol inválido especificado: " + rolStr);
            }
        } else {
            usuariosPage = usuarioRepository.findAll(pageable);
        }

        List<UsuarioPerfilResponseDto> items = usuariosPage.getContent().stream()
                .map(this::mapToPerfilDto)
                .toList();

        return new PageResponseDto<>(items, page, size, usuariosPage.getTotalElements(), usuariosPage.getTotalPages());
    }

    /**
     * H1 / H2: Historial paginado de reservas para un usuario, con filtro opcional por estado.
     */
    @Transactional(readOnly = true)
    public PageResponseDto<HistorialReservaResumenDto> obtenerHistorial(Long idUsuario, String estado, int page, int size) {
        // Verificar existencia de usuario
        buscarUsuarioPorId(idUsuario);

        Pageable pageable = PageRequest.of(page, size);
        Page<Reserva> reservasPage;

        if (estado != null && !estado.isBlank()) {
            reservasPage = reservaRepository.findByIdUsuarioAndEstado(idUsuario, estado.toLowerCase(), pageable);
        } else {
            reservasPage = reservaRepository.findByIdUsuario(idUsuario, pageable);
        }

        List<HistorialReservaResumenDto> items = reservasPage.getContent().stream()
                .map(reserva -> {
                    Viaje viaje = viajeRepository.findById(reserva.getIdViaje()).orElse(null);
                    ViajeHistorialResumenDto viajeDto = (viaje != null)
                            ? new ViajeHistorialResumenDto(viaje.getIdViaje(), viaje.getTitulo(), viaje.getDificultad(), viaje.getFechaHoraIda())
                            : new ViajeHistorialResumenDto(reserva.getIdViaje(), "Viaje no disponible", "N/A", null);

                    java.math.BigDecimal montoReserva = (viaje != null && viaje.getMontoReserva() != null)
                            ? viaje.getMontoReserva()
                            : java.math.BigDecimal.ZERO;

                    return new HistorialReservaResumenDto(
                            reserva.getIdReserva(),
                            viajeDto,
                            reserva.getEstado(),
                            montoReserva,
                            reserva.getFechaReserva(),
                            reserva.getFechaRevision()
                    );
                })
                .toList();

        return new PageResponseDto<>(items, page, size, reservasPage.getTotalElements(), reservasPage.getTotalPages());
    }

    /**
     * H3 / H4: Consulta el detalle completo de una reserva de historial.
     */
    @Transactional(readOnly = true)
    public HistorialReservaDetalleDto obtenerDetalleHistorial(Long idUsuario, Long idReserva, boolean esAdmin) {
        Reserva reserva;
        if (esAdmin) {
            reserva = reservaRepository.findById(idReserva)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada con ID: " + idReserva));
            if (idUsuario != null && !reserva.getIdUsuario().equals(idUsuario)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La reserva no pertenece al usuario especificado");
            }
        } else {
            reserva = reservaRepository.findByIdReservaAndIdUsuario(idReserva, idUsuario)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada para el usuario"));
        }

        // Obtener datos del viaje
        Viaje viaje = viajeRepository.findById(reserva.getIdViaje()).orElse(null);
        ViajeHistorialDetalleDto viajeDto = (viaje != null)
                ? new ViajeHistorialDetalleDto(viaje.getIdViaje(), viaje.getTitulo())
                : new ViajeHistorialDetalleDto(reserva.getIdViaje(), "Viaje no disponible");

        // Obtener lista de acompañantes
        List<Acompanante> acompanantes = acompananteRepository.findByIdReserva(reserva.getIdReserva());
        List<AcompananteHistorialDto> acompanantesDto = acompanantes.stream()
                .map(a -> new AcompananteHistorialDto(a.getNombreCompleto(), a.getTipoIdentificacion(), a.getNumeroIdentificacion()))
                .toList();

        // Obtener respuestas de formulario dinámico
        List<RespuestaFormulario> respuestas = respuestaFormularioRepository.findByIdReserva(reserva.getIdReserva());
        List<RespuestaFormularioHistorialDto> respuestasDto = new ArrayList<>();
        for (RespuestaFormulario r : respuestas) {
            CampoFormulario campo = campoFormularioRepository.findById(r.getIdCampo()).orElse(null);
            String pregunta = (campo != null) ? campo.getEtiquetaPregunta() : "Pregunta #" + r.getIdCampo();
            respuestasDto.add(new RespuestaFormularioHistorialDto(pregunta, r.getValorRespuesta()));
        }

        java.math.BigDecimal montoReserva = (viaje != null && viaje.getMontoReserva() != null)
                ? viaje.getMontoReserva()
                : java.math.BigDecimal.ZERO;

        return new HistorialReservaDetalleDto(
                reserva.getIdReserva(),
                reserva.getEstado(),
                viajeDto,
                montoReserva,
                reserva.getFechaReserva(),
                reserva.getFechaRevision(),
                reserva.getMotivoRechazo(),
                acompanantesDto,
                respuestasDto
        );
    }

    private Usuario buscarUsuarioPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + idUsuario));
    }

    public UsuarioPerfilResponseDto mapToPerfilDto(Usuario usuario) {
        return new UsuarioPerfilResponseDto(
                usuario.getIdUsuario(),
                usuario.getPrimerNombre(),
                usuario.getSegundoNombre(),
                usuario.getPrimerApellido(),
                usuario.getSegundoApellido(),
                usuario.getNombreCompleto(),
                usuario.getCorreo(),
                usuario.getRol().name(),
                usuario.getTelefono(),
                usuario.getSexo(),
                usuario.getNacionalidad(),
                usuario.getTipoIdentificacion(),
                usuario.getNumeroIdentificacion(),
                usuario.isNotificacionesHabilitadas(),
                usuario.getFechaRegistro()
        );
    }
}
