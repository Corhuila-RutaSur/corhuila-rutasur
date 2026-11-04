package co.edu.corhuila.rutasur.reservas.application.usecase;

import co.edu.corhuila.rutasur.contratos.evento.ReservaConfirmadaDatos;
import co.edu.corhuila.rutasur.reservas.application.port.in.ConfirmarReservaPorPagoCommand;
import co.edu.corhuila.rutasur.reservas.application.port.in.ConfirmarReservaPorPagoUseCase;
import co.edu.corhuila.rutasur.reservas.application.port.out.EventoInboxPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.EventoOutboxPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.ReservaPuertoSalida;
import co.edu.corhuila.rutasur.reservas.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.reservas.domain.model.Reserva;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ejecuta la transición PENDIENTE a CONFIRMADA al recibir un pago aprobado.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ConfirmarReservaPorPagoServicio implements ConfirmarReservaPorPagoUseCase {
    private final ReservaPuertoSalida reservas;
    private final EventoInboxPuertoSalida inbox;
    private final EventoOutboxPuertoSalida outbox;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void confirmar(ConfirmarReservaPorPagoCommand comando) {
        if (inbox.yaProcesado(comando.eventoId())) {
            log.info("correlationId={} servicio=reservas evento=PagoAprobado resultado=duplicado", comando.correlacionId());
            return;
        }
        Reserva reserva = reservas.buscarPorId(comando.reservaId())
                .orElseThrow(() -> new ExcepcionReglaNegocio("reserva.no.encontrada"));
        if (reserva.total().compareTo(comando.monto()) != 0) {
            throw new ExcepcionReglaNegocio("reserva.monto.pago.invalido");
        }
        if (!"PENDIENTE".equals(reserva.estado()) && !"CONFIRMADA".equals(reserva.estado())) {
            throw new ExcepcionReglaNegocio("reserva.estado.no.confirmable");
        }
        if ("PENDIENTE".equals(reserva.estado())) {
            Reserva confirmada = new Reserva(reserva.id(), reserva.codigo(), reserva.usuarioIdExterno(), reserva.salidaId(),
                    reserva.cantidadParticipantes(), reserva.total(), "CONFIRMADA", reserva.correlacionId(), reserva.creadaEn());
            reservas.guardar(confirmada);
            outbox.registrar("ReservaConfirmada", reserva.id(), comando.correlacionId(), serializar(
                    new ReservaConfirmadaDatos(reserva.id(), reserva.codigo(), reserva.salidaId(), reserva.usuarioIdExterno())));
        }
        inbox.registrarProcesado(comando.eventoId(), "PagoAprobado", "rutasur-pagos");
        log.info("correlationId={} servicio=reservas evento=PagoAprobado reservaId={} resultado=confirmada",
                comando.correlacionId(), reserva.id());
    }

    private String serializar(Object datos) {
        try {
            return objectMapper.writeValueAsString(datos);
        } catch (JsonProcessingException excepcion) {
            throw new IllegalStateException("No fue posible serializar el evento de reserva.", excepcion);
        }
    }
}
