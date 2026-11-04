package co.edu.corhuila.rutasur.catalogo.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Imagen administrada por el catálogo para presentar una experiencia.
 * El archivo se mantiene en almacenamiento externo al dominio; este modelo
 * conserva únicamente su ruta segura y los datos de ordenamiento.
 */
public record ImagenExperiencia(
        UUID id,
        UUID experienciaId,
        String ruta,
        boolean principal,
        int orden,
        boolean activa,
        OffsetDateTime creadoEn,
        OffsetDateTime actualizadoEn) {
}
