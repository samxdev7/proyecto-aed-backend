package com.cnm.backend.service;

import com.cnm.backend.dto.campo.ActualizarCampoFormularioRequestDto;
import com.cnm.backend.dto.campo.CampoFormularioResponseDto;
import com.cnm.backend.dto.campo.CrearCampoFormularioRequestDto;
import com.cnm.backend.entity.CampoFormulario;
import com.cnm.backend.repository.CampoFormularioRepository;
import com.cnm.backend.repository.RespuestaFormularioRepository;
import com.cnm.backend.repository.ViajeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Servicio para la gestión de campos de formulario dinámicos por viaje.
 * Implementa los casos de uso D1-D4 del Catálogo de Endpoints (Derivado de RF7).
 */
@Service
public class CampoFormularioService {

    private final CampoFormularioRepository campoFormularioRepository;
    private final ViajeRepository viajeRepository;
    private final RespuestaFormularioRepository respuestaFormularioRepository;

    public CampoFormularioService(CampoFormularioRepository campoFormularioRepository,
                                  ViajeRepository viajeRepository,
                                  RespuestaFormularioRepository respuestaFormularioRepository) {
        this.campoFormularioRepository = campoFormularioRepository;
        this.viajeRepository = viajeRepository;
        this.respuestaFormularioRepository = respuestaFormularioRepository;
    }

    /**
     * D1: Lista todos los campos personalizados definidos para el formulario de un viaje, en orden.
     */
    @Transactional(readOnly = true)
    public List<CampoFormularioResponseDto> listarCamposPorViaje(Long idViaje) {
        validarExistenciaViaje(idViaje);
        List<CampoFormulario> campos = campoFormularioRepository.findByIdViajeOrderByOrdenAsc(idViaje);
        return campos.stream()
                .map(this::mapToDto)
                .toList();
    }

    /**
     * D2: Crea un nuevo campo personalizado para el viaje especificado.
     */
    @Transactional
    public CampoFormularioResponseDto crearCampo(Long idViaje, CrearCampoFormularioRequestDto request) {
        validarExistenciaViaje(idViaje);

        CampoFormulario campo = new CampoFormulario();
        campo.setIdViaje(idViaje);
        campo.setTipoCampo(request.tipoCampo());
        campo.setEtiquetaPregunta(request.etiquetaPregunta());
        campo.setOpcionesRespuesta(request.opcionesRespuesta());
        campo.setOrden(request.orden());
        campo.setObligatorio(request.obligatorio());

        CampoFormulario guardado = campoFormularioRepository.save(campo);
        return mapToDto(guardado);
    }

    /**
     * D3: Actualiza la etiqueta, opciones, orden u obligatoriedad de un campo existente.
     */
    @Transactional
    public CampoFormularioResponseDto actualizarCampo(Long idViaje, Long idCampo, ActualizarCampoFormularioRequestDto request) {
        validarExistenciaViaje(idViaje);
        CampoFormulario campo = campoFormularioRepository.findByIdCampoAndIdViaje(idCampo, idViaje)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Campo con ID " + idCampo + " no encontrado para el viaje con ID " + idViaje));

        campo.setEtiquetaPregunta(request.etiquetaPregunta());
        campo.setOpcionesRespuesta(request.opcionesRespuesta());
        campo.setOrden(request.orden());
        campo.setObligatorio(request.obligatorio());

        CampoFormulario guardado = campoFormularioRepository.save(campo);
        return mapToDto(guardado);
    }

    /**
     * D4: Elimina un campo personalizado asegurando la integridad de respuestas históricas.
     */
    @Transactional
    public void eliminarCampo(Long idViaje, Long idCampo) {
        validarExistenciaViaje(idViaje);
        CampoFormulario campo = campoFormularioRepository.findByIdCampoAndIdViaje(idCampo, idViaje)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Campo con ID " + idCampo + " no encontrado para el viaje con ID " + idViaje));

        // Regla de integridad referencial de negocio: no eliminar si ya existen reservas con respuestas
        if (respuestaFormularioRepository.existsByIdCampo(idCampo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar el campo porque ya existen reservas con respuestas registradas.");
        }

        campoFormularioRepository.delete(campo);
    }

    private void validarExistenciaViaje(Long idViaje) {
        if (!viajeRepository.existsById(idViaje)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Viaje con ID " + idViaje + " no encontrado");
        }
    }

    private CampoFormularioResponseDto mapToDto(CampoFormulario campo) {
        return new CampoFormularioResponseDto(
                campo.getIdCampo(),
                campo.getIdViaje(),
                campo.getTipoCampo(),
                campo.getEtiquetaPregunta(),
                campo.getOpcionesRespuesta(),
                campo.getOrden(),
                Boolean.TRUE.equals(campo.getObligatorio())
        );
    }
}
