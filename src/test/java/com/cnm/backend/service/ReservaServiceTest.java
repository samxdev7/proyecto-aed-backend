package com.cnm.backend.service;

import com.cnm.backend.dto.reserva.RechazarReservaRequestDto;
import com.cnm.backend.entity.Reserva;
import com.cnm.backend.entity.Usuario;
import com.cnm.backend.entity.Viaje;
import com.cnm.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ViajeRepository viajeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AcompananteRepository acompananteRepository;

    @Mock
    private RespuestaFormularioRepository respuestaFormularioRepository;

    @Mock
    private CampoFormularioRepository campoFormularioRepository;

    @Mock
    private NotificacionService notificacionService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva reservaPendiente;
    private Viaje viaje;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        reservaPendiente = new Reserva();
        reservaPendiente.setIdReserva(100L);
        reservaPendiente.setIdUsuario(10L);
        reservaPendiente.setIdViaje(5L);
        reservaPendiente.setEstado("pendiente");
        reservaPendiente.setFechaReserva(ZonedDateTime.now());

        viaje = new Viaje();
        viaje.setIdViaje(5L);
        viaje.setTitulo("Volcán Cosigüina");
        viaje.setEnlaceWhatsApp("https://chat.whatsapp.com/test12345");
        viaje.setCuposMaximos(20);
        viaje.setCuposDisponibles(5);
        viaje.setMontoReserva(BigDecimal.valueOf(500));

        usuario = new Usuario();
        usuario.setIdUsuario(10L);
        usuario.setPrimerNombre("Carlos");
        usuario.setPrimerApellido("Pérez");
        usuario.setCorreo("carlos@example.com");
    }

    @Test
    void aprobarReserva_enviaNotificacionSinLinkWa_yLlamaEmailService() {
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reservaPendiente));
        when(viajeRepository.findById(5L)).thenReturn(Optional.of(viaje));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(acompananteRepository.findByIdReserva(100L)).thenReturn(Collections.emptyList());
        when(respuestaFormularioRepository.findByIdReserva(100L)).thenReturn(Collections.emptyList());

        reservaService.aprobarReserva(100L, 1L);

        assertEquals("aprobada", reservaPendiente.getEstado());
        assertEquals(1L, reservaPendiente.getIdAdministradorRevisor());

        ArgumentCaptor<String> mensajeCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificacionService).crearNotificacion(eq(10L), eq("reserva_aprobada"), mensajeCaptor.capture());

        String mensajeNotif = mensajeCaptor.getValue();
        assertTrue(mensajeNotif.contains("¡Tu cupo ha sido aceptado para el viaje: Volcán Cosigüina!"));
        assertTrue(mensajeNotif.contains("correo electrónico"));
        assertFalse(mensajeNotif.contains("chat.whatsapp.com"), "No debe exponer el link de WhatsApp directamente en la notificación");

        verify(emailService).enviarCorreoAprobacion(
                eq("carlos@example.com"),
                eq("Carlos Pérez"),
                eq("Volcán Cosigüina"),
                eq(100L),
                eq("https://chat.whatsapp.com/test12345")
        );
    }

    @Test
    void rechazarReserva_enviaNotificacionSinMotivoDetallado_yLlamaEmailServiceConJustificacion() {
        when(reservaRepository.findById(100L)).thenReturn(Optional.of(reservaPendiente));
        when(viajeRepository.findById(5L)).thenReturn(Optional.of(viaje));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(acompananteRepository.findByIdReserva(100L)).thenReturn(Collections.emptyList());
        when(respuestaFormularioRepository.findByIdReserva(100L)).thenReturn(Collections.emptyList());

        RechazarReservaRequestDto request = new RechazarReservaRequestDto("Comprobante ilegible, monto no coincide");
        reservaService.rechazarReserva(100L, 1L, request);

        assertEquals("rechazada", reservaPendiente.getEstado());
        assertEquals("Comprobante ilegible, monto no coincide", reservaPendiente.getMotivoRechazo());

        ArgumentCaptor<String> mensajeCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificacionService).crearNotificacion(eq(10L), eq("reserva_rechazada"), mensajeCaptor.capture());

        String mensajeNotif = mensajeCaptor.getValue();
        assertTrue(mensajeNotif.contains("Tu cupo para el viaje: Volcán Cosigüina ha sido rechazado."));
        assertTrue(mensajeNotif.contains("correo electrónico"));

        verify(emailService).enviarCorreoRechazo(
                eq("carlos@example.com"),
                eq("Carlos Pérez"),
                eq("Volcán Cosigüina"),
                eq(100L),
                eq("Comprobante ilegible, monto no coincide")
        );
    }

    @Test
    void crearReserva_cuandoFaltanMenosDe7DiasParaSalida_lanzaBadRequestPorExpiracion() {
        Usuario usuarioCompleto = new Usuario();
        usuarioCompleto.setIdUsuario(10L);
        usuarioCompleto.setPrimerNombre("Carlos");
        usuarioCompleto.setPrimerApellido("Pérez");
        usuarioCompleto.setNumeroIdentificacion("001-010190-0001A");
        usuarioCompleto.setNacionalidad("Nicaragüense");
        usuarioCompleto.setCorreo("carlos@example.com");

        Viaje viajePorExpirar = new Viaje();
        viajePorExpirar.setIdViaje(5L);
        viajePorExpirar.setTitulo("Volcán Cosigüina");
        viajePorExpirar.setEstado(com.cnm.backend.entity.Estado.activo);
        viajePorExpirar.setArchivado(false);
        // Salida dentro de 5 días (menos de 1 semana)
        viajePorExpirar.setFechaHoraIda(ZonedDateTime.now().plusDays(5));
        viajePorExpirar.setCuposDisponibles(10);
        viajePorExpirar.setMontoReserva(BigDecimal.valueOf(500));

        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioCompleto));
        when(viajeRepository.findById(5L)).thenReturn(Optional.of(viajePorExpirar));

        com.cnm.backend.dto.reserva.CrearReservaRequestDto request = new com.cnm.backend.dto.reserva.CrearReservaRequestDto(
                5L,
                Collections.emptyList(),
                Collections.emptyList(),
                "REF-12345",
                "https://ejemplo.com/voucher.jpg"
        );

        org.springframework.web.server.ResponseStatusException ex = assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> reservaService.crearReserva(10L, request)
        );

        assertEquals(org.springframework.http.HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertTrue(ex.getReason().contains("Las inscripciones para este viaje han expirado"));
    }
}
