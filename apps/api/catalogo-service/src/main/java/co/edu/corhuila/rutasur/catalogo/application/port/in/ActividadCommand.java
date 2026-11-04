package co.edu.corhuila.rutasur.catalogo.application.port.in;

import java.util.UUID;

/**
 * Datos de creación o edición de una actividad turística.
 */
public record ActividadCommand(String nombre, String descripcion, int duracionHoras, UUID correlacionId) {
}
