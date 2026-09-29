package com.cnm.backend.controllers;

import com.cnm.backend.dto.campo.ActualizarCampoFormularioRequestDto;
import com.cnm.backend.dto.campo.CampoFormularioResponseDto;
import com.cnm.backend.dto.campo.CrearCampoFormularioRequestDto;
import com.cnm.backend.service.CampoFormularioService;
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

    private final CampoFormularioService campoFormularioService;

    public CampoFormularioController(CampoFormularioService campoFormularioService) {
        this.campoFormularioService = campoFormularioService;
    }

    /**
     * D1: GET /viajes/{idViaje}/campos-formulario [Público]
     * Lista los campos personalizados definidos para el formulario de un viaje, en orden (RF7).
     */
    @GetMapping
    public ResponseEntity<List<CampoFormularioResponseDto>> listarCamposPorViaje(
            @PathVariable Long idViaje) {
        return ResponseEntity.ok(campoFormularioService.listarCamposPorViaje(idViaje));
    }

    /**
     * D2: POST /viajes/{idViaje}/campos-formulario [Administrador]
     * Agrega un nuevo campo personalizado al formulario del viaje (RF7).
     */
    @PostMapping
    public ResponseEntity<CampoFormularioResponseDto> crearCampo(
            @PathVariable Long idViaje,
            @Valid @RequestBody CrearCampoFormularioRequestDto request) {
        CampoFormularioResponseDto nuevoCampo = campoFormularioService.crearCampo(idViaje, request);
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
        return ResponseEntity.ok(campoFormularioService.actualizarCampo(idViaje, idCampo, request));
    }

    /**
     * D4: DELETE /viajes/{idViaje}/campos-formulario/{idCampo} [Administrador]
     * Elimina un campo personalizado del formulario de un viaje (RF7).
     */
    @DeleteMapping("/{idCampo}")
    public ResponseEntity<Void> eliminarCampo(
            @PathVariable Long idViaje,
            @PathVariable Long idCampo) {
        campoFormularioService.eliminarCampo(idViaje, idCampo);
        return ResponseEntity.noContent().build();
    }
}
