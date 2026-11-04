package co.edu.corhuila.rutasur.reservas.application.port.in;

import co.edu.corhuila.rutasur.reservas.domain.model.Reserva;

/**
 * Cancela una reserva a solicitud de su dueño.
 */
public interface CancelarReservaUseCase {
    Reserva cancelar(CancelarReservaCommand comando);
}
