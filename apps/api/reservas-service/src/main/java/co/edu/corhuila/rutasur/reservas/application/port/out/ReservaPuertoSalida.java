package co.edu.corhuila.rutasur.reservas.application.port.out;

import co.edu.corhuila.rutasur.reservas.domain.model.Reserva;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia de reservas confirmadas localmente.
 */
public interface ReservaPuertoSalida {
    Reserva guardar(Reserva reserva);

    Optional<Reserva> buscarPorId(UUID reservaId);

    /** Consulta las reservas de un usuario sin cruzar límites de bases de datos. */
    List<Reserva> consultarPorUsuario(UUID usuarioIdExterno);
}
