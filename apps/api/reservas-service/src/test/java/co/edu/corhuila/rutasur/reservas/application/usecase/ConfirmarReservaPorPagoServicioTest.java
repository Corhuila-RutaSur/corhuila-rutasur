package co.edu.corhuila.rutasur.reservas.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.reservas.application.port.in.ConfirmarReservaPorPagoCommand;
import co.edu.corhuila.rutasur.reservas.application.port.out.EventoInboxPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.EventoOutboxPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.ReservaPuertoSalida;
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
 * Verifica la confirmación idempotente que une Pagos con Reservas.
 */
@ExtendWith(MockitoExtension.class)
class ConfirmarReservaPorPagoServicioTest {
    @Mock
    private ReservaPuertoSalida reservas;
    @Mock
    private EventoInboxPuertoSalida inbox;
    @Mock
    private EventoOutboxPuertoSalida outbox;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private ConfirmarReservaPorPagoServicio servicio;

    @Test
    void debeConfirmarReservaPendienteYRegistrarEventos() throws Exception {
        UUID reservaId = UUID.randomUUID();
        UUID correlacionId = UUID.randomUUID();
        Reserva reserva = new Reserva(reservaId, "RS-123", UUID.randomUUID(), UUID.randomUUID(), 1,
                new BigDecimal("100000"), "PENDIENTE", correlacionId, OffsetDateTime.now());
        when(inbox.yaProcesado(any())).thenReturn(false);
        when(reservas.buscarPorId(reservaId)).thenReturn(Optional.of(reserva));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        servicio.confirmar(new ConfirmarReservaPorPagoCommand(UUID.randomUUID(), UUID.randomUUID(), reservaId,
                new BigDecimal("100000"), "COP", "SIM-1", correlacionId));

        verify(reservas).guardar(org.mockito.ArgumentMatchers.argThat(resultado -> "CONFIRMADA".equals(resultado.estado())));
        verify(outbox).registrar(org.mockito.ArgumentMatchers.eq("ReservaConfirmada"), org.mockito.ArgumentMatchers.eq(reservaId),
                org.mockito.ArgumentMatchers.eq(correlacionId), org.mockito.ArgumentMatchers.eq("{}"));
        verify(inbox).registrarProcesado(any(), org.mockito.ArgumentMatchers.eq("PagoAprobado"), org.mockito.ArgumentMatchers.eq("rutasur-pagos"));
    }

    @Test
    void noDebeConfirmarDosVecesElMismoEvento() {
        when(inbox.yaProcesado(any())).thenReturn(true);
        servicio.confirmar(new ConfirmarReservaPorPagoCommand(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("1"), "COP", "SIM-1", UUID.randomUUID()));
        verify(reservas, never()).guardar(any());
        verify(outbox, never()).registrar(any(), any(), any(), any());
    }
}
