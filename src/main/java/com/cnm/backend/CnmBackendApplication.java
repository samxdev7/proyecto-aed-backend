package com.cnm.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Import para auto importacion del env file en el root del proyecto
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class CnmBackendApplication {

    static {
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

        dotenv.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE).forEach(e -> { 
            if (e.getKey() != null && e.getValue() != null && System.getProperty(e.getKey()) == null)
                System.setProperty(e.getKey(), e.getValue());
        });
    }

	public static void main(String[] args) {
        SpringApplication.run(CnmBackendApplication.class, args);
	}
}
