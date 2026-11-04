package co.edu.corhuila.rutasur.consultas.adapter.out.persistence;

import co.edu.corhuila.rutasur.contratos.evento.*;
import com.fasterxml.jackson.databind.*;

import java.time.OffsetDateTime;
import java.util.*;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sqs.*;
import software.amazon.awssdk.services.sqs.model.*;

/**
 * Reconstruye la proyección pública de Catálogo desde eventos.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ConsumidorCatalogoConsultas {
    private final SqsClient sqs;
    private final ObjectMapper mapper;
    private final ExperienciaProyeccionRepositorio experiencias;
    private final ServicioProyeccionRepositorio servicios;
    private final ActividadProyeccionRepositorio actividades;
    private final ImagenExperienciaProyeccionRepositorio imagenes;
    private final InboxRepositorio inbox;
    @Value("${app.mensajeria.cola-catalogo}")
    String cola;

    @Scheduled(fixedDelayString = "${app.mensajeria.intervalo-consumo-ms}")
    public void consumir() {
        try {
            sqs.receiveMessage(ReceiveMessageRequest.builder().queueUrl(cola).maxNumberOfMessages(10).waitTimeSeconds(1).build()).messages().forEach(m -> procesar(m.body(), m.receiptHandle()));
        } catch (Exception e) {
            log.warn("servicio=consultas consumidor=catalogo resultado=error", e);
        }
    }

    @Transactional
    public void procesar(String cuerpo, String recibo) {
        try {
            JsonNode s = mapper.readTree(cuerpo);
            JsonNode e = s.has("Message") ? mapper.readTree(s.get("Message").asText()) : s;
            UUID id = UUID.fromString(e.path("id").asText());
            if (!inbox.existsById(id)) {
                String t = e.path("tipo").asText();
                if ("ExperienciaActualizada".equals(t)) {
                    var d = mapper.treeToValue(e.path("datos"), ExperienciaSincronizadaDatos.class);
                    var x = new ExperienciaProyeccionEntidad();
                    x.setId(d.experienciaId());
                    x.setNombre(d.nombre());
                    x.setDestino(d.destino());
                    x.setDescripcion(d.descripcion());
                    x.setActiva(d.activa());
                    x.setActualizadoEn(OffsetDateTime.now());
                    experiencias.save(x);
                } else if ("ServicioActualizado".equals(t)) {
                    var d = mapper.treeToValue(e.path("datos"), ServicioSincronizadoDatos.class);
                    var x = new ServicioProyeccionEntidad();
                    x.setId(d.servicioId());
                    x.setExperienciaId(d.experienciaId());
                    x.setTipo(d.tipo());
                    x.setNombre(d.nombre());
                    x.setDescripcion(d.descripcion());
                    x.setActivo(d.activo());
                    x.setActualizadoEn(OffsetDateTime.now());
                    servicios.save(x);
                } else if ("ActividadActualizada".equals(t)) {
                    var d = mapper.treeToValue(e.path("datos"), ActividadSincronizadaDatos.class);
                    var x = new ActividadProyeccionEntidad();
                    x.setId(d.actividadId());
                    x.setExperienciaId(d.experienciaId());
                    x.setNombre(d.nombre());
                    x.setDescripcion(d.descripcion());
                    x.setDuracionHoras(d.duracionHoras());
                    x.setActiva(d.activa());
                    x.setActualizadoEn(OffsetDateTime.now());
                    actividades.save(x);
                } else if ("ImagenExperienciaActualizada".equals(t)) {
                    var d = mapper.treeToValue(e.path("datos"), ImagenExperienciaSincronizadaDatos.class);
                    var x = new ImagenExperienciaProyeccionEntidad();
                    x.setId(d.imagenId()); x.setExperienciaId(d.experienciaId()); x.setRuta(d.ruta());
                    x.setPrincipal(d.principal()); x.setOrden(d.orden()); x.setActiva(d.activa()); x.setActualizadoEn(OffsetDateTime.now());
                    imagenes.save(x);
                } else {
                    return;
                }
                var i = new InboxEntidad();
                i.setId(id);
                i.setTipo(t);
                i.setOrigen(e.path("origen").asText());
                i.setRecibidoEn(OffsetDateTime.now());
                i.setProcesadoEn(OffsetDateTime.now());
                i.setEstado("PROCESADO");
                inbox.save(i);
            }
            sqs.deleteMessage(DeleteMessageRequest.builder().queueUrl(cola).receiptHandle(recibo).build());
        } catch (Exception error) {
            log.warn("servicio=consultas consumidor=catalogo resultado=error-procesamiento", error);
        }
    }
}
