package co.edu.corhuila.rutasur.notificaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

/**
 * Microservicio serverless-compatible dueño de entregas y reintentos.
 */
@SpringBootApplication
@EnableScheduling
public class NotificacionesApplication {
    /** Provee serialización consistente para mensajes consumidos. */
    @Bean ObjectMapper objectMapper() { return new ObjectMapper().findAndRegisterModules(); }

    /**
     * Inicia el microservicio de Notificaciones para ejecución local.
     */
    public static void main(String[] argumentos) {
        SpringApplication.run(NotificacionesApplication.class, argumentos);
    }
}
