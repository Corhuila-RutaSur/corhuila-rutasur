package co.edu.corhuila.rutasur.config.security;

import co.edu.corhuila.rutasur.service.ReservaServicio;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * Evalua si un usuario autenticado es titular de una reserva antes de autorizar una operacion.
 */
@Component("autorizacionReserva")
@RequiredArgsConstructor
public class AutorizacionReserva {

    private final ReservaServicio reservaServicio;


    /**
     * Determina si la reserva indicada pertenece al usuario autenticado.
     */
    public boolean esPropietario(Long reservaId, Long usuarioId) {
        return reservaServicio.consultarPorId(reservaId).usuarioId().equals(usuarioId);
    }
}
