package co.edu.corhuila.rutasur.pagos.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.pagos.application.port.in.ProcesarReembolsoCommand;
import co.edu.corhuila.rutasur.pagos.application.port.out.EventoInboxPuertoSalida;
import co.edu.corhuila.rutasur.pagos.application.port.out.EventoOutboxPuertoSalida;
import co.edu.corhuila.rutasur.pagos.application.port.out.PagoPuertoSalida;
import co.edu.corhuila.rutasur.pagos.application.port.out.ReembolsoPuertoSalida;
import co.edu.corhuila.rutasur.pagos.domain.model.Pago;
import co.edu.corhuila.rutasur.pagos.domain.model.Reembolso;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Verifica la compensación financiera de una cancelación de reserva.
 */
@ExtendWith(MockitoExtension.class)
class ProcesarReembolsoServicioTest {
    @Mock
    private PagoPuertoSalida pagos;
    @Mock
    private ReembolsoPuertoSalida reembolsos;
    @Mock
    private EventoInboxPuertoSalida inbox;
    @Mock
    private EventoOutboxPuertoSalida outbox;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private ProcesarReembolsoServicio servicio;

    @Test
    void debeReembolsarPagoAprobadoYPublicarResultado() throws Exception {
        UUID eventoId = UUID.randomUUID();
        UUID pagoId = UUID.randomUUID();
        UUID reservaId = UUID.randomUUID();
        UUID correlacionId = UUID.randomUUID();
        Pago pago = new Pago(pagoId, reservaId, UUID.randomUUID(), new BigDecimal("125000"), "COP", "APROBADO", "SIM-1");
        Reembolso reembolso = new Reembolso(UUID.randomUUID(), pagoId, pago.monto(), "CONFIRMADO", "cancelacion-cliente");
        when(inbox.yaProcesado(eventoId)).thenReturn(false);
        when(pagos.buscarAprobadoPorReserva(reservaId)).thenReturn(Optional.of(pago));
        when(reembolsos.existePorPagoId(pagoId)).thenReturn(false);
        when(reembolsos.guardar(any())).thenReturn(reembolso);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        servicio.procesar(new ProcesarReembolsoCommand(eventoId, reservaId, "cancelacion-cliente", correlacionId));

        verify(outbox).registrar(eq("ReembolsoConfirmado"), eq(reembolso.id()), eq(correlacionId), eq("{}"));
        verify(inbox).registrarProcesado(eventoId, "ReservaCancelada", "rutasur-reservas");
    }

    @Test
    void noDebeCrearReembolsoCuandoNoExistePagoAprobado() {
        UUID eventoId = UUID.randomUUID();
        UUID reservaId = UUID.randomUUID();
        when(inbox.yaProcesado(eventoId)).thenReturn(false);
        when(pagos.buscarAprobadoPorReserva(reservaId)).thenReturn(Optional.empty());

        servicio.procesar(new ProcesarReembolsoCommand(eventoId, reservaId, "cancelacion-cliente", UUID.randomUUID()));

        verify(reembolsos, never()).guardar(any());
        verify(outbox, never()).registrar(any(), any(), any(), any());
        verify(inbox).registrarProcesado(eventoId, "ReservaCancelada", "rutasur-reservas");
    }
}
