package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import java.util.UUID;

/**
 * Vista HTTP de una actividad turística.
 */
public record ActividadResponse(UUID id, UUID experienciaId, String nombre, String descripcion, int duracionHoras,
                                boolean activa) {
}
