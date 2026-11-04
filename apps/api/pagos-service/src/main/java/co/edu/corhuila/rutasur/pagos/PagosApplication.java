package co.edu.corhuila.rutasur.pagos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;

/**
 * Microservicio dueño de pagos e intentos de cobro.
 */
@SpringBootApplication
@EnableScheduling
public class PagosApplication {
    /** Provee serialización consistente para resultados financieros y eventos. */
    @Bean ObjectMapper objectMapper() { return new ObjectMapper().findAndRegisterModules(); }

    /**
     * Inicia el microservicio de Pagos.
     */
    public static void main(String[] argumentos) {
        SpringApplication.run(PagosApplication.class, argumentos);
    }
}
