package co.edu.corhuila.rutasur.reservas.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.reservas.application.port.in.CrearSalidaCommand;
import co.edu.corhuila.rutasur.reservas.application.port.out.SalidaPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.EventoOutboxPuertoSalida;
import co.edu.corhuila.rutasur.reservas.domain.model.Salida;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Prueba unitaria de disponibilidad inicial de una salida.
 */
@ExtendWith(MockitoExtension.class)
class GestionarSalidaServicioTest {
    @Mock
    private SalidaPuertoSalida salidas;
    @Mock
    private EventoOutboxPuertoSalida outbox;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private GestionarSalidaServicio servicio;

    @Test
    void debeCrearSalidaConTodosLosCuposDisponibles() throws Exception {
        UUID salidaId = UUID.randomUUID();
        when(salidas.guardar(any())).thenAnswer(i -> {
            Salida s = i.getArgument(0);
            return new Salida(salidaId, s.experienciaIdExterna(), s.experienciaNombre(), s.fechaHoraInicio(), s.cuposTotales(), s.cuposDisponibles(), s.estado(), s.creadoEn());
        });
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        Salida salida = servicio.crear(new CrearSalidaCommand(UUID.randomUUID(), "Tatacoa", OffsetDateTime.now().plusDays(2), 15, UUID.randomUUID()));
        assertThat(salida).extracting(Salida::cuposTotales, Salida::cuposDisponibles, Salida::estado).containsExactly(15, 15, "PROGRAMADA");
    }
}
