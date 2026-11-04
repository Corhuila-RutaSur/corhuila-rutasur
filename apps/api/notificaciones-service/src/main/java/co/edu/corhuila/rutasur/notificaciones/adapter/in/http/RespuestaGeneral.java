package co.edu.corhuila.rutasur.notificaciones.adapter.in.http;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Envelope uniforme de respuestas del microservicio.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RespuestaGeneral<T>(String codigoHttp, String codigo, String mensaje, T data) {
    public static <T> RespuestaGeneral<T> exitosa(String codigoHttp, String codigo, String mensaje, T data) {
        return new RespuestaGeneral<>(codigoHttp, codigo, mensaje, data);
    }
}
