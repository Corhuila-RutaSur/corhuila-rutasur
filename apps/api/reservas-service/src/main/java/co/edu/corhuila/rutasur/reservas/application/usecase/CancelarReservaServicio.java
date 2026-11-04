package co.edu.corhuila.rutasur.reservas.application.usecase;

import co.edu.corhuila.rutasur.contratos.evento.ReservaCanceladaDatos;
import co.edu.corhuila.rutasur.contratos.evento.SalidaSincronizadaDatos;
import co.edu.corhuila.rutasur.reservas.application.port.in.*;
import co.edu.corhuila.rutasur.reservas.application.port.out.*;
import co.edu.corhuila.rutasur.reservas.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.reservas.domain.model.Reserva;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cancela una reserva del usuario y publica el hecho definitivo para Pagos y Notificaciones.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CancelarReservaServicio implements CancelarReservaUseCase {
    private final ReservaPuertoSalida reservas;
    private final SalidaPuertoSalida salidas;
    private final EventoOutboxPuertoSalida outbox;
    private final ObjectMapper mapper;

    @Override
    @Transactional
    public Reserva cancelar(CancelarReservaCommand c) {
        Reserva r = reservas.buscarPorId(c.reservaId()).orElseThrow(() -> new ExcepcionReglaNegocio("reserva.no.encontrada"));
        if (!r.usuarioIdExterno().equals(c.usuarioIdExterno()))
            throw new ExcepcionReglaNegocio("reserva.cancelacion.no.autorizada");
        if (!"PENDIENTE".equals(r.estado()) && !"CONFIRMADA".equals(r.estado()))
            throw new ExcepcionReglaNegocio("reserva.estado.no.cancelable");
        if (!salidas.liberarCupos(r.salidaId(), r.cantidadParticipantes()))
            throw new ExcepcionReglaNegocio("reserva.cupos.no.liberables");
        Reserva x = reservas.guardar(new Reserva(r.id(), r.codigo(), r.usuarioIdExterno(), r.salidaId(), r.cantidadParticipantes(), r.total(), "CANCELADA", r.correlacionId(), r.creadaEn()));
        try {
            outbox.registrar("ReservaCancelada", r.id(), c.correlacionId(), mapper.writeValueAsString(new ReservaCanceladaDatos(r.id(), r.codigo(), r.salidaId(), r.usuarioIdExterno(), c.motivo())));
            var salida = salidas.buscarPorId(r.salidaId());
            if (salida.isPresent()) {
                var valor = salida.get();
                outbox.registrar("SalidaActualizada", valor.id(), c.correlacionId(), mapper.writeValueAsString(
                        new SalidaSincronizadaDatos(valor.id(), valor.experienciaIdExterna(), valor.fechaHoraInicio(), valor.cuposDisponibles(), valor.estado())));
            }
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible serializar la cancelación.", e);
        }
        log.info("correlationId={} servicio=reservas casoUso=cancelarReserva reservaId={} resultado=cancelada", c.correlacionId(), r.id());
        return x;
    }
}
