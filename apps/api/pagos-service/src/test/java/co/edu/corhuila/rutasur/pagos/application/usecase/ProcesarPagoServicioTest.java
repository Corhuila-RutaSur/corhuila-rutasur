package co.edu.corhuila.rutasur.pagos.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import co.edu.corhuila.rutasur.pagos.application.port.in.*;
import co.edu.corhuila.rutasur.pagos.application.port.out.*;
import co.edu.corhuila.rutasur.pagos.domain.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.*;
import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

/**
 * Verifica que un reintento con igual correlación no cree un segundo pago.
 */
@ExtendWith(MockitoExtension.class)
class ProcesarPagoServicioTest {
    @Mock
    PagoPuertoSalida pagos;
    @Mock
    EventoOutboxPuertoSalida outbox;
    @Mock
    ObjectMapper objectMapper;
    @InjectMocks
    ProcesarPagoServicio servicio;

    @Test
    void debeRetornarPagoExistenteAnteMismaCorrelacion() {
        UUID correlacion = UUID.randomUUID();
        Pago existente = new Pago(UUID.randomUUID(), UUID.randomUUID(), correlacion, new BigDecimal("100000"), "COP", "APROBADO", "SIM-1");
        when(pagos.buscarPorCorrelacion(correlacion)).thenReturn(Optional.of(existente));
        Pago resultado = servicio.procesar(new ProcesarPagoCommand(existente.reservaIdExterna(), existente.monto(), ResultadoPagoSimulado.APROBADO, correlacion));
        assertThat(resultado).isEqualTo(existente);
        verify(pagos, never()).guardar(any());
    }

    @Test
    void debePublicarPagoRechazadoCuandoElSimuladorLoIndica() throws Exception {
        UUID correlacion = UUID.randomUUID();
        Pago pago = new Pago(UUID.randomUUID(), UUID.randomUUID(), correlacion, new BigDecimal("100000"), "COP", "RECHAZADO", "SIM-1");
        when(pagos.buscarPorCorrelacion(correlacion)).thenReturn(Optional.empty());
        when(pagos.guardar(any())).thenReturn(pago);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");
        servicio.procesar(new ProcesarPagoCommand(pago.reservaIdExterna(), pago.monto(), ResultadoPagoSimulado.RECHAZADO, correlacion));
        verify(outbox).registrar(eq("PagoRechazado"), eq(pago.id()), eq(correlacion), eq("{}"));
    }
}
