package com.cnm.backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Políticas CORS para Despliegue")
class CorsConfigurationTest {

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(null);
    }

    @Test
    @DisplayName("Por defecto o con '*', permite cualquier origen de despliegue (incluyendo Vercel y localhost)")
    void cors_porDefecto_permiteOrigenesDeDespliegue() {
        ReflectionTestUtils.setField(securityConfig, "corsOrigenes", "*");
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        MockHttpServletRequest requestVercel = new MockHttpServletRequest("GET", "/viajes");
        requestVercel.addHeader("Origin", "https://cnm-inscripcion.vercel.app");

        CorsConfiguration config = source.getCorsConfiguration(requestVercel);
        assertNotNull(config, "Debe existir configuración CORS para rutas /viajes");
        assertEquals("https://cnm-inscripcion.vercel.app", config.checkOrigin("https://cnm-inscripcion.vercel.app"));
        assertEquals("https://cualquier-frontend-preview.vercel.app", config.checkOrigin("https://cualquier-frontend-preview.vercel.app"));
        assertEquals("http://localhost:3000", config.checkOrigin("http://localhost:3000"));
    }

    @Test
    @DisplayName("Permite múltiples orígenes separados por comas y patrones wildcard de despliegue")
    void cors_conMultiplesOrigenesYWildcards_permiteTodos() {
        ReflectionTestUtils.setField(securityConfig, "corsOrigenes",
                "http://localhost:3000, https://cnm-inscripcion.vercel.app, https://*.vercel.app");
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/auth/login");
        request.addHeader("Origin", "https://cnm-inscripcion.vercel.app");

        CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config);
        assertEquals("https://cnm-inscripcion.vercel.app", config.checkOrigin("https://cnm-inscripcion.vercel.app"));
        assertEquals("https://preview-123.vercel.app", config.checkOrigin("https://preview-123.vercel.app"));
        assertEquals("http://localhost:3000", config.checkOrigin("http://localhost:3000"));
        assertNull(config.checkOrigin("https://sitio-malicioso.com"), "No debe permitir orígenes ajenos no especificados");
    }

    @Test
    @DisplayName("Auto-expande comodines para despliegues preview de Vercel cuando solo se configura el dominio base")
    void cors_conSoloDominioBaseVercel_permitePreviewsDinamicos() {
        ReflectionTestUtils.setField(securityConfig, "corsOrigenes", "https://cnm-inscripcion.vercel.app");
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/viajes");
        CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config);

        // Producción debe funcionar
        assertEquals("https://cnm-inscripcion.vercel.app", config.checkOrigin("https://cnm-inscripcion.vercel.app"));

        // Previews de Vercel (con hash y rama generados por Vercel) deben ser permitidos automáticamente
        assertEquals("https://cnm-inscripcion-1c4seq0qg-anti-pseint.vercel.app",
                config.checkOrigin("https://cnm-inscripcion-1c4seq0qg-anti-pseint.vercel.app"));
        assertEquals("https://cnm-inscripcion-git-beta-ver-anti-pseint.vercel.app",
                config.checkOrigin("https://cnm-inscripcion-git-beta-ver-anti-pseint.vercel.app"));

        // No debe permitir sitios maliciosos arbitrarios
        assertNull(config.checkOrigin("https://malicious-site.com"));
    }


    @Test
    @DisplayName("Permite todos los encabezados HTTP (Accept, Authorization, Content-Type, etc.) para evitar rechazos en preflight")
    void cors_permiteTodosLosHeadersEnPreflight() {
        ReflectionTestUtils.setField(securityConfig, "corsOrigenes", "*");
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/auth/login");
        request.addHeader("Origin", "https://cnm-inscripcion.vercel.app");
        request.addHeader("Access-Control-Request-Method", "POST");
        request.addHeader("Access-Control-Request-Headers", "authorization,content-type,accept,x-requested-with");

        CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config);

        List<String> allowedHeaders = config.getAllowedHeaders();
        assertNotNull(allowedHeaders);
        assertTrue(allowedHeaders.contains("*") || allowedHeaders.containsAll(List.of("Authorization", "Content-Type", "Accept")),
                "Debe permitir '*' o encabezados estándar de preflight como Accept y Authorization");
    }

    @Test
    @DisplayName("Permite todos los métodos HTTP REST estándar (GET, POST, PUT, PATCH, DELETE, OPTIONS)")
    void cors_permiteMetodosRestEstándar() {
        ReflectionTestUtils.setField(securityConfig, "corsOrigenes", "*");
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/reservas");
        CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config);

        List<String> metodos = config.getAllowedMethods();
        assertNotNull(metodos);
        assertTrue(metodos.containsAll(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")));
    }

    @Test
    @DisplayName("Habilita credenciales (allowCredentials=true) y expone encabezados relevantes")
    void cors_habilitaCredencialesYExposedHeaders() {
        ReflectionTestUtils.setField(securityConfig, "corsOrigenes", "*");
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/usuarios/me");
        CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config);

        assertTrue(Boolean.TRUE.equals(config.getAllowCredentials()), "allowCredentials debe ser true para sesiones y tokens");
        assertNotNull(config.getExposedHeaders(), "Debe exponer encabezados para clientes frontend");
        assertTrue(config.getExposedHeaders().contains("Authorization"));
    }

    @Test
    @DisplayName("Si la variable corsOrigenes está vacía o en blanco, aplica fallback seguro a '*'")
    void cors_cadenaVacia_aplicaFallbackPermisivo() {
        ReflectionTestUtils.setField(securityConfig, "corsOrigenes", "   ");
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/viajes");
        CorsConfiguration config = source.getCorsConfiguration(request);
        assertNotNull(config);
        assertEquals("https://cnm-inscripcion.vercel.app", config.checkOrigin("https://cnm-inscripcion.vercel.app"));
    }
}
