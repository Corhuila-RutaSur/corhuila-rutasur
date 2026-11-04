package co.edu.corhuila.rutasur.contratos.evento;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Envelope inmutable y versionado para integrar microservicios sin compartir sus dominios.
 *
 * @param id            identificador único que permite idempotencia.
 * @param tipo          nombre del hecho de negocio publicado.
 * @param version       versión compatible del contrato.
 * @param ocurridoEn    fecha y hora de ocurrencia del hecho.
 * @param origen        nombre del servicio productor.
 * @param correlacionId identificador para seguir una operación distribuida.
 * @param datos         carga útil propia del evento.
 */
public record EventoIntegracion<T>(UUID id, String tipo, int version, OffsetDateTime ocurridoEn,
                                   String origen, UUID correlacionId, T datos) {
}
