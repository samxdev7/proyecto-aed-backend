package com.cnm.backend.service;

import com.cnm.backend.dto.estadistica.EstadisticasPanelResponseDto;
import com.cnm.backend.dto.estadistica.InscritosPorViajeDto;
import com.cnm.backend.dto.estadistica.ResumenCuposDto;
import com.cnm.backend.dto.estadistica.RutaPopularDto;
import com.cnm.backend.entity.Estado;
import com.cnm.backend.entity.Reserva;
import com.cnm.backend.entity.Viaje;
import com.cnm.backend.repository.ReservaRepository;
import com.cnm.backend.repository.ViajeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para el cálculo y agregación de indicadores estadísticos del Dashboard administrativo (RF6).
 */
@Service
public class EstadisticaService {

    private final ViajeRepository viajeRepository;
    private final ReservaRepository reservaRepository;

    public EstadisticaService(ViajeRepository viajeRepository,
                              ReservaRepository reservaRepository) {
        this.viajeRepository = viajeRepository;
        this.reservaRepository = reservaRepository;
    }

    /**
     * G1: Genera las estadísticas consolidadas del panel administrativo:
     * 1. Nivel de ocupación e inscritos por viaje activo.
     * 2. Rutas más populares mediante análisis de centralidad de grado en Grafo Bipartito.
     * 3. Resumen global de cupos mediante bifurcación funcional con Collectors.teeing.
     */
    @Transactional(readOnly = true)
    public EstadisticasPanelResponseDto obtenerEstadisticasPanel() {
        Page<Viaje> viajesPage = viajeRepository.findByEstado(Estado.activo, Pageable.unpaged());
        List<Viaje> viajesActivos = viajesPage.getContent();

        // 1. Inscritos y porcentaje de ocupación por viaje.
        List<InscritosPorViajeDto> inscritosPorViajeDtos = viajesActivos.stream()
                .map(viaje -> {
                    int cuposMaximos = viaje.getCuposMaximos() != null ? viaje.getCuposMaximos() : 0;
                    int cuposDisponibles = viaje.getCuposDisponibles() != null ? viaje.getCuposDisponibles() : 0;
                    int inscritos = Math.max(0, cuposMaximos - cuposDisponibles);

                    double porcentaje = cuposMaximos > 0
                            ? Math.round(((double) inscritos * 100.0 / cuposMaximos) * 100.0) / 100.0
                            : 0.0;

                    return new InscritosPorViajeDto(
                            viaje.getIdViaje(),
                            viaje.getTitulo(),
                            inscritos,
                            cuposMaximos,
                            porcentaje
                    );
                })
                .toList();

        // 2. Rutas más populares modeladas con Grafo Bipartito de Afinidad G = (U, V, E)
        GrafoBipartitoRutas grafo = construirGrafoReservasAprobadas();

        List<RutaPopularDto> rutaPopularDtos = viajesActivos.stream()
                .map(viaje -> new RutaPopularDto(
                        viaje.getIdViaje(),
                        viaje.getTitulo(),
                        viaje.getDificultad(),
                        grafo.obtenerGradoCentralidad(viaje.getIdViaje())
                ))
                .sorted(Comparator.comparingInt(RutaPopularDto::totalReservasAprobadas)
                        .reversed()
                        .thenComparing(RutaPopularDto::idViaje))
                .limit(5)
                .toList();

        // 3. Resumen de cupos en un único recorrido con el método Collectors.teeing
        ResumenCuposDto resumenCuposDto = viajesActivos.stream()
                .collect(Collectors.teeing(
                        Collectors.summingInt(v -> v.getCuposMaximos() != null ? v.getCuposMaximos() : 0),
                        Collectors.summingInt(v -> v.getCuposDisponibles() != null ? v.getCuposDisponibles() : 0),
                        (ofrecidos, disponibles) -> {
                            int reservados = Math.max(0, ofrecidos - disponibles);
                            return new ResumenCuposDto(ofrecidos, reservados, disponibles);
                        }
                ));

        return new EstadisticasPanelResponseDto(
                inscritosPorViajeDtos,
                rutaPopularDtos,
                resumenCuposDto
        );
    }

    /**
     * Construye un Grafo Bipartito donde las aristas no dirigidas representan
     * inscripciones aprobadas que unen Vértices de Usuario (U) con Vértices de Viaje (V).
     */
    private GrafoBipartitoRutas construirGrafoReservasAprobadas() {
        GrafoBipartitoRutas grafo = new GrafoBipartitoRutas();
        List<Reserva> reservasAprobadas = reservaRepository
                .findByEstado("aprobada", Pageable.unpaged())
                .getContent();

        for (Reserva reserva : reservasAprobadas) {
            grafo.agregarArista(reserva.getIdViaje(), reserva.getIdUsuario());
        }

        return grafo;
    }

    /**
     * Estructura de datos: Grafo Bipartito de Afinidad Ruta-Usuario.
     * Representado mediante listas de adyacencia basadas en Mapas Hash para acceso en O(1).
     */
    private static class GrafoBipartitoRutas {
        // Lista de adyacencia: Vértice Viaje -> Lista de Vértices Usuario incidentes
        private final Map<Long, List<Long>> adyacenciaViajeUsuarios = new HashMap<>();

        /**
         * Inserta una arista e = (idViaje, idUsuario) correspondiente a una reserva aprobada.
         */
        public void agregarArista(Long idViaje, Long idUsuario) {
            adyacenciaViajeUsuarios
                    .computeIfAbsent(idViaje, k -> new ArrayList<>())
                    .add(idUsuario);
        }

        /**
         * Calcula el Degree Centrality deg(v) del vértice de Viaje.
         * Representa el total de interacciones aprobadas que inciden en esta ruta.
         * Complejidad: O(1) tiempo de respuesta.
         */
        public int obtenerGradoCentralidad(Long idViaje) {
            List<Long> adyacentes = adyacenciaViajeUsuarios.get(idViaje);
            return adyacentes != null ? adyacentes.size() : 0;
        }
    }
}
