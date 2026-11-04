package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import java.util.List;
import java.util.UUID;

/**
 * Contrato público seguro del perfil de la sesión autenticada.
 */
public record PerfilUsuarioResponse(UUID id, String tipoDocumento, String numeroDocumento, String nombres,
                                    String apellidos, String correo, String telefono, boolean activo,
                                    List<String> roles) {
}
