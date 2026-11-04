package co.edu.corhuila.rutasur.contratos.evento;

import java.util.UUID;

/**
 * Referencia comercial de una experiencia distribuida por Catálogo.
 */
public record ExperienciaSincronizadaDatos(UUID experienciaId, String nombre, String destino, String descripcion,
                                           int duracionHoras, boolean activa) {
}
