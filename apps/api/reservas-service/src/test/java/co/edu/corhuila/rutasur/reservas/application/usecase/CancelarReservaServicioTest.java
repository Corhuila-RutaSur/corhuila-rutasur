package co.edu.corhuila.rutasur.reservas.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.reservas.application.port.in.CancelarReservaCommand;
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
 * Comprueba que la cancelación del dueño libera cupos y publica el hecho.
 */
@ExtendWith(MockitoExtension.class)
class CancelarReservaServicioTest {
    @Mock
    private ReservaPuertoSalida reservas;
    @Mock
    private SalidaPuertoSalida salidas;
    @Mock
    private EventoOutboxPuertoSalida outbox;
    @Mock
    private ObjectMapper mapper;
    @InjectMocks
    private CancelarReservaServicio servicio;

    @Test
    void debeCancelarReservaDelPropietarioYLiberarCupos() throws Exception {
        UUID reservaId = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        UUID correlacionId = UUID.randomUUID();
        Reserva pendiente = new Reserva(reservaId, "RS-001", usuarioId, UUID.randomUUID(), 2, new BigDecimal("180000"), "PENDIENTE", correlacionId, OffsetDateTime.now());
        when(reservas.buscarPorId(reservaId)).thenReturn(Optional.of(pendiente));
        when(salidas.liberarCupos(pendiente.salidaId(), 2)).thenReturn(true);
        when(reservas.guardar(any())).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(mapper.writeValueAsString(any())).thenReturn("{}");

        Reserva resultado = servicio.cancelar(new CancelarReservaCommand(reservaId, usuarioId, "cambio de planes", correlacionId));

        assertThat(resultado.estado()).isEqualTo("CANCELADA");
        verify(salidas).liberarCupos(pendiente.salidaId(), 2);
        verify(outbox).registrar(eq("ReservaCancelada"), eq(reservaId), eq(correlacionId), eq("{}"));
    }
}
