package com.cnm.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Import para auto importacion del env file en el root del proyecto
import io.github.cdimascio.dotenv.Dotenv;
@SpringBootApplication
public class CnmBackendApplication {

	public static void main(String[] args) {
    Dotenv dotenv = Dotenv.load();
    System.setProperty("DB_URL", dotenv.get("DB_URL"));
    System.setProperty("DB_USER", dotenv.get("DB_USER"));
    System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
    System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
		SpringApplication.run(CnmBackendApplication.class, args);
	}

}
