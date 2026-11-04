package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Vista HTTP segura de una experiencia.
 */
public record ExperienciaResponse(UUID id, UUID tipoExperienciaId, String tipo, String nombre, String destino,
                                  String descripcion,
                                  int duracionHoras, String recomendaciones, boolean activa, OffsetDateTime creadoEn) {
}
