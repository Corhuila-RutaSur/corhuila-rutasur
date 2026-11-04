package co.edu.corhuila.rutasur.catalogo.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.catalogo.application.port.in.ServicioExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ServicioExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.model.Experiencia;
import co.edu.corhuila.rutasur.catalogo.domain.model.ServicioExperiencia;

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
 * Pruebas de reglas de negocio de servicios por experiencia.
 */
@ExtendWith(MockitoExtension.class)
class GestionarServicioExperienciaServicioTest {
    @Mock
    private PublicadorCambiosCatalogo eventos;
    @Mock
    private ExperienciaPuertoSalida experiencias;
    @Mock
    private ServicioExperienciaPuertoSalida servicios;
    @InjectMocks
    private GestionarServicioExperienciaServicio servicio;

    @Test
    void debeCrearServicioParaExperienciaActiva() {
        UUID experienciaId = UUID.randomUUID();
        UUID servicioId = UUID.randomUUID();
        when(experiencias.buscarPorId(experienciaId)).thenReturn(Optional.of(experiencia(experienciaId)));
        when(servicios.existeActivoPorExperienciaYNombre(experienciaId, "Transporte local")).thenReturn(false);
        when(servicios.guardar(any())).thenAnswer(invocacion -> {
            ServicioExperiencia valor = invocacion.getArgument(0);
            return new ServicioExperiencia(servicioId, valor.experienciaId(), valor.tipo(), valor.nombre(), valor.descripcion(), valor.activo(), valor.creadoEn(), valor.actualizadoEn());
        });
        ServicioExperiencia creado = servicio.crear(experienciaId, new ServicioExperienciaCommand("TRANSPORTE", " Transporte local ", " Ida y vuelta ", UUID.randomUUID()));
        assertThat(creado.id()).isEqualTo(servicioId);
        assertThat(creado.nombre()).isEqualTo("Transporte local");
    }

    @Test
    void debeInactivarServicioLogicamente() {
        UUID experienciaId = UUID.randomUUID();
        UUID servicioId = UUID.randomUUID();
        when(experiencias.buscarPorId(experienciaId)).thenReturn(Optional.of(experiencia(experienciaId)));
        when(servicios.buscarPorId(servicioId)).thenReturn(Optional.of(new ServicioExperiencia(servicioId, experienciaId, "ALOJAMIENTO", "Hospedaje", null, true, OffsetDateTime.now(), null)));
        when(servicios.guardar(any())).thenAnswer(invocacion -> invocacion.getArgument(0));
        servicio.inactivar(experienciaId, servicioId);
        ArgumentCaptor<ServicioExperiencia> capturador = ArgumentCaptor.forClass(ServicioExperiencia.class);
        verify(servicios).guardar(capturador.capture());
        assertThat(capturador.getValue().activo()).isFalse();
    }

    private Experiencia experiencia(UUID id) {
        return new Experiencia(id, UUID.randomUUID(), "NATURALEZA", "Sendero", "Huila", "Detalle", 3, null, true, OffsetDateTime.now(), null);
    }
}
