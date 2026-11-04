package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import java.util.UUID;

/**
 * Vista HTTP de un servicio comercial de experiencia.
 */
public record ServicioExperienciaResponse(UUID id, UUID experienciaId, String tipo, String nombre, String descripcion,
                                          boolean activo) {
}
