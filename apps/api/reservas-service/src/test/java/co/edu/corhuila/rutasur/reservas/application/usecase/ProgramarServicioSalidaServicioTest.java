package co.edu.corhuila.rutasur.reservas.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.reservas.application.port.in.ProgramarServicioCommand;
import co.edu.corhuila.rutasur.reservas.application.port.out.EventoOutboxPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.ProgramacionServicioPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.SalidaPuertoSalida;
import co.edu.corhuila.rutasur.reservas.domain.model.ProgramacionServicio;
import co.edu.corhuila.rutasur.reservas.domain.model.Salida;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Verifica que programar un servicio conserva una correlación válida para el Outbox. */
@ExtendWith(MockitoExtension.class)
class ProgramarServicioSalidaServicioTest {
    @Mock private SalidaPuertoSalida salidas;
    @Mock private ProgramacionServicioPuertoSalida programaciones;
    @Mock private EventoOutboxPuertoSalida outbox;
    @Mock private ObjectMapper objectMapper;
    @InjectMocks private ProgramarServicioSalidaServicio servicio;

    @Test
    void debeRegistrarEventoOutboxConLaCorrelacionDelComando() throws Exception {
        UUID salidaId = UUID.randomUUID();
        UUID servicioId = UUID.randomUUID();
        UUID correlacionId = UUID.randomUUID();
        when(salidas.buscarPorId(salidaId)).thenReturn(Optional.of(new Salida(salidaId, UUID.randomUUID(), "Tatacoa", OffsetDateTime.now().plusDays(2), 10, 10, "PROGRAMADA", OffsetDateTime.now())));
        when(programaciones.guardar(any())).thenAnswer(invocacion -> {
            ProgramacionServicio valor = invocacion.getArgument(0);
            return new ProgramacionServicio(UUID.randomUUID(), valor.salidaId(), valor.servicioIdExterno(), valor.nombre(), valor.precio(), valor.cuposTotales(), valor.cuposDisponibles(), valor.activo());
        });
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        servicio.ejecutar(salidaId, new ProgramarServicioCommand(servicioId, "Transporte", BigDecimal.valueOf(25000), 10, correlacionId));

        ArgumentCaptor<UUID> correlacionCapturada = ArgumentCaptor.forClass(UUID.class);
        verify(outbox).registrar(any(), any(), correlacionCapturada.capture(), any());
        assertThat(correlacionCapturada.getValue()).isEqualTo(correlacionId);
    }
}
