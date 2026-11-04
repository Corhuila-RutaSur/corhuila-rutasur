package co.edu.corhuila.rutasur.contratos.evento;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Datos comerciales mínimos de una reserva creada en estado pendiente.
 */
public record ReservaCreadaDatos(UUID reservaId, String codigoReserva, UUID salidaId, UUID usuarioIdExterno,
                                 int cantidadParticipantes, BigDecimal total) {
}
