package co.edu.corhuila.rutasur.reservas.adapter.in.messaging;

import co.edu.corhuila.rutasur.contratos.evento.PagoAprobadoDatos;
import co.edu.corhuila.rutasur.contratos.evento.PagoNoAprobadoDatos;
import co.edu.corhuila.rutasur.reservas.application.port.in.CancelarReservaPorPagoCommand;
import co.edu.corhuila.rutasur.reservas.application.port.in.CancelarReservaPorPagoUseCase;
import co.edu.corhuila.rutasur.reservas.application.port.in.ConfirmarReservaPorPagoCommand;
import co.edu.corhuila.rutasur.reservas.application.port.in.ConfirmarReservaPorPagoUseCase;
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
 * Consume resultados de pago desde la cola privada de Reservas y aplica la transición o compensación idempotente.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ConsumidorPagoAprobado {
    private final SqsClient sqs;
    private final ObjectMapper objectMapper;
    private final ConfirmarReservaPorPagoUseCase confirmarReserva;
    private final CancelarReservaPorPagoUseCase cancelarReserva;
    @Value("${app.mensajeria.cola-pagos}")
    private String colaPagos;

    @Scheduled(fixedDelayString = "${app.mensajeria.intervalo-consumo-ms}")
    public void consumir() {
        try {
            sqs.receiveMessage(ReceiveMessageRequest.builder().queueUrl(colaPagos).maxNumberOfMessages(10).waitTimeSeconds(1).build()).messages()
                    .forEach(mensaje -> procesar(mensaje.body(), mensaje.receiptHandle()));
        } catch (Exception error) {
            log.warn("servicio=reservas consumidor=pagos resultado=error-consumo", error);
        }
    }

    private void procesar(String cuerpoSqs, String recibo) {
        try {
            JsonNode sobreSns = objectMapper.readTree(cuerpoSqs);
            JsonNode evento = sobreSns.hasNonNull("Message") ? objectMapper.readTree(sobreSns.get("Message").asText()) : sobreSns;
            String tipo = evento.path("tipo").asText();
            UUID eventoId = UUID.fromString(evento.path("id").asText());
            UUID correlacionId = UUID.fromString(evento.path("correlacionId").asText());
            if ("PagoAprobado".equals(tipo)) {
                PagoAprobadoDatos datos = objectMapper.treeToValue(evento.path("datos"), PagoAprobadoDatos.class);
                confirmarReserva.confirmar(new ConfirmarReservaPorPagoCommand(eventoId, datos.pagoId(), datos.reservaIdExterna(),
                        datos.monto(), datos.moneda(), datos.referenciaProveedor(), correlacionId));
            } else if ("PagoRechazado".equals(tipo) || "PagoExpirado".equals(tipo)) {
                PagoNoAprobadoDatos datos = objectMapper.treeToValue(evento.path("datos"), PagoNoAprobadoDatos.class);
                cancelarReserva.cancelar(new CancelarReservaPorPagoCommand(eventoId, datos.pagoId(), datos.reservaIdExterna(),
                        datos.monto(), datos.moneda(), datos.referenciaProveedor(), datos.motivo(), correlacionId));
            } else {
                return;
            }
            sqs.deleteMessage(DeleteMessageRequest.builder().queueUrl(colaPagos).receiptHandle(recibo).build());
        } catch (Exception error) {
            log.warn("servicio=reservas consumidor=resultadoPago resultado=error-procesamiento", error);
        }
    }
}
