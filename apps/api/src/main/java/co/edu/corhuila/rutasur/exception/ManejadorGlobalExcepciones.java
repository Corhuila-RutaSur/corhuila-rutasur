package co.edu.corhuila.rutasur.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce las excepciones del backend al contrato de error internacionalizado de RutaSur.
 */
@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    private final ResolvedorMensajes resolvedorMensajes;

    /**
     * Construye el manejador global.
     *
     * @param resolvedorMensajes componente que obtiene los mensajes visibles.
     */
    public ManejadorGlobalExcepciones(ResolvedorMensajes resolvedorMensajes) {
        this.resolvedorMensajes = resolvedorMensajes;
    }

    /**
     * Expone los errores de validacion por campo.
     *
     * @param excepcion detalle de Bean Validation.
     * @param solicitud solicitud HTTP actual.
     * @return respuesta HTTP 400 con los campos invalidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> manejarValidacion(
            MethodArgumentNotValidException excepcion, HttpServletRequest solicitud) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError errorCampo : excepcion.getBindingResult().getFieldErrors()) {
            errores.put(errorCampo.getField(), errorCampo.getDefaultMessage());
        }
        return construirRespuesta(HttpStatus.BAD_REQUEST, "validacion.error", solicitud, errores);
    }

    /**
     * Expone reglas de negocio incumplidas.
     *
     * @param excepcion regla que no se pudo satisfacer.
     * @param solicitud solicitud HTTP actual.
     * @return respuesta HTTP 409 para duplicados o 404 para recursos ausentes.
     */
    @ExceptionHandler(ExcepcionReglaNegocio.class)
    public ResponseEntity<RespuestaError> manejarReglaNegocio(
            ExcepcionReglaNegocio excepcion, HttpServletRequest solicitud) {
        HttpStatus estado = excepcion.getCodigo().endsWith("no.encontrado")
                ? HttpStatus.NOT_FOUND : HttpStatus.CONFLICT;
        return construirRespuesta(estado, excepcion.getCodigo(), solicitud, Map.of());
    }

    /**
     * Evita exponer detalles de restricciones de base de datos.
     *
     * @param excepcion error de integridad detectado por persistencia.
     * @param solicitud solicitud HTTP actual.
     * @return respuesta HTTP 409 generica.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RespuestaError> manejarIntegridad(
            DataIntegrityViolationException excepcion, HttpServletRequest solicitud) {
        return construirRespuesta(HttpStatus.CONFLICT, "error.integridad", solicitud, Map.of());
    }

    /**
     * Impide filtrar detalles internos ante errores no controlados.
     *
     * @param excepcion error inesperado.
     * @param solicitud solicitud HTTP actual.
     * @return respuesta HTTP 500 generica.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> manejarErrorInesperado(Exception excepcion, HttpServletRequest solicitud) {
        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "error.interno", solicitud, Map.of());
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
    private ResponseEntity<RespuestaError> construirRespuesta(
            HttpStatus estado, String codigo, HttpServletRequest solicitud, Map<String, String> errores) {
        RespuestaError respuesta = new RespuestaError(
                OffsetDateTime.now(), estado.value(), codigo, resolvedorMensajes.resolver(codigo),
                solicitud.getRequestURI(), errores);
        return ResponseEntity.status(estado).body(respuesta);
    }
}
