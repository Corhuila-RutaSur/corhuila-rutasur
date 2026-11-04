package co.edu.corhuila.rutasur.reservas.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.reservas.application.port.out.ReservaPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.SalidaPuertoSalida;
import co.edu.corhuila.rutasur.reservas.domain.model.Reserva;
import co.edu.corhuila.rutasur.reservas.domain.model.Salida;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Verifica el contrato de lectura de Mis reservas. */
@ExtendWith(MockitoExtension.class)
class ConsultarReservasUsuarioServicioTest {
    @Mock private ReservaPuertoSalida reservas;
    @Mock private SalidaPuertoSalida salidas;
    @InjectMocks private ConsultarReservasUsuarioServicio servicio;

    @Test
    void debeDevolverReservaConElResumenDeSuSalida() {
        UUID usuarioId = UUID.randomUUID(); UUID salidaId = UUID.randomUUID(); UUID experienciaId = UUID.randomUUID();
        Reserva reserva = new Reserva(UUID.randomUUID(), "RS-123", usuarioId, salidaId, 2, new BigDecimal("120000"), "PENDIENTE", UUID.randomUUID(), OffsetDateTime.now());
        Salida salida = new Salida(salidaId, experienciaId, "Observación astronómica", OffsetDateTime.now().plusDays(3), 10, 8, "PROGRAMADA", OffsetDateTime.now());
        when(reservas.consultarPorUsuario(usuarioId)).thenReturn(List.of(reserva));
        when(salidas.buscarPorId(salidaId)).thenReturn(Optional.of(salida));

        List<ReservaUsuarioDetalle> resultado = servicio.consultar(usuarioId);

        assertThat(resultado).singleElement().extracting(ReservaUsuarioDetalle::experienciaNombre, ReservaUsuarioDetalle::cantidadParticipantes).containsExactly("Observación astronómica", 2);
    }
}
