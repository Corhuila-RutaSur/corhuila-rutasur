package co.edu.corhuila.rutasur.reservas.application.usecase;

import co.edu.corhuila.rutasur.reservas.application.port.in.ConsultarReservasUsuarioUseCase;
import co.edu.corhuila.rutasur.reservas.application.port.out.ReservaPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.SalidaPuertoSalida;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Compone la vista de Mis reservas usando únicamente datos que pertenecen a Reservas. */
@Service
@RequiredArgsConstructor
public class ConsultarReservasUsuarioServicio implements ConsultarReservasUsuarioUseCase {
    private final ReservaPuertoSalida reservas;
    private final SalidaPuertoSalida salidas;
    @Override
    @Transactional(readOnly = true)
    public List<ReservaUsuarioDetalle> consultar(UUID usuarioIdExterno) {
        return reservas.consultarPorUsuario(usuarioIdExterno).stream().map(reserva -> {
            var salida = salidas.buscarPorId(reserva.salidaId()).orElse(null);
            return new ReservaUsuarioDetalle(reserva.id(), reserva.codigo(), reserva.salidaId(),
                    salida == null ? null : salida.experienciaIdExterna(), salida == null ? "Experiencia no disponible" : salida.experienciaNombre(),
                    salida == null ? null : salida.fechaHoraInicio(), reserva.cantidadParticipantes(), reserva.total(), reserva.estado(), reserva.creadaEn());
        }).toList();
    }
}
