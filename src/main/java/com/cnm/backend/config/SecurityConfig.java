package com.cnm.backend.config;

import com.cnm.backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Públicos
                .requestMatchers(HttpMethod.POST, "/auth/registro", "/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/viajes", "/viajes/*/campos-formulario").permitAll()
                .requestMatchers(HttpMethod.GET, "/viajes/{idViaje}").permitAll()

                // Solo ADMIN
                .requestMatchers(HttpMethod.POST, "/viajes").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/viajes/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/viajes/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/viajes/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/usuarios").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/usuarios/{idUsuario}").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/usuarios/*/historial").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/usuarios/*/historial/*").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/reservas").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/reservas/*/aprobar").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PATCH, "/reservas/*/rechazar").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/estadisticas/**").hasRole("ADMINISTRADOR")

                // Cualquier usuario autenticado
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
