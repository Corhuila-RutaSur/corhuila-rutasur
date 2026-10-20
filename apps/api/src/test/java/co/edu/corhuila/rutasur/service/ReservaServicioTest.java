package co.edu.corhuila.rutasur.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.dto.request.ParticipanteReservaRequest;
import co.edu.corhuila.rutasur.dto.request.ReservaCreacionRequest;
import co.edu.corhuila.rutasur.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.mapper.ReservaMapper;
import co.edu.corhuila.rutasur.repository.DetalleParticipanteRepositorio;
import co.edu.corhuila.rutasur.repository.DetalleReservaRepositorio;
import co.edu.corhuila.rutasur.repository.ParticipanteReservaRepositorio;
import co.edu.corhuila.rutasur.repository.ReservaRepositorio;
import co.edu.corhuila.rutasur.repository.UsuarioRepositorio;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Prueba las reglas tempranas de creación de una reserva. */
@ExtendWith(MockitoExtension.class)
class ReservaServicioTest {

    @Mock private ReservaRepositorio reservaRepositorio;
    @Mock private UsuarioRepositorio usuarioRepositorio;
    @Mock private ParticipanteReservaRepositorio participanteRepositorio;
    @Mock private DetalleReservaRepositorio detalleReservaRepositorio;
    @Mock private DetalleParticipanteRepositorio detalleParticipanteRepositorio;
    @Mock private ReservaMapper reservaMapper;
    @InjectMocks private ReservaServicio reservaServicio;

    /** Verifica que una reserva exija un único responsable entre los participantes. */
    @Test
    void crear_cuandoNoHayResponsable_rechazaAntesDeBloquearCupos() {
        when(usuarioRepositorio.findByIdAndActivoTrue(4L)).thenReturn(Optional.of(new co.edu.corhuila.rutasur.entity.Usuario()));
        ReservaCreacionRequest solicitud = new ReservaCreacionRequest(4L, 9L, List.of(20L),
                List.of(participante(false), participante(false)), null);

        ExcepcionReglaNegocio excepcion = assertThrows(ExcepcionReglaNegocio.class,
                () -> reservaServicio.crear(solicitud));

        assertEquals("reserva.responsable.invalido", excepcion.getCodigo());
        verify(reservaRepositorio, never()).bloquearSalida(9L);
    }

    /** Verifica que un paquete no acepte el mismo servicio dos veces. */
    @Test
    void crear_cuandoServicioEstaRepetido_rechazaReserva() {
        when(usuarioRepositorio.findByIdAndActivoTrue(4L)).thenReturn(Optional.of(new co.edu.corhuila.rutasur.entity.Usuario()));
        ReservaCreacionRequest solicitud = new ReservaCreacionRequest(4L, 9L, List.of(20L, 20L),
                List.of(participante(true)), null);

        ExcepcionReglaNegocio excepcion = assertThrows(ExcepcionReglaNegocio.class,
                () -> reservaServicio.crear(solicitud));

        assertEquals("reserva.servicio.duplicado", excepcion.getCodigo());
        verify(reservaRepositorio, never()).bloquearSalida(9L);
    }

    /** Construye un participante coherente para las pruebas de reserva. */
    private ParticipanteReservaRequest participante(boolean responsable) {
        return new ParticipanteReservaRequest("CC", "123456789", "Ana", "Perez", "ana@rutasur.co", "3001234567", responsable);
    }
}
