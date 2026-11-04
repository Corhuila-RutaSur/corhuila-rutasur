package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import co.edu.corhuila.rutasur.reservas.domain.exception.ExcepcionReglaNegocio;
import lombok.*;
import org.springframework.context.*;
import org.springframework.context.i18n.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * Traduce reglas de Reservas a respuestas legibles y uniformes.
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class ManejadorExcepcionesHttp {
    private final MessageSource mensajes;

    @ExceptionHandler(ExcepcionReglaNegocio.class)
    public ResponseEntity<RespuestaGeneral<Void>> manejar(ExcepcionReglaNegocio e) {
        String m = mensajes.getMessage(e.getMessage(), null, "No fue posible procesar la reserva.", LocaleContextHolder.getLocale());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(RespuestaGeneral.error("409", e.getMessage(), m));
    }
}
