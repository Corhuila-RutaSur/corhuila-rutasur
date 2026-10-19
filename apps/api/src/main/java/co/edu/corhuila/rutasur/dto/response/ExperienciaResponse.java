package co.edu.corhuila.rutasur.dto.response;

import java.time.OffsetDateTime;

/** Información visible de una experiencia de catálogo. */
public record ExperienciaResponse(Long id, Long tipoExperienciaId, Long creadoPorUsuarioId,
        String nombre, String descripcion, String destino, Integer duracionHoras,
        String recomendaciones, boolean activo, OffsetDateTime creadoEn, OffsetDateTime actualizadoEn) { }
