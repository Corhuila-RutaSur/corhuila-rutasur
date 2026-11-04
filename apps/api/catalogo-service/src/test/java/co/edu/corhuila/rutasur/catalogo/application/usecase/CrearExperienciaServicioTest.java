package co.edu.corhuila.rutasur.catalogo.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.catalogo.application.port.in.CrearExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.application.port.out.TipoExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.exception.ExcepcionReglaNegocio;
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
 * Pruebas unitarias del caso de uso que crea experiencias en Catálogo.
 */
@ExtendWith(MockitoExtension.class)
class CrearExperienciaServicioTest {

    @Mock
    private ExperienciaPuertoSalida experiencias;

    @Mock
    private TipoExperienciaPuertoSalida tipos;

    @Mock
    private PublicadorCambiosCatalogo eventos;

    @InjectMocks
    private CrearExperienciaServicio servicio;

    /**
     * Debe normalizar los datos y persistir una experiencia activa.
     */
    @Test
    void debeCrearExperienciaActivaConDatosNormalizados() {
        UUID identificador = UUID.randomUUID();
        UUID tipoIdentificador = UUID.randomUUID();
        when(tipos.buscarPorId(tipoIdentificador)).thenReturn(Optional.of(new TipoExperiencia(tipoIdentificador,
                "NATURALEZA", "Paisajes", true, OffsetDateTime.now(), null)));
        when(experiencias.guardar(any())).thenAnswer(invocacion -> {
            Experiencia experiencia = invocacion.getArgument(0);
            return new Experiencia(identificador, experiencia.tipoExperienciaId(), experiencia.tipo(), experiencia.nombre(), experiencia.destino(),
                    experiencia.descripcion(), experiencia.duracionHoras(), experiencia.recomendaciones(),
                    experiencia.activa(), experiencia.creadoEn(), experiencia.actualizadoEn());
        });

        Experiencia resultado = servicio.crear(new CrearExperienciaCommand(
                tipoIdentificador, "  Sendero del rio  ", "  Huila ", "  Recorrido guiado  ",
                5, "  Llevar hidratacion  ", UUID.randomUUID()));

        ArgumentCaptor<Experiencia> capturador = ArgumentCaptor.forClass(Experiencia.class);
        verify(experiencias).guardar(capturador.capture());

        assertThat(resultado.id()).isEqualTo(identificador);
        assertThat(capturador.getValue())
                .extracting(Experiencia::tipo, Experiencia::nombre, Experiencia::destino,
                        Experiencia::descripcion, Experiencia::recomendaciones, Experiencia::activa)
                .containsExactly("NATURALEZA", "Sendero del rio", "Huila", "Recorrido guiado",
                        "Llevar hidratacion", true);
    }

    /**
     * No debe permitir una duración nula, cero o negativa.
     */
    @Test
    void debeRechazarDuracionNoPositiva() {
        CrearExperienciaCommand comando = new CrearExperienciaCommand(
                UUID.randomUUID(), "Sendero", "Huila", "Recorrido", 0, null, UUID.randomUUID());

        assertThatThrownBy(() -> servicio.crear(comando))
                .isInstanceOf(ExcepcionReglaNegocio.class)
                .hasMessage("experiencia.duracion.invalida");
    }
}
