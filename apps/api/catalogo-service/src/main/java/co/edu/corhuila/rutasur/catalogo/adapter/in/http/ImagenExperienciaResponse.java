package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Respuesta administrativa de una imagen de experiencia. */
public record ImagenExperienciaResponse(UUID id, UUID experienciaId, String ruta, boolean principal, int orden,
                                        boolean activa, OffsetDateTime creadoEn, OffsetDateTime actualizadoEn) {
}
