package com.cnm.backend.config;

import com.cnm.backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
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
