package co.edu.corhuila.rutasur.reservas.application.port.out;

import co.edu.corhuila.rutasur.reservas.domain.model.Salida;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia local de salidas y su disponibilidad.
 */
public interface SalidaPuertoSalida {
    Salida guardar(Salida salida);

    Optional<Salida> buscarPorId(UUID id);

    boolean descontarCuposSiDisponibles(UUID id, int cantidad);

    boolean liberarCupos(UUID id, int cantidad);

    List<Salida> consultarDisponibles();
}
