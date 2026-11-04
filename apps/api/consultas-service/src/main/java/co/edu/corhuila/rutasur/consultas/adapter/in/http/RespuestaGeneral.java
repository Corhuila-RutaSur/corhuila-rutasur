package co.edu.corhuila.rutasur.consultas.adapter.in.http;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Envelope uniforme para consultas públicas V2.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RespuestaGeneral<T>(String codigoHttp, String codigo, String mensaje, T data) {
    public static <T> RespuestaGeneral<T> exitosa(String h, String c, String m, T d) {
        return new RespuestaGeneral<>(h, c, m, d);
    }
}
