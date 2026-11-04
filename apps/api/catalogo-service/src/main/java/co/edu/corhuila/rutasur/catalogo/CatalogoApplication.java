package co.edu.corhuila.rutasur.catalogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

/**
 * Microservicio dueño del catálogo comercial de experiencias.
 */
@SpringBootApplication
@EnableScheduling
public class CatalogoApplication {
    /** Provee serialización consistente para eventos Outbox. */
    @Bean ObjectMapper objectMapper() { return new ObjectMapper().findAndRegisterModules(); }

    /**
     * Inicia el microservicio de Catálogo.
     */
    public static void main(String[] argumentos) {
        SpringApplication.run(CatalogoApplication.class, argumentos);
    }
}
