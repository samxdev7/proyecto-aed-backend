package com.cnm.backend.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de EmailService (SMTP, Resend, Brevo y Diagnóstico)")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(
                mailSender,
                "contacto@clubnicaraguensemontanismo.org",
                "smtp.gmail.com",
                "samuelenriquerueda20@gmail.com",
                "app-password-1234",
                587,
                "", // relayUrl
                "", // relaySecret
                "", // resendApiKey
                ""  // brevoApiKey
        );
    }

    @Test
    @DisplayName("Valida configuración SMTP completa satisfactoriamente")
    void validarConfiguracion_conSmtpValido_retornaTrue() {
        assertTrue(emailService.validarConfiguracion("destinatario@correo.com"));
    }

    @Test
    @DisplayName("Rechaza destinatario nulo o en blanco")
    void validarConfiguracion_destinatarioVacio_retornaFalse() {
        assertFalse(emailService.validarConfiguracion(null));
        assertFalse(emailService.validarConfiguracion("   "));
    }

    @Test
    @DisplayName("Rechaza si no hay ningún proveedor configurado (sin SMTP, sin Relay, sin Resend, sin Brevo)")
    void validarConfiguracion_sinProveedores_retornaFalse() {
        EmailService serviceSinConfig = new EmailService(
                null, "", "", "", "", 587, "", "", "", "");
        assertFalse(serviceSinConfig.validarConfiguracion("test@correo.com"));
    }

    @Test
    @DisplayName("Habilita servicio si Relay URL de Vercel está configurada aunque no haya SMTP directo")
    void validarConfiguracion_conRelayVercel_retornaTrue() {
        EmailService serviceRelay = new EmailService(
                null, "", "", "", "", 587, "https://cnm-inscripcion.vercel.app/api/internal/email", "secreto123", "", "");
        assertTrue(serviceRelay.validarConfiguracion("test@correo.com"));
    }

    @Test
    @DisplayName("Habilita servicio si Resend API Key está configurada aunque no haya SMTP")
    void validarConfiguracion_conResendApiKey_retornaTrue() {
        EmailService serviceResend = new EmailService(
                null, "", "", "", "", 587, "", "", "re_123456789_test", "");
        assertTrue(serviceResend.validarConfiguracion("test@correo.com"));
    }

    @Test
    @DisplayName("Habilita servicio si Brevo API Key está configurada aunque no haya SMTP")
    void validarConfiguracion_conBrevoApiKey_retornaTrue() {
        EmailService serviceBrevo = new EmailService(
                null, "", "", "", "", 587, "", "", "", "xkeysib-123456_test");
        assertTrue(serviceBrevo.validarConfiguracion("test@correo.com"));
    }

    @Test
    @DisplayName("obtenerDiagnostico reporta correctamente el estado de cada proveedor")
    void obtenerDiagnostico_reportaEstadoConfigurado() {
        Map<String, Object> diag = emailService.obtenerDiagnostico();
        assertNotNull(diag);
        assertEquals(false, diag.get("relayConfigurado"));
        assertEquals(true, diag.get("smtpConfigurado"));
        assertEquals("smtp.gmail.com", diag.get("smtpHost"));
        assertEquals(587, diag.get("smtpPort"));
        assertEquals(true, diag.get("smtpPasswordPresente"));
        assertEquals(false, diag.get("resendConfigurado"));
        assertEquals(false, diag.get("brevoConfigurado"));
    }

    @Test
    @DisplayName("obtenerDiagnostico reporta VERCEL_RELAY como proveedor activo prioritario")
    void obtenerDiagnostico_conRelayConfigurado_reportaProveedorVercelRelay() {
        EmailService serviceRelay = new EmailService(
                null, "", "", "", "", 587, "https://cnm-inscripcion.vercel.app/api/internal/email", "secreto123", "re_test", "brevo_test");
        Map<String, Object> diag = serviceRelay.obtenerDiagnostico();
        assertEquals(true, diag.get("relayConfigurado"));
        assertEquals("https://cnm-inscripcion.vercel.app/api/internal/email", diag.get("relayUrl"));
        assertEquals(true, diag.get("relaySecretPresente"));
        assertEquals("VERCEL_RELAY (HTTPS puerto 443 -> Gmail SMTP)", diag.get("proveedorActivo"));
    }

    @Test
    @DisplayName("Envío de correo de aprobación ejecuta send() de JavaMailSender cuando usa SMTP")
    void enviarCorreoAprobacion_conSmtp_ejecutaSend() {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarCorreoAprobacion(
                "usuario@correo.com",
                "Juan Perez",
                "Volcán Momotombo",
                100L,
                "https://chat.whatsapp.com/test"
        );

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    @DisplayName("Envío de correo de rechazo ejecuta send() de JavaMailSender cuando usa SMTP")
    void enviarCorreoRechazo_conSmtp_ejecutaSend() {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarCorreoRechazo(
                "usuario@correo.com",
                "Juan Perez",
                "Volcán Momotombo",
                100L,
                "Comprobante ilegible"
        );

        verify(mailSender, times(1)).send(mimeMessage);
    }
}
