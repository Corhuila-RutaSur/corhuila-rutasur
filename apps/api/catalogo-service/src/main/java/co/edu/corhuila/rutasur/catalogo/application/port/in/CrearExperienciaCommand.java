package co.edu.corhuila.rutasur.catalogo.application.port.in;

import java.util.UUID;

/**
 * Datos validados para crear una experiencia administrable.
 */
public record CrearExperienciaCommand(UUID tipoExperienciaId, String nombre, String destino, String descripcion,
                                      int duracionHoras, String recomendaciones, UUID correlacionId) {
}
