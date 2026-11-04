package co.edu.corhuila.rutasur.catalogo.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Servicio comercial que una experiencia puede ofrecer dentro de sus planes.
 */
public record ServicioExperiencia(UUID id, UUID experienciaId, String tipo, String nombre, String descripcion,
                                  boolean activo, OffsetDateTime creadoEn, OffsetDateTime actualizadoEn) {
}
