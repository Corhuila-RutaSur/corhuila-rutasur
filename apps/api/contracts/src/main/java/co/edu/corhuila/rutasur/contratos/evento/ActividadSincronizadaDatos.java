package co.edu.corhuila.rutasur.contratos.evento;

import java.util.UUID;

/**
 * Referencia comercial de una actividad seleccionable en una experiencia.
 */
public record ActividadSincronizadaDatos(UUID actividadId, UUID experienciaId, String nombre, String descripcion,
                                         int duracionHoras, boolean activa) {
}
