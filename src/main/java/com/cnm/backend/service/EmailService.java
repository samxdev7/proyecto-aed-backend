package com.cnm.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio encargado de la generación y envío de correos electrónicos institucionales
 * del Club Nicaragüense de Montañismo (aprobación de cupo, rechazo con justificación, etc.).
 * Soporta SMTP estándar (JavaMail), Resend HTTP API (HTTPS puerto 443) y Brevo HTTP API (HTTPS puerto 443)
 * para garantizar operatividad en plataformas como Render Free Tier donde los puertos SMTP 25, 465 y 587 son bloqueados.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private static final String LOGO_URL = "https://cnm-inscripcion.vercel.app/icon.png";
    private static final String WHATSAPP_OFICIAL = "+505 5716 4892";
    private static final String WHATSAPP_LINK = "https://wa.me/50557164892";
    private static final String INSTAGRAM_LINK = "https://www.instagram.com/clubnicaraguensemontanismo/";
    private static final String FACEBOOK_LINK = "https://www.facebook.com/p/Club-Nicarag%C3%BCense-de-Monta%C3%B1ismo-61560332057105/";

    private final JavaMailSender mailSender;
    private final String remitente;
    private final String mailHost;
    private final String mailUsername;
    private final String mailPassword;
    private final int mailPort;
    private final String resendApiKey;
    private final String brevoApiKey;
    private final RestClient restClient;

    public EmailService(
            @Autowired(required = false) JavaMailSender mailSender,
            @Value("${app.mail.from:}") String remitente,
            @Value("${spring.mail.host:}") String mailHost,
            @Value("${spring.mail.username:}") String mailUsername,
            @Value("${spring.mail.password:}") String mailPassword,
            @Value("${spring.mail.port:587}") int mailPort,
            @Value("${app.mail.resend-api-key:}") String resendApiKey,
            @Value("${app.mail.brevo-api-key:}") String brevoApiKey
    ) {
        this.mailSender = mailSender;
        this.remitente = remitente != null ? remitente.trim() : "";
        this.mailHost = mailHost != null ? mailHost.trim() : "";
        this.mailUsername = mailUsername != null ? mailUsername.trim() : "";
        this.mailPassword = mailPassword != null ? mailPassword.trim() : "";
        this.mailPort = mailPort > 0 ? mailPort : 587;
        this.resendApiKey = resendApiKey != null ? resendApiKey.trim() : "";
        this.brevoApiKey = brevoApiKey != null ? brevoApiKey.trim() : "";
        this.restClient = RestClient.create();
    }

    /**
     * Envía correo de felicitación y aceptación de cupo con el enlace al grupo de WhatsApp.
     */
    @Async
    public void enviarCorreoAprobacion(
            String destinatario,
            String nombreUsuario,
            String tituloViaje,
            Long idReserva,
            String enlaceWhatsApp
    ) {
        if (!validarConfiguracion(destinatario)) return;

        String asunto = "¡Cupo Aceptado! - " + (tituloViaje != null ? tituloViaje : "Expedición") + " | Club Nicaragüense de Montañismo";
        String contenidoHtml = construirPlantillaAprobacion(nombreUsuario, tituloViaje, idReserva, enlaceWhatsApp);

        enviarCorreoHtml(destinatario, asunto, contenidoHtml);
    }

    /**
     * Envía correo informando el rechazo de la solicitud junto con la justificación del administrador.
     */
    @Async
    public void enviarCorreoRechazo(
            String destinatario,
            String nombreUsuario,
            String tituloViaje,
            Long idReserva,
            String motivoRechazo
    ) {
        if (!validarConfiguracion(destinatario)) return;

        String asunto = "Actualización sobre tu reserva #" + idReserva + " | Club Nicaragüense de Montañismo";
        String contenidoHtml = construirPlantillaRechazo(nombreUsuario, tituloViaje, idReserva, motivoRechazo);

        enviarCorreoHtml(destinatario, asunto, contenidoHtml);
    }

    /**
     * Envía correo con el enlace seguro y temporal para restablecer la contraseña olvidada.
     */
    @Async
    public void enviarCorreoRecuperacionContrasena(
            String destinatario,
            String nombreUsuario,
            String enlaceRecuperacion
    ) {
        log.info("Iniciando envío de correo de recuperación hacia: {}", destinatario);
        if (!validarConfiguracion(destinatario)) {
            log.error("No se pudo enviar correo de recuperación a {}: JavaMailSender o variables SMTP no configuradas.", destinatario);
            return;
        }

        String asunto = "Restablece tu contraseña | Club Nicaragüense de Montañismo";
        String contenidoHtml = construirPlantillaRecuperacion(nombreUsuario, enlaceRecuperacion);

        enviarCorreoHtml(destinatario.trim(), asunto, contenidoHtml);
    }

    boolean validarConfiguracion(String destinatario) {
        if (destinatario == null || destinatario.isBlank()) {
            log.warn("Servicio de correo: Destinatario nulo o vacío. No se enviará correo.");
            return false;
        }

        // Si Resend o Brevo están configurados vía HTTPS, no se necesita JavaMailSender ni SMTP
        if (!resendApiKey.isBlank() || !brevoApiKey.isBlank()) {
            return true;
        }

        // Si se usa SMTP tradicional
        if (mailSender == null) {
            log.warn("Servicio de correo: JavaMailSender no está disponible ni se configuró proveedor HTTP (Resend o Brevo). No se enviará correo a {}", destinatario);
            return false;
        }
        if (mailHost.isBlank() || mailUsername.isBlank()) {
            log.warn("Servicio de correo: Variables SMTP no configuradas en el entorno (MAIL_HOST o MAIL_USERNAME vacíos). Configure las variables de correo para enviar correos a {}.", destinatario);
            return false;
        }
        return true;
    }

    public void enviarCorreoHtml(String destinatario, String asunto, String cuerpoHtml) {
        // 1. Intentar envío vía Resend HTTP API (HTTPS puerto 443 - Inmune a bloqueos de firewall SMTP de Render)
        if (!resendApiKey.isBlank()) {
            try {
                enviarViaResend(destinatario, asunto, cuerpoHtml);
                return;
            } catch (Exception ex) {
                log.error("Fallo al enviar correo vía Resend API hacia {}: {}", destinatario, ex.getMessage(), ex);
                if (mailSender == null || mailHost.isBlank()) {
                    return;
                }
            }
        }

        // 2. Intentar envío vía Brevo HTTP API (HTTPS puerto 443)
        if (!brevoApiKey.isBlank()) {
            try {
                enviarViaBrevo(destinatario, asunto, cuerpoHtml);
                return;
            } catch (Exception ex) {
                log.error("Fallo al enviar correo vía Brevo API hacia {}: {}", destinatario, ex.getMessage(), ex);
                if (mailSender == null || mailHost.isBlank()) {
                    return;
                }
            }
        }

        // 3. Envío tradicional vía JavaMailSender (SMTP)
        if (mailSender != null && !mailHost.isBlank()) {
            try {
                MimeMessage mensaje = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mensaje, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

                String remitenteFinal = !remitente.isBlank() ? remitente : mailUsername;
                helper.setFrom(remitenteFinal, "Club Nicaragüense de Montañismo");
                helper.setTo(destinatario.trim());
                helper.setSubject(asunto);
                helper.setText(cuerpoHtml, true);

                mailSender.send(mensaje);
                log.info("Correo enviado exitosamente vía SMTP a {} con asunto '{}'", destinatario, asunto);
            } catch (Exception e) {
                log.error("Error al enviar correo vía SMTP a {} con asunto '{}': {}. " +
                        "DIAGNÓSTICO IMPORTANTE: Render Free Tier bloquea por firewall los puertos SMTP 25, 465 y 587. " +
                        "Si la aplicación está alojada en Render Free, los paquetes a smtp.gmail.com:587 son descartados y la conexión expira con timeout. " +
                        "Para solucionarlo en Render Free Tier sin costo, configure RESEND_API_KEY o BREVO_API_KEY para enviar vía HTTPS (puerto 443).",
                        destinatario, asunto, e.getMessage(), e);
            }
        } else {
            log.warn("No hay proveedor de correo configurado para enviar a {}", destinatario);
        }
    }

    private void enviarViaResend(String destinatario, String asunto, String cuerpoHtml) {
        String fromAddress = !remitente.isBlank() && remitente.contains("@") && !remitente.contains("gmail.com")
                ? "Club Nicaragüense de Montañismo <" + remitente + ">"
                : "Club Nicaragüense de Montañismo <onboarding@resend.dev>";

        Map<String, Object> payload = Map.of(
                "from", fromAddress,
                "to", List.of(destinatario.trim()),
                "subject", asunto,
                "html", cuerpoHtml
        );

        restClient.post()
                .uri("https://api.resend.com/emails")
                .header("Authorization", "Bearer " + resendApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        log.info("Correo enviado exitosamente vía Resend HTTP API (HTTPS) a {} con asunto '{}'", destinatario, asunto);
    }

    private void enviarViaBrevo(String destinatario, String asunto, String cuerpoHtml) {
        String senderEmail = !remitente.isBlank() ? remitente : (!mailUsername.isBlank() ? mailUsername : "contacto@clubnicaraguensemontanismo.org");

        Map<String, Object> payload = Map.of(
                "sender", Map.of("name", "Club Nicaragüense de Montañismo", "email", senderEmail),
                "to", List.of(Map.of("email", destinatario.trim())),
                "subject", asunto,
                "htmlContent", cuerpoHtml
        );

        restClient.post()
                .uri("https://api.brevo.com/v3/smtp/email")
                .header("api-key", brevoApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        log.info("Correo enviado exitosamente vía Brevo HTTP API (HTTPS) a {} con asunto '{}'", destinatario, asunto);
    }

    public Map<String, Object> obtenerDiagnostico() {
        Map<String, Object> diag = new LinkedHashMap<>();

        boolean resendActivo = !resendApiKey.isBlank();
        boolean brevoActivo = !brevoApiKey.isBlank();
        boolean smtpConfigurado = !mailHost.isBlank() && !mailUsername.isBlank();

        diag.put("resendConfigurado", resendActivo);
        diag.put("brevoConfigurado", brevoActivo);
        diag.put("smtpConfigurado", smtpConfigurado);
        diag.put("smtpHost", mailHost);
        diag.put("smtpPort", mailPort);
        diag.put("smtpUsername", enmascararCorreo(mailUsername));
        diag.put("smtpPasswordPresente", !mailPassword.isBlank());

        boolean socketOk = false;
        String socketMensaje = "No probado";
        if (smtpConfigurado) {
            int port = (mailPort > 0) ? mailPort : 587;
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(mailHost, port), 2500);
                socketOk = true;
                socketMensaje = "Conexión TCP exitosa con " + mailHost + ":" + port;
            } catch (Exception ex) {
                socketOk = false;
                socketMensaje = "Conexión TCP fallida (" + ex.getClass().getSimpleName() + ": " + ex.getMessage() +
                        "). En Render Free Tier, los puertos SMTP 25, 465 y 587 están bloqueados por el firewall de Render. Use Resend o Brevo vía HTTPS (puerto 443).";
            }
        }
        diag.put("smtpSocketConectado", socketOk);
        diag.put("smtpSocketMensaje", socketMensaje);

        if (resendActivo) {
            diag.put("proveedorActivo", "RESEND_HTTP_API (HTTPS puerto 443)");
        } else if (brevoActivo) {
            diag.put("proveedorActivo", "BREVO_HTTP_API (HTTPS puerto 443)");
        } else if (smtpConfigurado) {
            diag.put("proveedorActivo", "SMTP_JAVA_MAIL (" + mailHost + ":" + mailPort + ")");
        } else {
            diag.put("proveedorActivo", "NINGUNO (Variables de correo no configuradas)");
        }

        return diag;
    }

    private String enmascararCorreo(String correo) {
        if (correo == null || correo.isBlank()) return "no configurado";
        int at = correo.indexOf('@');
        if (at <= 1) return "***" + correo.substring(Math.max(0, at));
        return correo.charAt(0) + "***" + correo.substring(at);
    }

    private String construirPlantillaAprobacion(
            String nombreUsuario,
            String tituloViaje,
            Long idReserva,
            String enlaceWhatsApp
    ) {
        String nombre = (nombreUsuario != null && !nombreUsuario.isBlank()) ? nombreUsuario : "Estimado(a) montañista";
        String viaje = (tituloViaje != null && !tituloViaje.isBlank()) ? tituloViaje : "la expedición";
        String botonWhatsappHtml = (enlaceWhatsApp != null && !enlaceWhatsApp.isBlank())
                ? "<div style=\"text-align: center; margin: 30px 0;\">"
                + "<a href=\"" + enlaceWhatsApp + "\" target=\"_blank\" style=\"background-color: #1A2B4C; color: #FFFFFF; text-decoration: none; padding: 14px 28px; border-radius: 8px; font-weight: bold; font-size: 16px; display: inline-block; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);\">"
                + "🏔️ Unirme al Grupo Oficial de WhatsApp"
                + "</a>"
                + "</div>"
                + "<p style=\"font-size: 14px; color: #64748B; text-align: center; margin-top: 5px;\">"
                + "O copia y pega este enlace en tu navegador:<br/>"
                + "<a href=\"" + enlaceWhatsApp + "\" style=\"color: #0284C7; word-break: break-all;\">" + enlaceWhatsApp + "</a>"
                + "</p>"
                : "<div style=\"background-color: #FEF3C7; border-left: 4px solid #D97706; padding: 14px; border-radius: 6px; margin: 20px 0;\">"
                + "<p style=\"margin: 0; color: #92400E; font-size: 14px;\">El enlace del grupo de WhatsApp se compartirá próximamente a través de nuestros canales oficiales.</p>"
                + "</div>";

        String contenido = "<div style=\"margin-bottom: 24px;\">"
                + "<div style=\"display: inline-block; background-color: #DEF7EC; color: #03543F; font-size: 13px; font-weight: bold; padding: 4px 12px; border-radius: 9999px; margin-bottom: 12px;\">"
                + "✓ CUPO CONFIRMADO"
                + "</div>"
                + "<h2 style=\"color: #1A2B4C; margin-top: 0; margin-bottom: 12px; font-size: 22px;\">¡Felicidades, tu cupo ha sido aceptado!</h2>"
                + "<p style=\"color: #334155; line-height: 1.6; font-size: 15px;\">"
                + "Hola <strong>" + escapeHtml(nombre) + "</strong>,"
                + "</p>"
                + "<p style=\"color: #334155; line-height: 1.6; font-size: 15px;\">"
                + "Hemos verificado con éxito tu comprobante de pago para la expedición <strong>" + escapeHtml(viaje) + "</strong> (Reserva #" + idReserva + "). Tu lugar en esta aventura está asegurado."
                + "</p>"
                + "<p style=\"color: #334155; line-height: 1.6; font-size: 15px;\">"
                + "Para coordinar los puntos de salida, recomendaciones del equipo, itinerario detallado y comunicarte con los guías y demás montañistas, ingresa al grupo oficial de WhatsApp del viaje:"
                + "</p>"
                + botonWhatsappHtml
                + "</div>";

        return envolverEnPlantillaBase(contenido);
    }

    private String construirPlantillaRechazo(
            String nombreUsuario,
            String tituloViaje,
            Long idReserva,
            String motivoRechazo
    ) {
        String nombre = (nombreUsuario != null && !nombreUsuario.isBlank()) ? nombreUsuario : "Estimado(a) montañista";
        String viaje = (tituloViaje != null && !tituloViaje.isBlank()) ? tituloViaje : "la expedición";
        String motivo = (motivoRechazo != null && !motivoRechazo.isBlank()) ? motivoRechazo : "No se especificó un motivo particular.";

        String contenido = "<div style=\"margin-bottom: 24px;\">"
                + "<div style=\"display: inline-block; background-color: #FDE8E8; color: #9B1C1C; font-size: 13px; font-weight: bold; padding: 4px 12px; border-radius: 9999px; margin-bottom: 12px;\">"
                + "SOLICITUD RECHAZADA"
                + "</div>"
                + "<h2 style=\"color: #1A2B4C; margin-top: 0; margin-bottom: 12px; font-size: 22px;\">Actualización sobre tu reserva #" + idReserva + "</h2>"
                + "<p style=\"color: #334155; line-height: 1.6; font-size: 15px;\">"
                + "Hola <strong>" + escapeHtml(nombre) + "</strong>,"
                + "</p>"
                + "<p style=\"color: #334155; line-height: 1.6; font-size: 15px;\">"
                + "Lamentamos comunicarte que tu solicitud de reserva para <strong>" + escapeHtml(viaje) + "</strong> no pudo ser aprobada por nuestro equipo administrativo."
                + "</p>"
                + "<div style=\"background-color: #FFF5F5; border-left: 4px solid #E02424; padding: 16px; border-radius: 6px; margin: 20px 0;\">"
                + "<strong style=\"color: #9B1C1C; display: block; margin-bottom: 6px; font-size: 14px;\">Justificación del rechazo:</strong>"
                + "<p style=\"margin: 0; color: #4B5563; font-size: 14px; line-height: 1.5;\">" + escapeHtml(motivo) + "</p>"
                + "</div>"
                + "<p style=\"color: #334155; line-height: 1.6; font-size: 15px;\">"
                + "Si consideras que se trata de un error o deseas subsanar tu comprobante, por favor ponte en contacto directamente con nosotros a través de nuestros canales oficiales que encontrarás al pie de este correo."
                + "</p>"
                + "</div>";

        return envolverEnPlantillaBase(contenido);
    }

    private String construirPlantillaRecuperacion(
            String nombreUsuario,
            String enlaceRecuperacion
    ) {
        String nombre = (nombreUsuario != null && !nombreUsuario.isBlank()) ? nombreUsuario : "Estimado(a) montañista";

        String contenido = "<div style=\"margin-bottom: 24px;\">"
                + "<div style=\"display: inline-block; background-color: #FEF3C7; color: #92400E; font-size: 13px; font-weight: bold; padding: 4px 12px; border-radius: 9999px; margin-bottom: 12px;\">"
                + "🔒 SEGURIDAD DE LA CUENTA"
                + "</div>"
                + "<h2 style=\"color: #1A2B4C; margin-top: 0; margin-bottom: 12px; font-size: 22px;\">Restablece tu contraseña</h2>"
                + "<p style=\"color: #334155; line-height: 1.6; font-size: 15px;\">"
                + "Hola <strong>" + escapeHtml(nombre) + "</strong>,"
                + "</p>"
                + "<p style=\"color: #334155; line-height: 1.6; font-size: 15px;\">"
                + "Recibimos una solicitud para restablecer la contraseña asociada a tu cuenta en el Club Nicaragüense de Montañismo. Para crear una nueva clave y acceder nuevamente a tu perfil y reservas, haz clic en el siguiente botón:"
                + "</p>"
                + "<div style=\"text-align: center; margin: 30px 0;\">"
                + "<a href=\"" + enlaceRecuperacion + "\" target=\"_blank\" style=\"background-color: #1A2B4C; color: #FFFFFF; text-decoration: none; padding: 14px 28px; border-radius: 8px; font-weight: bold; font-size: 16px; display: inline-block; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);\">"
                + "Restablecer mi contraseña"
                + "</a>"
                + "</div>"
                + "<p style=\"font-size: 14px; color: #64748B; text-align: center; margin-top: 5px;\">"
                + "O copia y pega este enlace seguro en tu navegador:<br/>"
                + "<a href=\"" + enlaceRecuperacion + "\" style=\"color: #0284C7; word-break: break-all;\">" + enlaceRecuperacion + "</a>"
                + "</p>"
                + "<div style=\"background-color: #F1F5F9; border-left: 4px solid #64748B; padding: 14px; border-radius: 6px; margin: 25px 0;\">"
                + "<p style=\"margin: 0; color: #475569; font-size: 13px; line-height: 1.5;\">"
                + "⚠️ <strong>Aviso de seguridad:</strong> Este enlace es de un solo uso y expirará automáticamente en <strong>1 hora</strong>. Si no solicitaste este cambio, puedes ignorar este mensaje; tu contraseña actual continuará siendo segura."
                + "</p>"
                + "</div>"
                + "</div>";

        return envolverEnPlantillaBase(contenido);
    }

    private String envolverEnPlantillaBase(String cuerpoContenido) {
        return "<!DOCTYPE html>"
                + "<html lang=\"es\">"
                + "<head>"
                + "<meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "<title>Club Nicaragüense de Montañismo</title>"
                + "</head>"
                + "<body style=\"margin: 0; padding: 0; background-color: #F8FAFC; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;\">"
                + "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout: fixed;\">"
                + "<tr>"
                + "<td align=\"center\" style=\"padding: 30px 15px;\">"
                + "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px; background-color: #FFFFFF; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05); border: 1px solid #E2E8F0;\">"
                // Header
                + "<tr>"
                + "<td style=\"background-color: #1A2B4C; padding: 25px 30px; text-align: center;\">"
                + "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">"
                + "<tr>"
                + "<td align=\"center\">"
                + "<img src=\"" + LOGO_URL + "\" alt=\"CNM Logo\" width=\"55\" height=\"55\" style=\"display: block; margin-bottom: 10px; border-radius: 8px;\"/>"
                + "<div style=\"color: #D4A373; font-size: 11px; letter-spacing: 2px; text-transform: uppercase; font-weight: bold;\">EXPEDICIONES Y AVENTURA</div>"
                + "<h1 style=\"color: #FFFFFF; margin: 4px 0 0 0; font-size: 20px; font-weight: 700;\">Club Nicaragüense de Montañismo</h1>"
                + "</td>"
                + "</tr>"
                + "</table>"
                + "</td>"
                + "</tr>"
                // Body
                + "<tr>"
                + "<td style=\"padding: 35px 30px;\">"
                + cuerpoContenido
                + "</td>"
                + "</tr>"
                // Footer
                + "<tr>"
                + "<td style=\"background-color: #0F172A; padding: 30px; color: #94A3B8; text-align: center; font-size: 13px;\">"
                + "<p style=\"color: #E2E8F0; font-weight: bold; margin: 0 0 10px 0; font-size: 14px;\">Contactos Oficiales</p>"
                + "<p style=\"margin: 0 0 15px 0; line-height: 1.8;\">"
                + "📱 WhatsApp: <a href=\"" + WHATSAPP_LINK + "\" style=\"color: #38BDF8; text-decoration: none;\">" + WHATSAPP_OFICIAL + "</a><br/>"
                + "✉️ Correo: <a href=\"mailto:" + ((remitente != null && !remitente.isBlank()) ? remitente : ((mailUsername != null && !mailUsername.isBlank()) ? mailUsername : "contacto@clubnicaraguensemontanismo.org")) + "\" style=\"color: #38BDF8; text-decoration: none;\">" + ((remitente != null && !remitente.isBlank()) ? remitente : ((mailUsername != null && !mailUsername.isBlank()) ? mailUsername : "contacto@clubnicaraguensemontanismo.org")) + "</a><br/>"
                + "📍 Managua, Nicaragua"
                + "</p>"
                + "<div style=\"margin: 15px 0;\">"
                + "<a href=\"" + FACEBOOK_LINK + "\" style=\"color: #D4A373; text-decoration: none; margin: 0 10px; font-weight: 500;\">Facebook</a> | "
                + "<a href=\"" + INSTAGRAM_LINK + "\" style=\"color: #D4A373; text-decoration: none; margin: 0 10px; font-weight: 500;\">Instagram</a> | "
                + "<a href=\"" + WHATSAPP_LINK + "\" style=\"color: #D4A373; text-decoration: none; margin: 0 10px; font-weight: 500;\">WhatsApp</a>"
                + "</div>"
                + "<p style=\"font-size: 11px; color: #64748B; margin: 20px 0 0 0;\">"
                + "Este es un correo institucional automático generado por la plataforma de reservas del Club Nicaragüense de Montañismo."
                + "</p>"
                + "</td>"
                + "</tr>"
                + "</table>"
                + "</td>"
                + "</tr>"
                + "</table>"
                + "</body>"
                + "</html>";
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
