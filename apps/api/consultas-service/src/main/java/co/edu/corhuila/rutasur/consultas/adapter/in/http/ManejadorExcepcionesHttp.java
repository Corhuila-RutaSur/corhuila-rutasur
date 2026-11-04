package co.edu.corhuila.rutasur.consultas.adapter.in.http;

import co.edu.corhuila.rutasur.consultas.adapter.out.persistence.ConsultaCatalogoPublicoAdaptador.ExperienciaNoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce reglas de lectura a la respuesta uniforme pública.
 */
@RestControllerAdvice
public class ManejadorExcepcionesHttp {
    @ExceptionHandler(ExperienciaNoEncontradaException.class)
    public ResponseEntity<RespuestaGeneral<Void>> noEncontrada(ExperienciaNoEncontradaException error) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RespuestaGeneral<>("404", "experiencia.no.encontrada", error.getMessage(), null));
    }
}
