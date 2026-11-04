package co.edu.corhuila.rutasur.contratos.evento;

import java.util.UUID;

/**
 * Datos mínimos del hecho ReservaConfirmada para Operación y proyecciones.
 */
public record ReservaConfirmadaDatos(UUID reservaId, String codigoReserva, UUID salidaId, UUID usuarioIdExterno) {
}
