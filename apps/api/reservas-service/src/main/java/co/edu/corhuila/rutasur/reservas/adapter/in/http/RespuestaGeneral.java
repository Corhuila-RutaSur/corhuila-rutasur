package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Estructura uniforme de respuesta del microservicio Reservas.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RespuestaGeneral<T>(String codigoHttp, String codigo, String mensaje, T data) {
    public static <T> RespuestaGeneral<T> exitosa(String http, String codigo, String mensaje, T data) {
        return new RespuestaGeneral<>(http, codigo, mensaje, data);
    }

    public static RespuestaGeneral<Void> error(String http, String codigo, String mensaje) {
        return new RespuestaGeneral<>(http, codigo, mensaje, null);
    }
}
