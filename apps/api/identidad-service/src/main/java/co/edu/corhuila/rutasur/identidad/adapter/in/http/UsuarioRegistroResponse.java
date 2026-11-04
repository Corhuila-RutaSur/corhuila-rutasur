package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Datos públicos de la identidad creada durante un registro exitoso.
 */
public record UsuarioRegistroResponse(UUID id, String tipoDocumento, String numeroDocumento, String nombres,
                                      String apellidos, String correo, String telefono, String rol,
                                      OffsetDateTime creadoEn) {
}
