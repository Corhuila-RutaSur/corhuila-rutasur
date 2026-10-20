package co.edu.corhuila.rutasur.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Contrato unico para todas las respuestas HTTP expuestas por la API.
 *
 * @param codigoHttp estado HTTP expresado como texto.
 * @param codigo codigo funcional internacionalizable.
 * @param mensaje mensaje legible asociado al codigo.
 * @param data informacion propia de la operacion; se omite si no aplica.
 * @param <T> tipo de la informacion de negocio.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RespuestaGeneral<T>(String codigoHttp, String codigo, String mensaje, T data) {
}
