package co.edu.corhuila.rutasur.reservas.application.port.in;

import co.edu.corhuila.rutasur.reservas.domain.model.Salida;

import java.util.List;

/**
 * Puerto de entrada para crear y consultar salidas programadas.
 */
public interface GestionarSalidaUseCase {
    Salida crear(CrearSalidaCommand comando);

    List<Salida> consultarDisponibles();
}
