package co.edu.corhuila.rutasur.identidad.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Agregado de identidad independiente de HTTP y persistencia.
 */
public record Usuario(UUID id, String tipoDocumento, String numeroDocumento, String nombres, String apellidos,
                      String correo, String telefono, String passwordHash, boolean activo,
                      OffsetDateTime creadoEn, OffsetDateTime actualizadoEn) {
}
