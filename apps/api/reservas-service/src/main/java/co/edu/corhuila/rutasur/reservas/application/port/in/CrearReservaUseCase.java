package co.edu.corhuila.rutasur.reservas.application.port.in;

import co.edu.corhuila.rutasur.reservas.domain.model.Reserva;

/**
 * Puerto de entrada que crea una reserva y compromete cupos.
 */
public interface CrearReservaUseCase {
    Reserva crear(CrearReservaCommand comando);
}
