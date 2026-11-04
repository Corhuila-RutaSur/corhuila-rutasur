package co.edu.corhuila.rutasur.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI rutasurOpenApi() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Access token JWT. Las rutas públicas no requieren este esquema.")))
                .info(new Info()
                        .title("RutaSur API")
                        .description("API para la gestión de experiencias turísticas, reservas y operación de RutaSur.")
                        .version("v1")
                        .license(new License().name("Uso académico")));
    }
}
