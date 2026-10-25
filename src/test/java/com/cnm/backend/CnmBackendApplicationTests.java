package com.cnm.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class CnmBackendApplicationTests {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> registry.add(entry.getKey(), entry::getValue));
    }

    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @org.springframework.beans.factory.annotation.Autowired
    private com.cnm.backend.service.NotificacionService notificacionService;

    @org.springframework.beans.factory.annotation.Autowired
    private com.cnm.backend.repository.NotificacionRepository notificacionRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private com.cnm.backend.repository.UsuarioRepository usuarioRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private com.cnm.backend.repository.ViajeRepository viajeRepository;

	@Test
	void contextLoads() {
	}
}
