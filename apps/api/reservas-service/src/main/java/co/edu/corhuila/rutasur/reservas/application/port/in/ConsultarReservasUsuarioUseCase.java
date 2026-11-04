package co.edu.corhuila.rutasur.reservas.application.port.in;

import co.edu.corhuila.rutasur.reservas.application.usecase.ReservaUsuarioDetalle;
import java.util.List;
import java.util.UUID;

/** Consulta las reservas que pertenecen a un usuario externo. */
public interface ConsultarReservasUsuarioUseCase {
    List<ReservaUsuarioDetalle> consultar(UUID usuarioIdExterno);
}
