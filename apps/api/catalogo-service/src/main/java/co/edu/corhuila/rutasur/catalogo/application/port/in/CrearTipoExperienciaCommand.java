package co.edu.corhuila.rutasur.catalogo.application.port.in;

import java.util.UUID;

/**
 * Datos validados para registrar una clasificación turística.
 */
public record CrearTipoExperienciaCommand(String nombre, String descripcion, UUID correlacionId) {
}
