package co.edu.corhuila.rutasur.catalogo.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.catalogo.application.port.in.ActividadCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ActividadPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.catalogo.domain.model.Actividad;
import co.edu.corhuila.rutasur.catalogo.domain.model.Experiencia;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de creación y validación de actividades.
 */
@ExtendWith(MockitoExtension.class)
class GestionarActividadServicioTest {
    @Mock
    private ExperienciaPuertoSalida experiencias;
    @Mock
    private ActividadPuertoSalida actividades;
    @Mock
    private PublicadorCambiosCatalogo eventos;
    @InjectMocks
    private GestionarActividadServicio servicio;

    @Test
    void debeCrearActividadParaExperienciaActiva() {
        UUID experienciaId = UUID.randomUUID();
        UUID actividadId = UUID.randomUUID();
        when(experiencias.buscarPorId(experienciaId)).thenReturn(Optional.of(experiencia(experienciaId)));
        when(actividades.existeActivaPorExperienciaYNombre(experienciaId, "Caminata lunar")).thenReturn(false);
        when(actividades.guardar(any())).thenAnswer(invocacion -> {
            Actividad actividad = invocacion.getArgument(0);
            return new Actividad(actividadId, actividad.experienciaId(), actividad.nombre(), actividad.descripcion(), actividad.duracionHoras(), actividad.activa(), actividad.creadoEn(), actividad.actualizadoEn());
        });
        Actividad creada = servicio.crear(experienciaId, new ActividadCommand(" Caminata lunar ", " Recorrido ", 2, UUID.randomUUID()));
        assertThat(creada).extracting(Actividad::id, Actividad::nombre, Actividad::duracionHoras).containsExactly(actividadId, "Caminata lunar", 2);
    }

    @Test
    void debeRechazarDuracionNoPositiva() {
        assertThatThrownBy(() -> servicio.crear(UUID.randomUUID(), new ActividadCommand("Actividad", "Detalle", 0, UUID.randomUUID())))
                .isInstanceOf(ExcepcionReglaNegocio.class).hasMessage("actividad.duracion.invalida");
    }

    private Experiencia experiencia(UUID id) {
        return new Experiencia(id, UUID.randomUUID(), "NATURALEZA", "Sendero", "Huila", "Detalle", 3, null, true, OffsetDateTime.now(), null);
    }
}
