package com.cnm.backend.controllers;

import com.cnm.backend.dto.campo.ActualizarCampoFormularioRequestDto;
import com.cnm.backend.dto.campo.CampoFormularioResponseDto;
import com.cnm.backend.dto.campo.CrearCampoFormularioRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para el Módulo de Campos de Formulario Personalizados.
 * Cubre los endpoints D1-D4 del Catálogo de Endpoints (Derivado de RF7).
 */
@RestController
@RequestMapping("/viajes/{idViaje}/campos-formulario")
public class CampoFormularioController {

    /**
     * D1: GET /viajes/{idViaje}/campos-formulario [Público]
     * Lista los campos personalizados definidos para el formulario de un viaje, en orden (RF7).
     */
    @GetMapping
    public ResponseEntity<List<CampoFormularioResponseDto>> listarCamposPorViaje(
            @PathVariable Long idViaje) {
        CampoFormularioResponseDto campo1 = new CampoFormularioResponseDto(
                1L, idViaje, "texto", "¿Tiene alguna condición médica relevante o alergias?",
                null, 1, false
        );
        CampoFormularioResponseDto campo2 = new CampoFormularioResponseDto(
                2L, idViaje, "seleccion_unica", "Nivel de experiencia previa en caminatas de montaña",
                "[\"Ninguna\", \"Principiante (1-2 ascensos)\", \"Intermedio\", \"Avanzado\"]",
                2, true
        );
        return ResponseEntity.ok(List.of(campo1, campo2));
    }

    /**
     * D2: POST /viajes/{idViaje}/campos-formulario [Administrador]
     * Agrega un nuevo campo personalizado al formulario del viaje (RF7).
     */
    @PostMapping
    public ResponseEntity<CampoFormularioResponseDto> crearCampo(
            @PathVariable Long idViaje,
            @Valid @RequestBody CrearCampoFormularioRequestDto request) {
        CampoFormularioResponseDto nuevoCampo = new CampoFormularioResponseDto(
                10L, idViaje, request.tipoCampo(), request.etiquetaPregunta(),
                request.opcionesRespuesta(), request.orden(), request.obligatorio()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCampo);
    }

    /**
     * D3: PUT /viajes/{idViaje}/campos-formulario/{idCampo} [Administrador]
     * Edita la pregunta o las opciones de respuesta de un campo existente (RF7).
     */
    @PutMapping("/{idCampo}")
    public ResponseEntity<CampoFormularioResponseDto> actualizarCampo(
            @PathVariable Long idViaje,
            @PathVariable Long idCampo,
            @Valid @RequestBody ActualizarCampoFormularioRequestDto request) {
        CampoFormularioResponseDto campoActualizado = new CampoFormularioResponseDto(
                idCampo, idViaje, "texto", request.etiquetaPregunta(),
                request.opcionesRespuesta(), request.orden(), request.obligatorio()
        );
        return ResponseEntity.ok(campoActualizado);
    }

    /**
     * D4: DELETE /viajes/{idViaje}/campos-formulario/{idCampo} [Administrador]
     * Elimina un campo personalizado del formulario de un viaje (RF7).
     */
    @DeleteMapping("/{idCampo}")
    public ResponseEntity<Void> eliminarCampo(
            @PathVariable Long idViaje,
            @PathVariable Long idCampo) {
        // En Service se valida que no existan respuestas asociadas para no corromper histórico.
        return ResponseEntity.noContent().build();
    }
}
