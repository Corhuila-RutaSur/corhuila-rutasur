package co.edu.corhuila.rutasur.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada HTTP de RutaSur V2.
 */
@SpringBootApplication
public class GatewayApplication {

    /**
     * Inicia el API Gateway local.
     */
    public static void main(String[] argumentos) {
        SpringApplication.run(GatewayApplication.class, argumentos);
    }
}
