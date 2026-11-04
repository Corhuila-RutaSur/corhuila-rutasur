package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import co.edu.corhuila.rutasur.contratos.evento.EventoIntegracion;
import com.fasterxml.jackson.databind.*;

import java.time.OffsetDateTime;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sns.*;
import software.amazon.awssdk.services.sns.model.PublishRequest;

/**
 * Publica eventos de Catálogo solo después de la transacción local.
 */
@Component
@Slf4j
@RequiredArgsConstructor
class PublicadorOutboxCatalogo {
    private final EventoOutboxRepositorioJpa eventos;
    private final SnsClient sns;
    private final ObjectMapper mapper;
    @Value("${app.mensajeria.topico-catalogo}")
    private String topico;

    @Scheduled(fixedDelayString = "${app.mensajeria.intervalo-publicacion-ms}")
    @Transactional
    public void publicar() {
        for (EventoOutboxEntidad e : eventos.findTop20ByPublicadoEnIsNullOrderByOcurridoEnAsc()) {
            try {
                JsonNode datos = mapper.readTree(e.getDatos());
                String cuerpo = mapper.writeValueAsString(new EventoIntegracion<>(e.getId(), e.getTipo(), e.getVersion(), e.getOcurridoEn(), "rutasur-catalogo", e.getCorrelacionId(), datos));
                sns.publish(PublishRequest.builder().topicArn(topico).message(cuerpo).build());
                e.setPublicadoEn(OffsetDateTime.now());
                e.setUltimoError(null);
                log.info("correlationId={} servicio=catalogo evento={} resultado=publicado", e.getCorrelacionId(), e.getTipo());
            } catch (Exception error) {
                e.setIntentosPublicacion(e.getIntentosPublicacion() + 1);
                e.setUltimoError(error.getMessage());
                log.warn("servicio=catalogo evento={} resultado=error-publicacion", e.getTipo(), error);
            }
        }
    }
}
