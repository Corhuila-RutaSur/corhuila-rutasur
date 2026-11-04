package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import co.edu.corhuila.rutasur.contratos.evento.*;
import com.fasterxml.jackson.databind.*;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sqs.*;
import software.amazon.awssdk.services.sqs.model.*;

/**
 * Construye referencias locales de Catálogo para programar salidas sin dependencia remota.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ConsumidorCatalogoReservas {
    private final SqsClient sqs;
    private final ObjectMapper mapper;
    private final ReferenciaExperienciaRepositorio experiencias;
    private final ReferenciaServicioRepositorio servicios;
    private final ReferenciaActividadRepositorio actividades;
    private final EventoInboxRepositorioJpa inbox;
    @Value("${app.mensajeria.cola-catalogo}")
    private String cola;

    @Scheduled(fixedDelayString = "${app.mensajeria.intervalo-consumo-ms}")
    public void consumir() {
        try {
            sqs.receiveMessage(ReceiveMessageRequest.builder().queueUrl(cola).maxNumberOfMessages(10).waitTimeSeconds(1).build()).messages().forEach(m -> procesar(m.body(), m.receiptHandle()));
        } catch (Exception e) {
            log.warn("servicio=reservas consumidor=catalogo resultado=error", e);
        }
    }

    @Transactional
    void procesar(String cuerpo, String recibo) {
        try {
            JsonNode sns = mapper.readTree(cuerpo);
            JsonNode e = sns.has("Message") ? mapper.readTree(sns.get("Message").asText()) : sns;
            UUID id = UUID.fromString(e.path("id").asText());
            if (!inbox.existsById(id)) {
                String tipo = e.path("tipo").asText();
                if ("ExperienciaActualizada".equals(tipo)) {
                    var d = mapper.treeToValue(e.path("datos"), ExperienciaSincronizadaDatos.class);
                    var x = new ReferenciaExperienciaEntidad();
                    x.setId(d.experienciaId());
                    x.setNombre(d.nombre());
                    x.setDestino(d.destino());
                    x.setActiva(d.activa());
                    x.setActualizadoEn(OffsetDateTime.now());
                    experiencias.save(x);
                } else if ("ServicioActualizado".equals(tipo)) {
                    var d = mapper.treeToValue(e.path("datos"), ServicioSincronizadoDatos.class);
                    var x = new ReferenciaServicioEntidad();
                    x.setId(d.servicioId());
                    x.setExperienciaId(d.experienciaId());
                    x.setTipo(d.tipo());
                    x.setNombre(d.nombre());
                    x.setActivo(d.activo());
                    x.setActualizadoEn(OffsetDateTime.now());
                    servicios.save(x);
                } else if ("ActividadActualizada".equals(tipo)) {
                    var d = mapper.treeToValue(e.path("datos"), ActividadSincronizadaDatos.class);
                    var x = new ReferenciaActividadEntidad();
                    x.setId(d.actividadId());
                    x.setExperienciaId(d.experienciaId());
                    x.setNombre(d.nombre());
                    x.setActivo(d.activa());
                    x.setActualizadoEn(OffsetDateTime.now());
                    actividades.save(x);
                } else {
                    return;
                }
                var i = new EventoInboxEntidad();
                i.setIdEvento(id);
                i.setTipo(tipo);
                i.setOrigen(e.path("origen").asText());
                i.setRecibidoEn(OffsetDateTime.now());
                i.setProcesadoEn(OffsetDateTime.now());
                i.setEstado("PROCESADO");
                inbox.save(i);
            }
            sqs.deleteMessage(DeleteMessageRequest.builder().queueUrl(cola).receiptHandle(recibo).build());
        } catch (Exception error) {
            log.warn("servicio=reservas consumidor=catalogo resultado=error-procesamiento", error);
        }
    }
}
