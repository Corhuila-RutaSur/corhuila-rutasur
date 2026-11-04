package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.identidad.domain.exception.ExcepcionAutenticacion;

import java.util.Locale;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce fallos técnicos y de negocio a respuestas HTTP internacionalizadas.
 */
@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ManejadorExcepcionesHttp {
    private final MessageSource mensajes;

    /**
     * Responde reglas de negocio sin filtrar la clave de internacionalización.
     */
    @ExceptionHandler(ExcepcionReglaNegocio.class)
    public ResponseEntity<RespuestaGeneral<Void>> manejarReglaNegocio(ExcepcionReglaNegocio excepcion) {
        return responder(HttpStatus.CONFLICT, excepcion.getMessage());
    }

    /**
     * Responde 401 para tokens vencidos, revocados o criptográficamente inválidos.
     */
    @ExceptionHandler(ExcepcionAutenticacion.class)
    public ResponseEntity<RespuestaGeneral<Void>> manejarAutenticacion(ExcepcionAutenticacion excepcion) {
        return responder(HttpStatus.UNAUTHORIZED, excepcion.getMessage());
    }

    /**
     * Devuelve el primer error de formato informado por Bean Validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaGeneral<Void>> manejarSolicitudInvalida(MethodArgumentNotValidException excepcion) {
        FieldError error = excepcion.getBindingResult().getFieldError();
        String codigo = error == null ? "solicitud.invalida" : error.getDefaultMessage();
        return responder(HttpStatus.BAD_REQUEST, codigo);
    }

    /**
     * Evita exponer detalles técnicos ante fallos no previstos.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaGeneral<Void>> manejarErrorNoControlado(Exception excepcion) {
        log.error("servicio=identidad evento=error_no_controlado tipo={}", excepcion.getClass().getSimpleName(), excepcion);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "error.interno");
    }

    private ResponseEntity<RespuestaGeneral<Void>> responder(HttpStatus estado, String codigo) {
        Locale idioma = LocaleContextHolder.getLocale();
        String mensajePredeterminado = mensajes.getMessage("error.interno", null, idioma);
        String mensaje = mensajes.getMessage(codigo, null, mensajePredeterminado, idioma);
        return ResponseEntity.status(estado).body(RespuestaGeneral.error(String.valueOf(estado.value()), codigo, mensaje));
    }
}
