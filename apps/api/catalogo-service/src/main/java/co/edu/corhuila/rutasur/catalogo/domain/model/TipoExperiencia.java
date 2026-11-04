package co.edu.corhuila.rutasur.catalogo.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Clasificación turística administrable para las experiencias del catálogo.
 */
public record TipoExperiencia(UUID id, String nombre, String descripcion, boolean activo,
                              OffsetDateTime creadoEn, OffsetDateTime actualizadoEn) {
}
