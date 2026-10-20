package co.edu.corhuila.rutasur.service;

import co.edu.corhuila.rutasur.dto.request.PagoSimuladoRequest;
import co.edu.corhuila.rutasur.dto.response.PagoResponse;
import co.edu.corhuila.rutasur.entity.*;
import co.edu.corhuila.rutasur.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.repository.*;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Procesa pagos simulados sin almacenar datos financieros sensibles.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PagoServicio {
    private final PagoRepositorio pagoRepositorio;
    private final ReservaRepositorio reservaRepositorio;

    @Transactional
    public PagoResponse pagarSimulado(Long reservaId, PagoSimuladoRequest solicitud) {
        Reserva reserva = reservaRepositorio.findByIdAndEstadoNot(reservaId, EstadoReserva.CANCELADA).orElseThrow(() -> new ExcepcionReglaNegocio("reserva.no_encontrada"));
        if (reserva.getEstado() != EstadoReserva.PENDIENTE || pagoRepositorio.existsByReservaIdAndEstado(reservaId, EstadoPago.APROBADO))
            throw new ExcepcionReglaNegocio("pago.no.permitido");
        Pago pago = new Pago();
        pago.setReservaId(reservaId);
        pago.setReferencia("SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pago.setMetodoPago(solicitud.metodoPago().trim().toUpperCase());
        pago.setValor(reserva.getTotal());
        pago.setEstado(EstadoPago.APROBADO);
        pago.setFechaPago(OffsetDateTime.now());
        pago.setRespuestaPasarela("Pago aprobado por el simulador interno de RutaSur.");
        Pago guardado = pagoRepositorio.save(pago);
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        log.info("evento=pago_simulado_aprobado reservaId={} pagoId={}", reservaId, guardado.getId());
        return new PagoResponse(guardado.getId(), reservaId, guardado.getReferencia(), guardado.getMetodoPago(), guardado.getValor(), guardado.getEstado(), guardado.getFechaPago());
    }
}
