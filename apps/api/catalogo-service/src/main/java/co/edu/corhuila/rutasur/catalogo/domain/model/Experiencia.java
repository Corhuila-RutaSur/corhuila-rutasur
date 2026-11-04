package co.edu.corhuila.rutasur.catalogo.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Agregado comercial raíz del catálogo turístico.
 */
public record Experiencia(UUID id, UUID tipoExperienciaId, String tipo, String nombre, String destino,
                          String descripcion,
                          int duracionHoras, String recomendaciones, boolean activa,
                          OffsetDateTime creadoEn, OffsetDateTime actualizadoEn) {
}
