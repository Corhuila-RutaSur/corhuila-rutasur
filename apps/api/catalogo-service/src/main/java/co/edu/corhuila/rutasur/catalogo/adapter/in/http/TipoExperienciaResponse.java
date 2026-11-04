package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import java.util.UUID;

/**
 * Representación pública de una clasificación turística.
 */
public record TipoExperienciaResponse(UUID id, String nombre, String descripcion, boolean activo) {
}
