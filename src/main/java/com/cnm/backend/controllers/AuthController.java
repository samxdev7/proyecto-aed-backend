package com.cnm.backend.controllers;

import com.cnm.backend.dto.auth.GoogleAuthRequestDto;
import com.cnm.backend.dto.auth.LoginRequestDto;
import com.cnm.backend.dto.auth.LoginResponseDto;
import com.cnm.backend.dto.auth.MensajeResponseDto;
import com.cnm.backend.dto.auth.RecuperarContrasenaRequestDto;
import com.cnm.backend.dto.auth.RegistroUsuarioRequestDto;
import com.cnm.backend.dto.auth.RestablecerContrasenaRequestDto;
import com.cnm.backend.dto.usuario.UsuarioPerfilResponseDto;
import com.cnm.backend.service.AuthService;
import com.cnm.backend.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    public AuthController(AuthService authService, EmailService emailService) {
        this.authService = authService;
        this.emailService = emailService;
    }

    @GetMapping("/diagnostico-email")
    public ResponseEntity<Map<String, Object>> diagnosticoEmail() {
        return ResponseEntity.ok(emailService.obtenerDiagnostico());
    }

    @PostMapping("/diagnostico-email/enviar-prueba")
    public ResponseEntity<Map<String, Object>> enviarPruebaEmail(
            @RequestParam(required = false) String destinatario) {
        Map<String, Object> res = new LinkedHashMap<>();
        String dest = (destinatario != null && !destinatario.isBlank()) ? destinatario.trim() : "mrmrmrbeastbro@gmail.com";
        try {
            emailService.enviarCorreoHtml(dest, "Prueba Diagnóstico CNM",
                    "<h3>Prueba de envío</h3><p>Este correo confirma que el despacho hacia " + dest + " funciona correctamente.</p>");
            res.put("exito", true);
            res.put("mensaje", "Petición de envío procesada hacia " + dest);
        } catch (Exception e) {
            res.put("exito", false);
            res.put("error", e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        res.put("diagnostico", emailService.obtenerDiagnostico());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioPerfilResponseDto> registrar(
            @Valid @RequestBody RegistroUsuarioRequestDto request) {
        UsuarioPerfilResponseDto response = authService.registro(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponseDto> loginConGoogle(
            @Valid @RequestBody GoogleAuthRequestDto request) {
        LoginResponseDto response = authService.autenticarConGoogle(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<MensajeResponseDto> solicitarRecuperacion(
            @Valid @RequestBody RecuperarContrasenaRequestDto request) {
        MensajeResponseDto response = authService.solicitarRecuperacion(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<MensajeResponseDto> restablecerContrasena(
            @Valid @RequestBody RestablecerContrasenaRequestDto request) {
        MensajeResponseDto response = authService.restablecerContrasena(request);
        return ResponseEntity.ok(response);
    }
}
