package co.edu.corhuila.rutasur.reservas.application.port.out;

import co.edu.corhuila.rutasur.reservas.domain.model.ParticipanteReserva;

import java.util.*;

/**
 * Puerto para registrar los viajeros de una reserva.
 */
public interface ParticipanteReservaPuertoSalida {
    void guardarTodos(List<ParticipanteReserva> participantes);
}
