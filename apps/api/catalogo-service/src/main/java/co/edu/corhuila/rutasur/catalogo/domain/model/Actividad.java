package co.edu.corhuila.rutasur.catalogo.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Actividad turística perteneciente a una única experiencia.
 */
public record Actividad(UUID id, UUID experienciaId, String nombre, String descripcion, int duracionHoras,
                        boolean activa, OffsetDateTime creadoEn, OffsetDateTime actualizadoEn) {
}
