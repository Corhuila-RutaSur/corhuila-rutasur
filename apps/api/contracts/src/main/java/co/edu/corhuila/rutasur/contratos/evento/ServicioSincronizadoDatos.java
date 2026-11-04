package co.edu.corhuila.rutasur.contratos.evento;

import java.util.UUID;

/**
 * Referencia comercial de un servicio ofrecido por una experiencia.
 */
public record ServicioSincronizadoDatos(UUID servicioId, UUID experienciaId, String tipo, String nombre,
                                        String descripcion, boolean activo) {
}
