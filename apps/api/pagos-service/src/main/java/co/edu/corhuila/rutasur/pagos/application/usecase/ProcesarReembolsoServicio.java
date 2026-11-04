package co.edu.corhuila.rutasur.pagos.application.usecase;

import co.edu.corhuila.rutasur.contratos.evento.ReembolsoConfirmadoDatos;
import co.edu.corhuila.rutasur.pagos.application.port.in.ProcesarReembolsoCommand;
import co.edu.corhuila.rutasur.pagos.application.port.in.ProcesarReembolsoUseCase;
import co.edu.corhuila.rutasur.pagos.application.port.out.EventoInboxPuertoSalida;
import co.edu.corhuila.rutasur.pagos.application.port.out.EventoOutboxPuertoSalida;
import co.edu.corhuila.rutasur.pagos.application.port.out.PagoPuertoSalida;
import co.edu.corhuila.rutasur.pagos.application.port.out.ReembolsoPuertoSalida;
import co.edu.corhuila.rutasur.pagos.domain.model.Reembolso;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Compensa un pago aprobado al recibir una cancelación definitiva de Reservas.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProcesarReembolsoServicio implements ProcesarReembolsoUseCase {
    private final PagoPuertoSalida pagos;
    private final ReembolsoPuertoSalida reembolsos;
    private final EventoInboxPuertoSalida inbox;
    private final EventoOutboxPuertoSalida outbox;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void procesar(ProcesarReembolsoCommand comando) {
        if (inbox.yaProcesado(comando.eventoId())) {
            log.info("correlationId={} servicio=pagos casoUso=reembolsar resultado=evento-ya-procesado", comando.correlacionId());
            return;
        }
        pagos.buscarAprobadoPorReserva(comando.reservaIdExterna()).ifPresent(pago -> {
            if (!reembolsos.existePorPagoId(pago.id())) {
                Reembolso reembolso = reembolsos.guardar(new Reembolso(null, pago.id(), pago.monto(), "CONFIRMADO", comando.motivo()));
                outbox.registrar("ReembolsoConfirmado", reembolso.id(), comando.correlacionId(), serializar(
                        new ReembolsoConfirmadoDatos(reembolso.id(), pago.id(), pago.reservaIdExterna(), reembolso.monto(), comando.motivo())));
                log.info("correlationId={} servicio=pagos casoUso=reembolsar pagoId={} reembolsoId={} resultado=confirmado", comando.correlacionId(), pago.id(), reembolso.id());
            }
        });
        inbox.registrarProcesado(comando.eventoId(), "ReservaCancelada", "rutasur-reservas");
    }

    private String serializar(Object datos) {
        try {
            return objectMapper.writeValueAsString(datos);
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("No fue posible serializar el evento de reembolso.", error);
        }
    }
}
