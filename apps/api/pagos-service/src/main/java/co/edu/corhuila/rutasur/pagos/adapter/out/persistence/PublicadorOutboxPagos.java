package co.edu.corhuila.rutasur.pagos.adapter.out.persistence;

import co.edu.corhuila.rutasur.contratos.evento.EventoIntegracion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

/**
 * Publica hechos del Outbox solo después de que la transacción de Pagos quedó confirmada.
 */
@Component
@Slf4j
@RequiredArgsConstructor
class PublicadorOutboxPagos {
    private final EventoOutboxRepositorioJpa eventos;
    private final SnsClient sns;
    private final ObjectMapper objectMapper;
    @Value("${app.mensajeria.topico-pagos}")
    private String topicoPagos;

    @Scheduled(fixedDelayString = "${app.mensajeria.intervalo-publicacion-ms}")
    @Transactional
    public void publicarPendientes() {
        for (EventoOutboxEntidad evento : eventos.findTop20ByPublicadoEnIsNullOrderByOcurridoEnAsc()) {
            try {
                JsonNode datos = objectMapper.readTree(evento.getDatos());
                String cuerpo = objectMapper.writeValueAsString(new EventoIntegracion<>(evento.getId(), evento.getTipo(), evento.getVersion(),
                        evento.getOcurridoEn(), "rutasur-pagos", evento.getCorrelacionId(), datos));
                sns.publish(PublishRequest.builder().topicArn(topicoPagos).message(cuerpo).build());
                evento.setPublicadoEn(OffsetDateTime.now());
                evento.setUltimoError(null);
                log.info("correlationId={} servicio=pagos evento={} resultado=publicado", evento.getCorrelacionId(), evento.getTipo());
            } catch (Exception error) {
                evento.setIntentosPublicacion(evento.getIntentosPublicacion() + 1);
                evento.setUltimoError(error.getMessage());
                log.warn("correlationId={} servicio=pagos evento={} resultado=error-publicacion", evento.getCorrelacionId(), evento.getTipo(), error);
            }
        }
    }
}
