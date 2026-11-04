package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Resultado seguro de creación administrativa con los roles realmente asignados.
 */
public record UsuarioAdministrativoResponse(UUID id, String tipoDocumento, String numeroDocumento, String nombres,
                                            String apellidos, String correo, String telefono, boolean activo,
                                            List<String> roles, OffsetDateTime creadoEn) {
}
