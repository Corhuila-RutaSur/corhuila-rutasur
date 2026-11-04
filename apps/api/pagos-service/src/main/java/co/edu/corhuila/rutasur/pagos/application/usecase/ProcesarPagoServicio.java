package co.edu.corhuila.rutasur.pagos.application.usecase;

import co.edu.corhuila.rutasur.contratos.evento.PagoAprobadoDatos;
import co.edu.corhuila.rutasur.contratos.evento.PagoNoAprobadoDatos;
import co.edu.corhuila.rutasur.pagos.application.port.in.*;
import co.edu.corhuila.rutasur.pagos.application.port.out.*;
import co.edu.corhuila.rutasur.pagos.domain.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.*;
import java.util.*;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Procesa cobros simulados y registra el resultado en Outbox dentro de la misma transacción.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProcesarPagoServicio implements ProcesarPagoUseCase {
    private final PagoPuertoSalida pagos;
    private final EventoOutboxPuertoSalida outbox;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Pago procesar(ProcesarPagoCommand c) {
        return pagos.buscarPorCorrelacion(c.correlacionId()).orElseGet(() -> {
            String estado = c.resultado().name();
            Pago pago = pagos.guardar(new Pago(null, c.reservaIdExterna(), c.correlacionId(), c.monto(), "COP", estado, "SIM-" + UUID.randomUUID().toString().substring(0, 8)));
            registrarResultado(pago, c.correlacionId());
            log.info("correlationId={} servicio=pagos casoUso=procesarPago pagoId={} estado={}", c.correlacionId(), pago.id(), estado);
            return pago;
        });
    }

    private void registrarResultado(Pago pago, UUID correlacionId) {
        if ("APROBADO".equals(pago.estado())) {
            outbox.registrar("PagoAprobado", pago.id(), correlacionId, serializar(new PagoAprobadoDatos(pago.id(), pago.reservaIdExterna(), pago.monto(), pago.moneda(), pago.referenciaProveedor())));
            return;
        }
        String tipo = "RECHAZADO".equals(pago.estado()) ? "PagoRechazado" : "PagoExpirado";
        outbox.registrar(tipo, pago.id(), correlacionId, serializar(new PagoNoAprobadoDatos(pago.id(), pago.reservaIdExterna(), pago.monto(), pago.moneda(), pago.referenciaProveedor(), pago.estado())));
    }

    private String serializar(Object datos) {
        try {
            return objectMapper.writeValueAsString(datos);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No fue posible serializar el evento de pago.", e);
        }
    }
}
