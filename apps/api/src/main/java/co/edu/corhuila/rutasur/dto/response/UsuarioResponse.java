package co.edu.corhuila.rutasur.dto.response;

import java.time.OffsetDateTime;

/**
 * Informacion segura de un usuario expuesta por la API.
 *
 * @param id              identificador tecnico.
 * @param tipoDocumento   tipo del documento.
 * @param numeroDocumento numero de documento.
 * @param nombres         nombres.
 * @param apellidos       apellidos.
 * @param correo          correo de contacto.
 * @param telefono        telefono de contacto.
 * @param activo          indica si la cuenta permanece habilitada.
 * @param creadoEn        fecha de creacion.
 * @param actualizadoEn   fecha de la ultima actualizacion.
 */
public record UsuarioResponse(
        Long id,
        String tipoDocumento,
        String numeroDocumento,
        String nombres,
        String apellidos,
        String correo,
        String telefono,
        boolean activo,
        OffsetDateTime creadoEn,
        OffsetDateTime actualizadoEn) {
}
