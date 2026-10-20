package co.edu.corhuila.rutasur.exception;

import jakarta.servlet.http.HttpServletRequest;
import co.edu.corhuila.rutasur.dto.response.RespuestaGeneral;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Traduce las excepciones del backend al contrato de error internacionalizado de RutaSur.
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ManejadorGlobalExcepciones {

    private final ResolvedorMensajes resolvedorMensajes;

    /**
     * Expone los errores de validacion por campo.
     *
     * @param excepcion detalle de Bean Validation.
     * @param solicitud solicitud HTTP actual.
     * @return respuesta HTTP 400 con los campos invalidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaGeneral<Map<String, String>>> manejarValidacion(
            MethodArgumentNotValidException excepcion, HttpServletRequest solicitud) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError errorCampo : excepcion.getBindingResult().getFieldErrors()) {
            errores.put(errorCampo.getField(), errorCampo.getDefaultMessage());
        }
        return construirRespuesta(HttpStatus.BAD_REQUEST, "validacion.error", errores);
    }

    /**
     * Expone reglas de negocio incumplidas.
     *
     * @param excepcion regla que no se pudo satisfacer.
     * @param solicitud solicitud HTTP actual.
     * @return respuesta HTTP 409 para duplicados o 404 para recursos ausentes.
     */
    @ExceptionHandler(ExcepcionReglaNegocio.class)
    public ResponseEntity<RespuestaGeneral<Void>> manejarReglaNegocio(
            ExcepcionReglaNegocio excepcion, HttpServletRequest solicitud) {
        HttpStatus estado = resolverEstadoRegla(excepcion.getCodigo());
        log.warn("evento=regla_negocio_rechazada codigo={} estado={} ruta={}", excepcion.getCodigo(), estado.value(), solicitud.getRequestURI());
        return construirRespuesta(estado, excepcion.getCodigo(), null);
    }

    /**
     * Evita exponer detalles de restricciones de base de datos.
     *
     * @param excepcion error de integridad detectado por persistencia.
     * @param solicitud solicitud HTTP actual.
     * @return respuesta HTTP 409 generica.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RespuestaGeneral<Void>> manejarIntegridad(
            DataIntegrityViolationException excepcion, HttpServletRequest solicitud) {
        return construirRespuesta(HttpStatus.CONFLICT, "error.integridad", null);
    }

    /**
     * Impide filtrar detalles internos ante errores no controlados.
     *
     * @param excepcion error inesperado.
     * @param solicitud solicitud HTTP actual.
     * @return respuesta HTTP 500 generica.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaGeneral<Void>> manejarErrorInesperado(Exception excepcion, HttpServletRequest solicitud) {
        log.error("evento=error_no_controlado ruta={} tipo={}", solicitud.getRequestURI(), excepcion.getClass().getSimpleName(), excepcion);
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "error.interno", null);
    }

    /**
     * Construye el formato uniforme de error HTTP.
     *
     * @param estado estado HTTP de salida.
     * @param codigo clave funcional.
     * @param solicitud solicitud HTTP actual.
     * @param errores detalle opcional por campo.
     * @return respuesta estructurada de error.
     */
    private <T> ResponseEntity<RespuestaGeneral<T>> construirRespuesta(
            HttpStatus estado, String codigo, T data) {
        RespuestaGeneral<T> respuesta = new RespuestaGeneral<>(
                String.valueOf(estado.value()), codigo, resolvedorMensajes.resolver(codigo), data);
        return ResponseEntity.status(estado).body(respuesta);
    }

    /** Asigna el estado HTTP apropiado sin exponer detalles de seguridad. */
    private HttpStatus resolverEstadoRegla(String codigo) {
        if (codigo.startsWith("autenticacion.")) {
            return HttpStatus.UNAUTHORIZED;
        }
        return codigo.endsWith("no.encontrado") ? HttpStatus.NOT_FOUND : HttpStatus.CONFLICT;
    }
}
