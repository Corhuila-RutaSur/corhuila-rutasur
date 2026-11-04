package co.edu.corhuila.rutasur.consultas.adapter.out.persistence;

import co.edu.corhuila.rutasur.contratos.evento.SalidaSincronizadaDatos;
import co.edu.corhuila.rutasur.contratos.evento.ProgramacionServicioSincronizadaDatos;
import co.edu.corhuila.rutasur.contratos.evento.ProgramacionActividadSincronizadaDatos;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

/**
 * Reconstruye salidas públicas desde Reservas sin lectura síncrona entre servicios.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ConsumidorSalidasConsultas {
    private final SqsClient sqs;
    private final ObjectMapper mapper;
    private final SalidaProyeccionRepositorio salidas;
    private final ProgramacionServicioProyeccionRepositorio servicios;
    private final ProgramacionActividadProyeccionRepositorio actividades;
    private final InboxRepositorio inbox;
    @Value("${app.mensajeria.cola-reservas}")
    String cola;

    @Scheduled(fixedDelayString = "${app.mensajeria.intervalo-consumo-ms}")
    public void consumir() {
        try {
            sqs.receiveMessage(ReceiveMessageRequest.builder().queueUrl(cola).maxNumberOfMessages(10).waitTimeSeconds(1).build()).messages().forEach(m -> procesar(m.body(), m.receiptHandle()));
        } catch (Exception e) {
            log.warn("servicio=consultas consumidor=reservas resultado=error", e);
        }
    }

    @Transactional
    void procesar(String cuerpo, String recibo) {
        try {
            JsonNode sobre = mapper.readTree(cuerpo);
            JsonNode evento = sobre.has("Message") ? mapper.readTree(sobre.get("Message").asText()) : sobre;
            UUID eventoId = UUID.fromString(evento.path("id").asText());
            if (!inbox.existsById(eventoId)) {
                String tipo = evento.path("tipo").asText();
                if ("SalidaActualizada".equals(tipo)) {
                    SalidaSincronizadaDatos d = mapper.treeToValue(evento.path("datos"), SalidaSincronizadaDatos.class);
                    SalidaProyeccionEntidad x = new SalidaProyeccionEntidad();
                    x.setId(d.salidaId()); x.setExperienciaId(d.experienciaIdExterna()); x.setFechaHoraInicio(d.fechaHoraInicio());
                    x.setCuposDisponibles(d.cuposDisponibles()); x.setEstado(d.estado()); x.setActualizadoEn(OffsetDateTime.now()); salidas.save(x);
                } else if ("ProgramacionServicioActualizada".equals(tipo)) {
                    ProgramacionServicioSincronizadaDatos d = mapper.treeToValue(evento.path("datos"), ProgramacionServicioSincronizadaDatos.class);
                    ProgramacionServicioProyeccionEntidad x = new ProgramacionServicioProyeccionEntidad();
                    x.setId(d.programacionId()); x.setSalidaId(d.salidaId()); x.setServicioIdExterno(d.servicioIdExterno()); x.setNombre(d.nombre()); x.setPrecio(d.precio()); x.setCuposDisponibles(d.cuposDisponibles()); x.setActivo(d.activo()); x.setActualizadoEn(OffsetDateTime.now()); servicios.save(x);
                } else if ("ProgramacionActividadActualizada".equals(tipo)) {
                    ProgramacionActividadSincronizadaDatos d = mapper.treeToValue(evento.path("datos"), ProgramacionActividadSincronizadaDatos.class);
                    ProgramacionActividadProyeccionEntidad x = new ProgramacionActividadProyeccionEntidad();
                    x.setId(d.programacionId()); x.setSalidaId(d.salidaId()); x.setActividadIdExterna(d.actividadIdExterna()); x.setNombre(d.nombre()); x.setPrecio(d.precio()); x.setActivo(d.activo()); x.setActualizadoEn(OffsetDateTime.now()); actividades.save(x);
                } else {
                    sqs.deleteMessage(DeleteMessageRequest.builder().queueUrl(cola).receiptHandle(recibo).build());
                    return;
                }
                InboxEntidad i = new InboxEntidad();
                i.setId(eventoId);
                i.setTipo(tipo);
                i.setOrigen(evento.path("origen").asText());
                i.setRecibidoEn(OffsetDateTime.now());
                i.setProcesadoEn(OffsetDateTime.now());
                i.setEstado("PROCESADO");
                inbox.save(i);
            }
            sqs.deleteMessage(DeleteMessageRequest.builder().queueUrl(cola).receiptHandle(recibo).build());
        } catch (Exception e) {
            log.warn("servicio=consultas consumidor=salidas resultado=error-procesamiento", e);
        }
    }
}
