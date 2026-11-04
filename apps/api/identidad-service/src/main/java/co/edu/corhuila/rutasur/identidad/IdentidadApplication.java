package co.edu.corhuila.rutasur.identidad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

/**
 * Microservicio dueño de usuarios, roles, sesiones y tokens de RutaSur V2.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class IdentidadApplication {
    /** Provee serialización consistente para sesiones y tokens. */
    @Bean ObjectMapper objectMapper() { return new ObjectMapper().findAndRegisterModules(); }

    /**
     * Inicia el microservicio de Identidad.
     */
    public static void main(String[] argumentos) {
        SpringApplication.run(IdentidadApplication.class, argumentos);
    }
}
