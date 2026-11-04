package co.edu.corhuila.rutasur.catalogo.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.catalogo.application.port.in.ActualizarImagenExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.in.ArchivoImagenCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.out.AlmacenamientoImagenPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ImagenExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.model.Experiencia;
import co.edu.corhuila.rutasur.catalogo.domain.model.ImagenExperiencia;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Pruebas unitarias de las reglas de portada y eliminación lógica de imágenes. */
@ExtendWith(MockitoExtension.class)
class GestionarImagenExperienciaServicioTest {
    @Mock private ExperienciaPuertoSalida experiencias;
    @Mock private ImagenExperienciaPuertoSalida imagenes;
    @Mock private AlmacenamientoImagenPuertoSalida almacenamiento;
    @Mock private PublicadorCambiosCatalogo eventos;
    @InjectMocks private GestionarImagenExperienciaServicio servicio;

    @Test
    void debeConvertirLaPrimeraImagenEnPortada() {
        UUID experienciaId = UUID.randomUUID();
        when(experiencias.bloquearPorId(experienciaId)).thenReturn(Optional.of(experiencia(experienciaId)));
        when(imagenes.listarActivasPorExperiencia(experienciaId)).thenReturn(List.of());
        when(almacenamiento.guardar(any(), any(), any())).thenReturn("/archivos/experiencias/uno/foto.jpg");
        when(imagenes.guardar(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

        ImagenExperiencia creada = servicio.cargar(experienciaId, archivo(), false, 0, UUID.randomUUID());

        assertThat(creada.principal()).isTrue();
        verify(imagenes).quitarPortadaActiva(experienciaId);
        verify(eventos).imagen(any(), any());
    }

    @Test
    void debeQuitarPortadaAnteriorAntesDeMarcarLaNueva() {
        UUID experienciaId = UUID.randomUUID();
        UUID imagenId = UUID.randomUUID();
        ImagenExperiencia actual = new ImagenExperiencia(imagenId, experienciaId, "/archivos/experiencias/uno/foto.jpg", false, 2, true, OffsetDateTime.now(), null);
        when(experiencias.bloquearPorId(experienciaId)).thenReturn(Optional.of(experiencia(experienciaId)));
        when(imagenes.buscarPorId(imagenId)).thenReturn(Optional.of(actual));
        when(imagenes.guardar(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

        ImagenExperiencia actualizada = servicio.actualizar(experienciaId, imagenId, new ActualizarImagenExperienciaCommand(true, 1, UUID.randomUUID()));

        assertThat(actualizada.principal()).isTrue();
        assertThat(actualizada.orden()).isEqualTo(1);
        verify(imagenes).quitarPortadaActiva(experienciaId);
    }

    @Test
    void debeInactivarLogicamenteYPromoverLaSiguienteImagen() {
        UUID experienciaId = UUID.randomUUID(); UUID portadaId = UUID.randomUUID(); UUID siguienteId = UUID.randomUUID();
        ImagenExperiencia portada = new ImagenExperiencia(portadaId, experienciaId, "/archivos/experiencias/uno/portada.jpg", true, 0, true, OffsetDateTime.now(), null);
        ImagenExperiencia siguiente = new ImagenExperiencia(siguienteId, experienciaId, "/archivos/experiencias/uno/galeria.jpg", false, 1, true, OffsetDateTime.now(), null);
        when(experiencias.bloquearPorId(experienciaId)).thenReturn(Optional.of(experiencia(experienciaId)));
        when(imagenes.buscarPorId(portadaId)).thenReturn(Optional.of(portada));
        when(imagenes.listarActivasPorExperiencia(experienciaId)).thenReturn(List.of(portada, siguiente));
        when(imagenes.guardar(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

        servicio.inactivar(experienciaId, portadaId, UUID.randomUUID());

        ArgumentCaptor<ImagenExperiencia> capturador = ArgumentCaptor.forClass(ImagenExperiencia.class);
        verify(imagenes, org.mockito.Mockito.times(2)).guardar(capturador.capture());
        assertThat(capturador.getAllValues()).anyMatch(imagen -> !imagen.activa()).anyMatch(ImagenExperiencia::principal);
    }

    private ArchivoImagenCommand archivo() { return new ArchivoImagenCommand("foto.jpg", "image/jpeg", new byte[]{1, 2, 3}); }
    private Experiencia experiencia(UUID id) { return new Experiencia(id, UUID.randomUUID(), "NATURALEZA", "Sendero", "Huila", "Detalle", 3, null, true, OffsetDateTime.now(), null); }
}
