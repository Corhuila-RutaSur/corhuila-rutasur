package co.edu.corhuila.rutasur.catalogo.application.port.in;

import java.util.UUID;

/**
 * Datos de creación o edición de un servicio de experiencia.
 */
public record ServicioExperienciaCommand(String tipo, String nombre, String descripcion, UUID correlacionId) {
}
