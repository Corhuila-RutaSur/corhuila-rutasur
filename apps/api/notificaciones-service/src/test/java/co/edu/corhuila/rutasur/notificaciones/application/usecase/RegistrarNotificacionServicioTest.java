package co.edu.corhuila.rutasur.notificaciones.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.notificaciones.application.port.in.RegistrarNotificacionCommand;
import co.edu.corhuila.rutasur.notificaciones.application.port.out.EventoInboxPuertoSalida;
import co.edu.corhuila.rutasur.notificaciones.application.port.out.NotificacionPuertoSalida;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Verifica creación e idempotencia de notificaciones internas.
 */
@ExtendWith(MockitoExtension.class)
class RegistrarNotificacionServicioTest {
    @Mock
    private NotificacionPuertoSalida notificaciones;
    @Mock
    private EventoInboxPuertoSalida inbox;
    @InjectMocks
    private RegistrarNotificacionServicio servicio;

    @Test
    void debeCrearNotificacionYRegistrarInbox() {
        UUID eventoId = UUID.randomUUID();
        when(inbox.yaProcesado(eventoId)).thenReturn(false);
        servicio.registrar(new RegistrarNotificacionCommand(eventoId, "ReservaConfirmada", "rutasur-reservas", UUID.randomUUID(), "Reserva confirmada", "Tu reserva fue confirmada."));
        verify(notificaciones).guardar(any());
        verify(inbox).registrarProcesado(eventoId, "ReservaConfirmada", "rutasur-reservas");
    }

    @Test
    void noDebeCrearDosAvisosAnteReintento() {
        UUID eventoId = UUID.randomUUID();
        when(inbox.yaProcesado(eventoId)).thenReturn(true);
        servicio.registrar(new RegistrarNotificacionCommand(eventoId, "ReservaConfirmada", "rutasur-reservas", UUID.randomUUID(), "Reserva confirmada", "Tu reserva fue confirmada."));
        verify(notificaciones, never()).guardar(any());
    }
}
