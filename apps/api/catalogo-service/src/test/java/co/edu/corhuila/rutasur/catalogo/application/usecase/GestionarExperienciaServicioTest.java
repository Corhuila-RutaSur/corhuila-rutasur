package co.edu.corhuila.rutasur.catalogo.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.catalogo.application.port.in.ActualizarExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.application.port.out.TipoExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.model.Experiencia;
import co.edu.corhuila.rutasur.catalogo.domain.model.TipoExperiencia;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para edición e inactivación de experiencias.
 */
@ExtendWith(MockitoExtension.class)
class GestionarExperienciaServicioTest {
    @Mock
    private PublicadorCambiosCatalogo eventos;
    @Mock
    private ExperienciaPuertoSalida experiencias;
    @Mock
    private TipoExperienciaPuertoSalida tipos;
    @InjectMocks
    private GestionarExperienciaServicio servicio;

    /**
     * Debe actualizar una experiencia con un tipo activo.
     */
    @Test
    void debeActualizarExperienciaConTipoActivo() {
        UUID experienciaId = UUID.randomUUID();
        UUID tipoId = UUID.randomUUID();
        Experiencia actual = new Experiencia(experienciaId, tipoId, "NATURALEZA", "Anterior", "Huila", "Detalle", 2, null, true, OffsetDateTime.now(), null);
        when(experiencias.buscarPorId(experienciaId)).thenReturn(Optional.of(actual));
        when(tipos.buscarPorId(tipoId)).thenReturn(Optional.of(new TipoExperiencia(tipoId, "AVENTURA", null, true, OffsetDateTime.now(), null)));
        when(experiencias.guardar(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

        Experiencia resultado = servicio.actualizar(experienciaId, new ActualizarExperienciaCommand(tipoId, "Nueva", "Tatacoa", "Nueva descripcion", 4, null, UUID.randomUUID()));

        assertThat(resultado).extracting(Experiencia::tipo, Experiencia::nombre, Experiencia::destino, Experiencia::duracionHoras)
                .containsExactly("AVENTURA", "Nueva", "Tatacoa", 4);
    }

    /**
     * Debe conservar el registro y desactivarlo en lugar de eliminarlo.
     */
    @Test
    void debeInactivarExperienciaLogicamente() {
        UUID experienciaId = UUID.randomUUID();
        Experiencia actual = new Experiencia(experienciaId, UUID.randomUUID(), "NATURALEZA", "Sendero", "Huila", "Detalle", 2, null, true, OffsetDateTime.now(), null);
        when(experiencias.buscarPorId(experienciaId)).thenReturn(Optional.of(actual));
        when(experiencias.guardar(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

        servicio.inactivar(experienciaId);

        ArgumentCaptor<Experiencia> capturador = ArgumentCaptor.forClass(Experiencia.class);
        verify(experiencias).guardar(capturador.capture());
        assertThat(capturador.getValue().activa()).isFalse();
    }
}
