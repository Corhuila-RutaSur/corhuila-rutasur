package co.edu.corhuila.rutasur.consultas.adapter.in.http;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Habilita Angular local mientras la entrada por Gateway se encuentra en transición. */
@Configuration
public class ConfiguracionCors implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registro) {
        registro.addMapping("/**").allowedOrigins("http://localhost:4200", "http://localhost:4201")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*").exposedHeaders("Authorization", "X-Correlation-Id")
                .allowCredentials(true).maxAge(3600);
    }
}
