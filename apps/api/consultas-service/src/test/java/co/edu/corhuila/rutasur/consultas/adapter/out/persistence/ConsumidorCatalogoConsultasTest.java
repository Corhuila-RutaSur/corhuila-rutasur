package co.edu.corhuila.rutasur.consultas.adapter.out.persistence;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.corhuila.rutasur.consultas.adapter.out.persistence.ConsumidorCatalogoConsultas;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsClient;

/** Verifica que el evento de galería alimente la proyección local de Consultas. */
@ExtendWith(MockitoExtension.class)
class ConsumidorCatalogoConsultasTest {
    @Mock private SqsClient sqs;
    @Spy private ObjectMapper mapper = new ObjectMapper();
    @Mock private ExperienciaProyeccionRepositorio experiencias;
    @Mock private ServicioProyeccionRepositorio servicios;
    @Mock private ActividadProyeccionRepositorio actividades;
    @Mock private ImagenExperienciaProyeccionRepositorio imagenes;
    @Mock private InboxRepositorio inbox;
    @InjectMocks private ConsumidorCatalogoConsultas consumidor;

    @Test
    void debeReconstruirProyeccionDeImagenDesdeEventoOutbox() {
        UUID eventoId = UUID.randomUUID(); UUID imagenId = UUID.randomUUID(); UUID experienciaId = UUID.randomUUID();
        when(inbox.existsById(eventoId)).thenReturn(false);
        String evento = "{\"id\":\"" + eventoId + "\",\"tipo\":\"ImagenExperienciaActualizada\",\"origen\":\"catalogo\",\"datos\":{\"imagenId\":\"" + imagenId + "\",\"experienciaId\":\"" + experienciaId + "\",\"ruta\":\"/archivos/experiencias/uno/foto.jpg\",\"principal\":true,\"orden\":0,\"activa\":true}}";
        consumidor.procesar(evento, "recibo");
        ArgumentCaptor<ImagenExperienciaProyeccionEntidad> capturador = ArgumentCaptor.forClass(ImagenExperienciaProyeccionEntidad.class);
        verify(imagenes).save(capturador.capture());
        org.assertj.core.api.Assertions.assertThat(capturador.getValue().getRuta()).isEqualTo("/archivos/experiencias/uno/foto.jpg");
        org.assertj.core.api.Assertions.assertThat(capturador.getValue().isPrincipal()).isTrue();
    }
}
