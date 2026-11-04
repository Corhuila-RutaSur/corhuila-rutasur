package co.edu.corhuila.rutasur.operacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio dueño de recursos y ejecución turística.
 */
@SpringBootApplication
public class OperacionApplication {

    /**
     * Inicia el microservicio de Operación.
     */
    public static void main(String[] argumentos) {
        SpringApplication.run(OperacionApplication.class, argumentos);
    }
}
