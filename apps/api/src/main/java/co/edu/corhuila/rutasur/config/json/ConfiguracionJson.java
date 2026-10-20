package co.edu.corhuila.rutasur.config.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra el serializador JSON compartido por las respuestas HTTP de RutaSur.
 */
@Configuration
public class ConfiguracionJson {

    /**
     * Provee el serializador requerido por respuestas de seguridad y API.
     */
    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
