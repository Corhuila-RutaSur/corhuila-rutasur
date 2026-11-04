package co.edu.corhuila.rutasur.reservas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

/**
 * Microservicio dueño de salidas, cupos, bloqueos y reservas.
 */
@SpringBootApplication
@EnableScheduling
public class ReservasApplication {
    /** Provee serialización consistente para eventos de reserva. */
    @Bean ObjectMapper objectMapper() { return new ObjectMapper().findAndRegisterModules(); }

    /**
     * Inicia el microservicio de Reservas.
     */
    public static void main(String[] argumentos) {
        SpringApplication.run(ReservasApplication.class, argumentos);
    }
}
