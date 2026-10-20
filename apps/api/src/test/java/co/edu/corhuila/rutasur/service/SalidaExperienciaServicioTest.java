package co.edu.corhuila.rutasur.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.dto.request.SalidaExperienciaRequest;
import co.edu.corhuila.rutasur.dto.response.SalidaExperienciaResponse;
import co.edu.corhuila.rutasur.entity.EstadoSalida;
import co.edu.corhuila.rutasur.entity.Experiencia;
import co.edu.corhuila.rutasur.entity.SalidaExperiencia;
import co.edu.corhuila.rutasur.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.repository.ExperienciaRepositorio;
import co.edu.corhuila.rutasur.repository.SalidaExperienciaRepositorio;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Prueba las reglas de fechas y disponibilidad de las salidas. */
@ExtendWith(MockitoExtension.class)
class SalidaExperienciaServicioTest {

    @Mock private SalidaExperienciaRepositorio salidaRepositorio;
    @Mock private ExperienciaRepositorio experienciaRepositorio;
    @InjectMocks private SalidaExperienciaServicio salidaExperienciaServicio;

    /** Verifica que una salida nueva inicie con todos sus cupos disponibles. */
    @Test
    void crear_cuandoSalidaEsValida_inicializaCuposDisponibles() {
        when(experienciaRepositorio.findByIdAndActivoTrue(7L)).thenReturn(Optional.of(new Experiencia()));
        when(salidaRepositorio.save(any(SalidaExperiencia.class))).thenAnswer(invocacion -> {
            SalidaExperiencia salida = invocacion.getArgument(0);
            salida.setId(21L);
            return salida;
        });

        SalidaExperienciaResponse respuesta = salidaExperienciaServicio.crear(7L, solicitudValida());

        ArgumentCaptor<SalidaExperiencia> captor = ArgumentCaptor.forClass(SalidaExperiencia.class);
        verify(salidaRepositorio).save(captor.capture());
        assertEquals(12, captor.getValue().getCuposDisponibles());
        assertEquals(12, respuesta.cuposTotales());
        assertEquals(EstadoSalida.ABIERTA, respuesta.estado());
    }

    /** Verifica que la hora final sea posterior a la hora inicial. */
    @Test
    void crear_cuandoFechaFinalEsAnterior_lanzaReglaDeNegocio() {
        when(experienciaRepositorio.findByIdAndActivoTrue(7L)).thenReturn(Optional.of(new Experiencia()));
        OffsetDateTime inicio = OffsetDateTime.parse("2026-11-02T10:00:00-05:00");
        SalidaExperienciaRequest solicitud = new SalidaExperienciaRequest(inicio, inicio.minusHours(1), 12,
                EstadoSalida.ABIERTA, null);

        ExcepcionReglaNegocio excepcion = assertThrows(ExcepcionReglaNegocio.class,
                () -> salidaExperienciaServicio.crear(7L, solicitud));

        assertEquals("salida.fechas.invalidas", excepcion.getCodigo());
    }

    /** Verifica que cancelar una salida conserve la fila y cambie solo el estado. */
    @Test
    void inactivar_cuandoSalidaExiste_cambiaEstadoACancelada() {
        SalidaExperiencia salida = new SalidaExperiencia();
        salida.setEstado(EstadoSalida.ABIERTA);
        when(salidaRepositorio.findByIdAndExperienciaId(21L, 7L)).thenReturn(Optional.of(salida));

        salidaExperienciaServicio.inactivar(7L, 21L);

        assertEquals(EstadoSalida.CANCELADA, salida.getEstado());
    }

    /** Construye una salida abierta con doce cupos para las pruebas. */
    private SalidaExperienciaRequest solicitudValida() {
        OffsetDateTime inicio = OffsetDateTime.parse("2026-11-02T10:00:00-05:00");
        return new SalidaExperienciaRequest(inicio, inicio.plusHours(4), 12, EstadoSalida.ABIERTA, "Punto de salida: Neiva");
    }
}
