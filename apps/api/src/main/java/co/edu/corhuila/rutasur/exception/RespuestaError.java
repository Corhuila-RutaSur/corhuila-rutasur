package co.edu.corhuila.rutasur.exception;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Contrato uniforme de errores devuelto por la API.
 *
 * @param fechaHora instante en que se produjo el error.
 * @param estado    codigo HTTP.
 * @param codigo    codigo funcional estable.
 * @param mensaje   mensaje localizado.
 * @param ruta      ruta HTTP solicitada.
 * @param errores   campos invalidos, cuando aplica.
 */
public record RespuestaError(
        OffsetDateTime fechaHora,
        int estado,
        String codigo,
        String mensaje,
        String ruta,
        Map<String, String> errores) {
}
