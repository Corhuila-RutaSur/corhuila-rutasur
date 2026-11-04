package co.edu.corhuila.rutasur.catalogo.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.catalogo.application.port.in.CrearTipoExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.out.TipoExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.catalogo.domain.model.TipoExperiencia;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de las reglas del catálogo de tipos turísticos.
 */
@ExtendWith(MockitoExtension.class)
class GestionarTipoExperienciaServicioTest {
    @Mock
    private TipoExperienciaPuertoSalida tipos;
    @InjectMocks
    private GestionarTipoExperienciaServicio servicio;

    /**
     * Debe convertir el nombre a mayúscula y crear un tipo activo.
     */
    @Test
    void debeCrearTipoNormalizado() {
        UUID identificador = UUID.randomUUID();
        when(tipos.existeActivoPorNombre("NATURALEZA")).thenReturn(false);
        when(tipos.guardar(any())).thenAnswer(invocacion -> {
            TipoExperiencia tipo = invocacion.getArgument(0);
            return new TipoExperiencia(identificador, tipo.nombre(), tipo.descripcion(), tipo.activo(), tipo.creadoEn(), tipo.actualizadoEn());
        });

        TipoExperiencia creado = servicio.crear(new CrearTipoExperienciaCommand(" naturaleza ", " Paisajes ", UUID.randomUUID()));

        ArgumentCaptor<TipoExperiencia> capturador = ArgumentCaptor.forClass(TipoExperiencia.class);
        verify(tipos).guardar(capturador.capture());
        assertThat(creado.id()).isEqualTo(identificador);
        assertThat(capturador.getValue()).extracting(TipoExperiencia::nombre, TipoExperiencia::descripcion, TipoExperiencia::activo)
                .containsExactly("NATURALEZA", "Paisajes", true);
    }

    /**
     * Debe impedir duplicar un tipo activo incluso cuando cambia el uso de mayúsculas.
     */
    @Test
    void debeRechazarNombreActivoDuplicado() {
        when(tipos.existeActivoPorNombre("AVENTURA")).thenReturn(true);

        assertThatThrownBy(() -> servicio.crear(new CrearTipoExperienciaCommand("aventura", null, UUID.randomUUID())))
                .isInstanceOf(ExcepcionReglaNegocio.class)
                .hasMessage("tipo.experiencia.nombre.duplicado");
        verify(tipos, never()).guardar(any());
    }
}
