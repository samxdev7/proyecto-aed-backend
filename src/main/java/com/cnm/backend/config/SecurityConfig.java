package com.cnm.backend.config;

import com.cnm.backend.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * CORS para el frontend. Por defecto acepta CUALQUIER origen (patrón "*") para que
     * funcione out-of-box con cualquier puerto que asigne `next dev` (3000, 3001, …).
     * En producción: definir CORS_ORIGINS en .env con la lista separada por comas,
     * p.ej. "https://cnm-frontend.vercel.app,https://cnm.org".
     */
    @Value("${app.cors.origins:*}")
    private String corsOrigenes;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origenes = Arrays.stream(corsOrigenes.split(","))
                .map(String::trim)
                .filter(origen -> !origen.isEmpty())
                .toList();
        config.setAllowedOriginPatterns(origenes);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .authorizeHttpRequests(auth -> auth
                // Públicos
                .requestMatchers(HttpMethod.POST, "/auth/registro", "/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/viajes", "/viajes/*/campos-formulario").permitAll()
                .requestMatchers(HttpMethod.GET, "/viajes/{idViaje}").permitAll()

                // Solo ADMIN (orden importa: reglas específicas antes de genéricas)
                .requestMatchers(HttpMethod.POST, "/viajes").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/viajes/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/viajes/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/viajes/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/usuarios").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/reservas").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/reservas/*/aprobar").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/reservas/*/rechazar").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/estadisticas/**").hasRole("ADMINISTRADOR")

                // Cualquier usuario autenticado (ANTES de las reglas genéricas de ADMIN)
                .requestMatchers(HttpMethod.GET, "/usuarios/me").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/usuarios/me").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/usuarios/me/notificaciones").authenticated()
                .requestMatchers(HttpMethod.GET, "/usuarios/me/historial").authenticated()
                .requestMatchers(HttpMethod.GET, "/usuarios/me/historial/{idReserva}").authenticated()
                .requestMatchers(HttpMethod.GET, "/notificaciones/me").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/notificaciones/{idNotificacion}/leida").authenticated()
                .requestMatchers(HttpMethod.POST, "/notificaciones/suscripcion").authenticated()
                .requestMatchers(HttpMethod.POST, "/reservas").authenticated()
                .requestMatchers(HttpMethod.GET, "/reservas/{idReserva}").authenticated()

                // ADMIN genérico DESPUÉS de las reglas /me
                .requestMatchers(HttpMethod.GET, "/usuarios/{idUsuario}").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/usuarios/*/historial").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/usuarios/*/historial/*").hasRole("ADMINISTRADOR")

                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
