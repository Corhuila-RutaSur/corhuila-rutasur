package co.edu.corhuila.rutasur.notificaciones.adapter.in.messaging;

import co.edu.corhuila.rutasur.contratos.evento.ReservaCanceladaDatos;
import co.edu.corhuila.rutasur.contratos.evento.ReservaConfirmadaDatos;
import co.edu.corhuila.rutasur.contratos.evento.ReservaCreadaDatos;
import co.edu.corhuila.rutasur.notificaciones.application.port.in.RegistrarNotificacionCommand;
import co.edu.corhuila.rutasur.notificaciones.application.port.in.RegistrarNotificacionUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

/**
 * Convierte los eventos de Reservas en avisos internos visibles para cada usuario.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ConsumidorEventosReserva {
    private final SqsClient sqs;
    private final ObjectMapper objectMapper;
    private final RegistrarNotificacionUseCase registrar;
    @Value("${app.mensajeria.cola-reservas}")
    private String colaReservas;

    @Scheduled(fixedDelayString = "${app.mensajeria.intervalo-consumo-ms}")
    public void consumir() {
        try {
            sqs.receiveMessage(ReceiveMessageRequest.builder().queueUrl(colaReservas).maxNumberOfMessages(10).waitTimeSeconds(1).build()).messages()
                    .forEach(mensaje -> procesar(mensaje.body(), mensaje.receiptHandle()));
        } catch (Exception error) {
            log.warn("servicio=notificaciones consumidor=reservas resultado=error-consumo", error);
        }
    }

    private void procesar(String cuerpoSqs, String recibo) {
        try {
            JsonNode sobreSns = objectMapper.readTree(cuerpoSqs);
            JsonNode evento = sobreSns.hasNonNull("Message") ? objectMapper.readTree(sobreSns.get("Message").asText()) : sobreSns;
            RegistrarNotificacionCommand comando = aComando(evento);
            if (comando == null) return;
            registrar.registrar(comando);
            sqs.deleteMessage(DeleteMessageRequest.builder().queueUrl(colaReservas).receiptHandle(recibo).build());
        } catch (Exception error) {
            log.warn("servicio=notificaciones consumidor=reservas resultado=error-procesamiento", error);
        }
    }

    private RegistrarNotificacionCommand aComando(JsonNode evento) throws Exception {
        String tipo = evento.path("tipo").asText();
        UUID eventoId = UUID.fromString(evento.path("id").asText());
        String origen = evento.path("origen").asText("rutasur-reservas");
        if ("ReservaCreada".equals(tipo)) {
            ReservaCreadaDatos datos = objectMapper.treeToValue(evento.path("datos"), ReservaCreadaDatos.class);
            return new RegistrarNotificacionCommand(eventoId, tipo, origen, datos.usuarioIdExterno(), "Reserva creada", "Tu reserva " + datos.codigoReserva() + " está pendiente de pago.");
        }
        if ("ReservaConfirmada".equals(tipo)) {
            ReservaConfirmadaDatos datos = objectMapper.treeToValue(evento.path("datos"), ReservaConfirmadaDatos.class);
            return new RegistrarNotificacionCommand(eventoId, tipo, origen, datos.usuarioIdExterno(), "Reserva confirmada", "Tu reserva " + datos.codigoReserva() + " fue confirmada.");
        }
        if ("ReservaCancelada".equals(tipo)) {
            ReservaCanceladaDatos datos = objectMapper.treeToValue(evento.path("datos"), ReservaCanceladaDatos.class);
            return new RegistrarNotificacionCommand(eventoId, tipo, origen, datos.usuarioIdExterno(), "Reserva cancelada", "Tu reserva " + datos.codigoReserva() + " fue cancelada. Los cupos fueron liberados.");
        }
        return null;
    }
}
