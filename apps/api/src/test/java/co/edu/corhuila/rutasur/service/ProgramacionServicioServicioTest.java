package co.edu.corhuila.rutasur.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.dto.request.ProgramacionServicioRequest;
import co.edu.corhuila.rutasur.dto.response.ProgramacionServicioResponse;
import co.edu.corhuila.rutasur.entity.ProgramacionServicio;
import co.edu.corhuila.rutasur.entity.SalidaExperiencia;
import co.edu.corhuila.rutasur.entity.ServicioExperiencia;
import co.edu.corhuila.rutasur.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.repository.ProgramacionServicioRepositorio;
import co.edu.corhuila.rutasur.repository.SalidaExperienciaRepositorio;
import co.edu.corhuila.rutasur.repository.ServicioExperienciaRepositorio;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Prueba la programación comercial de servicios dentro de una salida. */
@ExtendWith(MockitoExtension.class)
class ProgramacionServicioServicioTest {

    @Mock private ProgramacionServicioRepositorio programacionRepositorio;
    @Mock private SalidaExperienciaRepositorio salidaRepositorio;
    @Mock private ServicioExperienciaRepositorio servicioRepositorio;
    @InjectMocks private ProgramacionServicioServicio programacionServicioServicio;

    /** Verifica que una programación copie sus cupos disponibles al crearla. */
    @Test
    void crear_cuandoDatosSonValidos_guardaPrecioYCuposIniciales() {
        SalidaExperiencia salida = new SalidaExperiencia();
        salida.setExperienciaId(4L);
        when(salidaRepositorio.findById(9L)).thenReturn(Optional.of(salida));
        when(servicioRepositorio.findByIdAndExperienciaIdAndActivoTrue(12L, 4L))
                .thenReturn(Optional.of(new ServicioExperiencia()));
        when(programacionRepositorio.existsBySalidaExperienciaIdAndServicioExperienciaId(9L, 12L)).thenReturn(false);
        when(programacionRepositorio.save(any(ProgramacionServicio.class))).thenAnswer(invocacion -> {
            ProgramacionServicio programacion = invocacion.getArgument(0);
            programacion.setId(19L);
            return programacion;
        });

        ProgramacionServicioResponse respuesta = programacionServicioServicio.crear(9L, solicitudValida());

        ArgumentCaptor<ProgramacionServicio> captor = ArgumentCaptor.forClass(ProgramacionServicio.class);
        verify(programacionRepositorio).save(captor.capture());
        assertEquals(15, captor.getValue().getCuposDisponibles());
        assertEquals(new BigDecimal("85000.00"), respuesta.precioUnitario());
    }

    /** Verifica que no se repita un servicio dentro de la misma salida. */
    @Test
    void crear_cuandoProgramacionYaExiste_lanzaReglaDeNegocio() {
        SalidaExperiencia salida = new SalidaExperiencia();
        salida.setExperienciaId(4L);
        when(salidaRepositorio.findById(9L)).thenReturn(Optional.of(salida));
        when(servicioRepositorio.findByIdAndExperienciaIdAndActivoTrue(12L, 4L))
                .thenReturn(Optional.of(new ServicioExperiencia()));
        when(programacionRepositorio.existsBySalidaExperienciaIdAndServicioExperienciaId(9L, 12L)).thenReturn(true);

        ExcepcionReglaNegocio excepcion = assertThrows(ExcepcionReglaNegocio.class,
                () -> programacionServicioServicio.crear(9L, solicitudValida()));

        assertEquals("programacion.duplicada", excepcion.getCodigo());
    }

    /** Construye una solicitud válida para reutilizar en los escenarios de prueba. */
    private ProgramacionServicioRequest solicitudValida() {
        return new ProgramacionServicioRequest(12L, new BigDecimal("85000.00"), 15);
    }
}
