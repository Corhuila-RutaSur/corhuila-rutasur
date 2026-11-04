package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Estructura uniforme de todas las respuestas HTTP de Identidad.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RespuestaGeneral<T>(String codigoHttp, String codigo, String mensaje, T data) {

    /**
     * Construye una respuesta satisfactoria con contenido.
     */
    public static <T> RespuestaGeneral<T> exitosa(String codigoHttp, String codigo, String mensaje, T data) {
        return new RespuestaGeneral<>(codigoHttp, codigo, mensaje, data);
    }

    /**
     * Construye una respuesta de error sin exponer datos internos.
     */
    public static RespuestaGeneral<Void> error(String codigoHttp, String codigo, String mensaje) {
        return new RespuestaGeneral<>(codigoHttp, codigo, mensaje, null);
    }
}
