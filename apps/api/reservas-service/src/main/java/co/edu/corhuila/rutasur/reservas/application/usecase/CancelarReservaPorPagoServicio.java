package co.edu.corhuila.rutasur.reservas.application.usecase;

import co.edu.corhuila.rutasur.contratos.evento.ReservaCanceladaDatos;
import co.edu.corhuila.rutasur.contratos.evento.SalidaSincronizadaDatos;
import co.edu.corhuila.rutasur.reservas.application.port.in.CancelarReservaPorPagoCommand;
import co.edu.corhuila.rutasur.reservas.application.port.in.CancelarReservaPorPagoUseCase;
import co.edu.corhuila.rutasur.reservas.application.port.out.EventoInboxPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.EventoOutboxPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.ReservaPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.SalidaPuertoSalida;
import co.edu.corhuila.rutasur.reservas.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.reservas.domain.model.Reserva;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Compensa una reserva pendiente al rechazar o expirar el pago, incluida la devolución local de cupos.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CancelarReservaPorPagoServicio implements CancelarReservaPorPagoUseCase {
    private final ReservaPuertoSalida reservas;
    private final SalidaPuertoSalida salidas;
    private final EventoInboxPuertoSalida inbox;
    private final EventoOutboxPuertoSalida outbox;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void cancelar(CancelarReservaPorPagoCommand comando) {
        if (inbox.yaProcesado(comando.eventoId())) {
            log.info("correlationId={} servicio=reservas evento=pagoNoAprobado resultado=duplicado", comando.correlacionId());
            return;
        }
        Reserva reserva = reservas.buscarPorId(comando.reservaId())
                .orElseThrow(() -> new ExcepcionReglaNegocio("reserva.no.encontrada"));
        if (reserva.total().compareTo(comando.monto()) != 0) {
            throw new ExcepcionReglaNegocio("reserva.monto.pago.invalido");
        }
        if ("PENDIENTE".equals(reserva.estado())) {
            if (!salidas.liberarCupos(reserva.salidaId(), reserva.cantidadParticipantes())) {
                throw new ExcepcionReglaNegocio("reserva.cupos.no.liberables");
            }
            Reserva cancelada = new Reserva(reserva.id(), reserva.codigo(), reserva.usuarioIdExterno(), reserva.salidaId(),
                    reserva.cantidadParticipantes(), reserva.total(), "CANCELADA", reserva.correlacionId(), reserva.creadaEn());
            reservas.guardar(cancelada);
            outbox.registrar("ReservaCancelada", reserva.id(), comando.correlacionId(), serializar(
                    new ReservaCanceladaDatos(reserva.id(), reserva.codigo(), reserva.salidaId(), reserva.usuarioIdExterno(), comando.motivo())));
            salidas.buscarPorId(reserva.salidaId()).ifPresent(salida -> outbox.registrar("SalidaActualizada", salida.id(), comando.correlacionId(), serializar(
                    new SalidaSincronizadaDatos(salida.id(), salida.experienciaIdExterna(), salida.fechaHoraInicio(), salida.cuposDisponibles(), salida.estado()))));
            log.info("correlationId={} servicio=reservas evento=pagoNoAprobado reservaId={} resultado=cancelada-cupos-liberados",
                    comando.correlacionId(), reserva.id());
        } else {
            log.info("correlationId={} servicio=reservas evento=pagoNoAprobado reservaId={} resultado=sin-compensacion estado={}",
                    comando.correlacionId(), reserva.id(), reserva.estado());
        }
        inbox.registrarProcesado(comando.eventoId(), "PagoNoAprobado", "rutasur-pagos");
    }

    private String serializar(Object datos) {
        try {
            return objectMapper.writeValueAsString(datos);
        } catch (JsonProcessingException excepcion) {
            throw new IllegalStateException("No fue posible serializar la cancelación de reserva.", excepcion);
        }
    }
}
