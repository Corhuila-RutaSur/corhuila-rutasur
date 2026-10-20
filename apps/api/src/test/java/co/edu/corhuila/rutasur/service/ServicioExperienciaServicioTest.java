package co.edu.corhuila.rutasur.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.dto.request.ServicioExperienciaRequest;
import co.edu.corhuila.rutasur.dto.response.ServicioExperienciaResponse;
import co.edu.corhuila.rutasur.entity.Experiencia;
import co.edu.corhuila.rutasur.entity.ServicioExperiencia;
import co.edu.corhuila.rutasur.entity.TipoServicio;
import co.edu.corhuila.rutasur.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.repository.ExperienciaRepositorio;
import co.edu.corhuila.rutasur.repository.ServicioExperienciaRepositorio;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Prueba las reglas de negocio de los servicios configurables de una experiencia. */
@ExtendWith(MockitoExtension.class)
class ServicioExperienciaServicioTest {

    @Mock private ServicioExperienciaRepositorio servicioRepositorio;
    @Mock private ExperienciaRepositorio experienciaRepositorio;
    @InjectMocks private ServicioExperienciaServicio servicioExperienciaServicio;

    /** Verifica que un servicio único se persista asociado a la experiencia activa. */
    @Test
    void crear_cuandoServicioEsValido_persisteServicioNormalizado() {
        when(experienciaRepositorio.findByIdAndActivoTrue(7L)).thenReturn(Optional.of(new Experiencia()));
        when(servicioRepositorio.existsByExperienciaIdAndNombreIgnoreCase(7L, "Transporte ida y vuelta"))
                .thenReturn(false);
        when(servicioRepositorio.save(any(ServicioExperiencia.class))).thenAnswer(invocacion -> {
            ServicioExperiencia entidad = invocacion.getArgument(0);
            entidad.setId(11L);
            return entidad;
        });

        ServicioExperienciaResponse respuesta = servicioExperienciaServicio.crear(7L, solicitudValida());

        ArgumentCaptor<ServicioExperiencia> captor = ArgumentCaptor.forClass(ServicioExperiencia.class);
        verify(servicioRepositorio).save(captor.capture());
        assertEquals(7L, captor.getValue().getExperienciaId());
        assertEquals("Transporte ida y vuelta", captor.getValue().getNombre());
        assertEquals(11L, respuesta.id());
        assertEquals(TipoServicio.TRANSPORTE, respuesta.tipo());
    }

    /** Verifica que no se permita repetir el nombre de un servicio en una experiencia. */
    @Test
    void crear_cuandoNombreYaExiste_lanzaReglaDeNegocio() {
        when(experienciaRepositorio.findByIdAndActivoTrue(7L)).thenReturn(Optional.of(new Experiencia()));
        when(servicioRepositorio.existsByExperienciaIdAndNombreIgnoreCase(7L, "Transporte ida y vuelta"))
                .thenReturn(true);

        ExcepcionReglaNegocio excepcion = assertThrows(ExcepcionReglaNegocio.class,
                () -> servicioExperienciaServicio.crear(7L, solicitudValida()));

        assertEquals("servicio.experiencia.duplicado", excepcion.getCodigo());
    }

    /** Verifica que la eliminación del servicio sea lógica y no física. */
    @Test
    void inactivar_cuandoServicioExiste_marcaServicioComoInactivo() {
        ServicioExperiencia servicio = new ServicioExperiencia();
        servicio.setActivo(true);
        when(servicioRepositorio.findByIdAndExperienciaIdAndActivoTrue(3L, 7L)).thenReturn(Optional.of(servicio));

        servicioExperienciaServicio.inactivar(7L, 3L);

        assertFalse(servicio.isActivo());
    }

    /** Construye una solicitud válida y reutilizable para las pruebas. */
    private ServicioExperienciaRequest solicitudValida() {
        return new ServicioExperienciaRequest(TipoServicio.TRANSPORTE, "  Transporte ida y vuelta  ", "Desde Neiva");
    }
}
