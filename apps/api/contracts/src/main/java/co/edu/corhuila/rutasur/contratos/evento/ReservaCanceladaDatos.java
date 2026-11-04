package co.edu.corhuila.rutasur.contratos.evento;

import java.util.UUID;

/**
 * Datos mínimos de una reserva cancelada y compensada.
 */
public record ReservaCanceladaDatos(UUID reservaId, String codigoReserva, UUID salidaId, UUID usuarioIdExterno,
                                    String motivo) {
}
