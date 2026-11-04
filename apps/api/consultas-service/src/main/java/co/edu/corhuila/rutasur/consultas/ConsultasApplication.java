package co.edu.corhuila.rutasur.consultas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

/**
 * Microservicio dueño de las proyecciones CQRS de lectura.
 */
@SpringBootApplication
@EnableScheduling
public class ConsultasApplication {
    /** Provee serialización consistente para envelopes de integración. */
    @Bean ObjectMapper objectMapper() { return new ObjectMapper().findAndRegisterModules(); }

    /**
     * Inicia el microservicio de Consultas.
     */
    public static void main(String[] argumentos) {
        SpringApplication.run(ConsultasApplication.class, argumentos);
    }
}
