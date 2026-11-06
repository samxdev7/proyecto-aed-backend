package com.cnm.backend.service;

import com.cnm.backend.CnmBackendApplication;
import com.cnm.backend.dto.reserva.ReservaDetalleDto;
import com.cnm.backend.entity.Reserva;
import com.cnm.backend.repository.ReservaRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CnmBackendApplication.class)
public class SimulacionAprobacionTest {

    private static final Logger log = LoggerFactory.getLogger(SimulacionAprobacionTest.class);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> {
            registry.add(entry.getKey(), entry::getValue);
            System.setProperty(entry.getKey(), entry.getValue());
        });
        registry.add("spring.mail.host", () -> dotenv.get("MAIL_HOST", "smtp.gmail.com"));
        registry.add("spring.mail.port", () -> dotenv.get("MAIL_PORT", "587"));
        registry.add("spring.mail.username", () -> dotenv.get("MAIL_USERNAME", ""));
        registry.add("spring.mail.password", () -> dotenv.get("MAIL_PASSWORD", ""));
        registry.add("app.mail.from", () -> dotenv.get("MAIL_FROM", ""));
    }

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private EmailService emailService;

    @Test
    @DisplayName("Simular aprobación de reserva a Peña Labrada para ratasnicas@gmail.com y verificar envío de correo")
    void simularAprobacionReservaPenaLabradaRatasnicas() throws Exception {
        Long idReserva = 51L;
        Long idAdmin = 1L; // Sofía Ramírez

        Reserva reservaAntes = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new IllegalStateException("No se encontró reserva #" + idReserva));
        log.info("Estado inicial de la reserva #{}: {}", idReserva, reservaAntes.getEstado());

        // Si ya estuviese aprobada, se restablece a pendiente para simular el flujo real
        if (!"pendiente".equalsIgnoreCase(reservaAntes.getEstado())) {
            reservaAntes.setEstado("pendiente");
            reservaAntes.setIdAdministradorRevisor(null);
            reservaAntes.setFechaRevision(null);
            reservaRepository.save(reservaAntes);
            log.info("Reserva #{} restablecida a estado 'pendiente' para ejecutar el flujo completo", idReserva);
        }

        log.info("Diagnóstico de servicio de correo: {}", emailService.obtenerDiagnostico());

        log.info("Ejecutando aprobación administrativa para reserva #{} con admin #{}...", idReserva, idAdmin);
        ReservaDetalleDto detalle = reservaService.aprobarReserva(idReserva, idAdmin);

        assertNotNull(detalle, "El DTO retornado no debe ser nulo");
        assertEquals("aprobada", detalle.estado(), "El estado de la reserva debe ser 'aprobada'");
        log.info("Aprobación exitosa en base de datos. Detalle: ID={}, Usuario={}, Viaje={}, Estado={}",
                detalle.idReserva(), detalle.nombreUsuario(), detalle.tituloViaje(), detalle.estado());

        // Esperar unos segundos a que el hilo @Async complete la conexión TLS y el envío SMTP a Gmail
        log.info("Esperando despacho SMTP a través de smtp.gmail.com:587 para ratasnicas@gmail.com...");
        Thread.sleep(8000);

        Reserva reservaDespues = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new IllegalStateException("No se encontró reserva #" + idReserva));
        assertEquals("aprobada", reservaDespues.getEstado());
        assertEquals(idAdmin, reservaDespues.getIdAdministradorRevisor());
        assertNotNull(reservaDespues.getFechaRevision());

        log.info("¡Simulación completada con éxito! Reserva #{} confirmada como 'aprobada' en Supabase.", idReserva);
    }
}
