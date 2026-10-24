package com.cnm.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Import para auto importacion del env file en el root del proyecto
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class CnmBackendApplication {

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

        dotenv.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE).forEach(e -> { 
            if (e.getKey() != null && e.getValue() != null)
                System.setProperty(e.getKey(), e.getValue());
        });

        SpringApplication.run(CnmBackendApplication.class, args);
	}
}
