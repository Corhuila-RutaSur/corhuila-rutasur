package co.edu.corhuila.rutasur.reservas.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.reservas.application.port.in.CancelarReservaPorPagoCommand;
import co.edu.corhuila.rutasur.reservas.application.port.out.EventoInboxPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.EventoOutboxPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.ReservaPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.SalidaPuertoSalida;
import co.edu.corhuila.rutasur.reservas.domain.model.Reserva;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Verifica la compensación Saga cuando el pago no queda aprobado.
 */
@ExtendWith(MockitoExtension.class)
class CancelarReservaPorPagoServicioTest {
    @Mock
    private ReservaPuertoSalida reservas;
    @Mock
    private SalidaPuertoSalida salidas;
    @Mock
    private EventoInboxPuertoSalida inbox;
    @Mock
    private EventoOutboxPuertoSalida outbox;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private CancelarReservaPorPagoServicio servicio;

    @Test
    void debeCancelarReservaPendienteYLiberarSusCupos() throws Exception {
        UUID reservaId = UUID.randomUUID();
        UUID salidaId = UUID.randomUUID();
        UUID correlacionId = UUID.randomUUID();
        Reserva reserva = new Reserva(reservaId, "RS-123", UUID.randomUUID(), salidaId, 2,
                new BigDecimal("200000"), "PENDIENTE", correlacionId, OffsetDateTime.now());
        when(inbox.yaProcesado(any())).thenReturn(false);
        when(reservas.buscarPorId(reservaId)).thenReturn(Optional.of(reserva));
        when(salidas.liberarCupos(salidaId, 2)).thenReturn(true);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        servicio.cancelar(new CancelarReservaPorPagoCommand(UUID.randomUUID(), UUID.randomUUID(), reservaId,
                new BigDecimal("200000"), "COP", "SIM-1", "RECHAZADO", correlacionId));

        verify(salidas).liberarCupos(salidaId, 2);
        verify(reservas).guardar(org.mockito.ArgumentMatchers.argThat(resultado -> "CANCELADA".equals(resultado.estado())));
        verify(outbox).registrar(org.mockito.ArgumentMatchers.eq("ReservaCancelada"), org.mockito.ArgumentMatchers.eq(reservaId),
                org.mockito.ArgumentMatchers.eq(correlacionId), org.mockito.ArgumentMatchers.eq("{}"));
        verify(inbox).registrarProcesado(any(), org.mockito.ArgumentMatchers.eq("PagoNoAprobado"), org.mockito.ArgumentMatchers.eq("rutasur-pagos"));
    }
}
