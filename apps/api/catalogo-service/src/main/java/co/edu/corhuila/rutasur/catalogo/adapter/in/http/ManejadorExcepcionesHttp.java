package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import co.edu.corhuila.rutasur.catalogo.domain.exception.ExcepcionReglaNegocio;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce reglas de Catálogo a respuestas uniformes e internacionalizadas.
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class ManejadorExcepcionesHttp {
    private final MessageSource mensajes;

    @ExceptionHandler(ExcepcionReglaNegocio.class)
    public ResponseEntity<RespuestaGeneral<Void>> manejar(ExcepcionReglaNegocio error) {
        String mensaje = mensajes.getMessage(error.getMessage(), null, "Solicitud inválida.", LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(RespuestaGeneral.error("409", error.getMessage(), mensaje));
    }
}
