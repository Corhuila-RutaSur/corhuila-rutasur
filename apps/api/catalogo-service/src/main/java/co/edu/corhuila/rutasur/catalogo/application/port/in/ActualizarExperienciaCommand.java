package co.edu.corhuila.rutasur.catalogo.application.port.in;

import java.util.UUID;

/**
 * Datos modificables de una experiencia administrada.
 */
public record ActualizarExperienciaCommand(UUID tipoExperienciaId, String nombre, String destino, String descripcion,
                                           int duracionHoras, String recomendaciones, UUID correlacionId) {
}
