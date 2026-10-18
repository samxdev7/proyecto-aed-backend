package com.cnm.backend.config;

import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de ciclo de vida de Flyway.
 * Ejecuta flyway.repair() antes de flyway.migrate() para limpiar automáticamente
 * registros de migraciones fallidas en 'flyway_schema_history' y realinear checksums.
 */
@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
