package co.edu.corhuila.rutasur.catalogo.application.port.in;

import java.util.UUID;

/**
 * Datos modificables de una clasificación turística.
 */
public record ActualizarTipoExperienciaCommand(String nombre, String descripcion, UUID correlacionId) {
}
