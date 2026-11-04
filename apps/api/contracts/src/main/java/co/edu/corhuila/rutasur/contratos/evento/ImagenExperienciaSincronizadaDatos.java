package co.edu.corhuila.rutasur.contratos.evento;

import java.util.UUID;

/**
 * Contrato de sincronización de una imagen del catálogo hacia proyecciones de lectura.
 */
public record ImagenExperienciaSincronizadaDatos(UUID imagenId, UUID experienciaId, String ruta,
                                                  boolean principal, int orden, boolean activa) {
}
