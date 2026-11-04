package co.edu.corhuila.rutasur.pagos.adapter.in.messaging;

import co.edu.corhuila.rutasur.contratos.evento.ReservaCanceladaDatos;
import co.edu.corhuila.rutasur.pagos.application.port.in.ProcesarReembolsoCommand;
import co.edu.corhuila.rutasur.pagos.application.port.in.ProcesarReembolsoUseCase;
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
 * Consume cancelaciones definitivas; reembolsa solo pagos aprobados.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ConsumidorCancelacionReserva {
    private final SqsClient sqs;
    private final ObjectMapper objectMapper;
    private final ProcesarReembolsoUseCase reembolsar;
    @Value("${app.mensajeria.cola-reservas}")
    private String colaReservas;

    @Scheduled(fixedDelayString = "${app.mensajeria.intervalo-consumo-ms}")
    public void consumir() {
        try {
            sqs.receiveMessage(ReceiveMessageRequest.builder().queueUrl(colaReservas).maxNumberOfMessages(10).waitTimeSeconds(1).build()).messages().forEach(mensaje -> procesar(mensaje.body(), mensaje.receiptHandle()));
        } catch (Exception error) {
            log.warn("servicio=pagos consumidor=reservas resultado=error-consumo", error);
        }
    }

    private void procesar(String cuerpoSqs, String recibo) {
        try {
            JsonNode sobreSns = objectMapper.readTree(cuerpoSqs);
            JsonNode evento = sobreSns.hasNonNull("Message") ? objectMapper.readTree(sobreSns.get("Message").asText()) : sobreSns;
            if (!"ReservaCancelada".equals(evento.path("tipo").asText())) {
                sqs.deleteMessage(DeleteMessageRequest.builder().queueUrl(colaReservas).receiptHandle(recibo).build());
                return;
            }
            ReservaCanceladaDatos datos = objectMapper.treeToValue(evento.path("datos"), ReservaCanceladaDatos.class);
            reembolsar.procesar(new ProcesarReembolsoCommand(UUID.fromString(evento.path("id").asText()), datos.reservaId(), datos.motivo(), UUID.fromString(evento.path("correlacionId").asText())));
            sqs.deleteMessage(DeleteMessageRequest.builder().queueUrl(colaReservas).receiptHandle(recibo).build());
        } catch (Exception error) {
            log.warn("servicio=pagos consumidor=cancelacionReserva resultado=error-procesamiento", error);
        }
    }
}
