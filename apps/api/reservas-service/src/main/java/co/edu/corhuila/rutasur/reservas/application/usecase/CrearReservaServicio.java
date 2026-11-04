package co.edu.corhuila.rutasur.reservas.application.usecase;

import co.edu.corhuila.rutasur.contratos.evento.ReservaCreadaDatos;
import co.edu.corhuila.rutasur.contratos.evento.SalidaSincronizadaDatos;
import co.edu.corhuila.rutasur.reservas.application.port.in.*;
import co.edu.corhuila.rutasur.reservas.application.port.out.*;
import co.edu.corhuila.rutasur.reservas.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.reservas.domain.model.Reserva;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.*;
import java.time.*;
import java.util.*;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reserva cupos mediante actualización condicional ACID para evitar sobreventa concurrente.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CrearReservaServicio implements CrearReservaUseCase {
    private final SalidaPuertoSalida salidas;
    private final ReservaPuertoSalida reservas;
    private final ProgramacionServicioPuertoSalida servicios;
    private final ProgramacionActividadPuertoSalida actividades;
    private final ParticipanteReservaPuertoSalida participantes;
    private final EventoOutboxPuertoSalida outbox;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Reserva crear(CrearReservaCommand c) {
        if (c.cantidadParticipantes() <= 0 || c.participantes() == null || c.participantes().size() != c.cantidadParticipantes())
            throw new ExcepcionReglaNegocio("reserva.participantes.invalidos");
        var serviciosSeleccionados = servicios.buscarActivas(c.salidaId(), c.programacionesServicio());
        var actividadesSeleccionadas = actividades.buscarActivas(c.salidaId(), c.programacionesActividad());
        if (serviciosSeleccionados.size() != c.programacionesServicio().size() || actividadesSeleccionadas.size() != c.programacionesActividad().size())
            throw new ExcepcionReglaNegocio("reserva.seleccion.invalida");
        if (!salidas.descontarCuposSiDisponibles(c.salidaId(), c.cantidadParticipantes()))
            throw new ExcepcionReglaNegocio("reserva.cupos.insuficientes");
        BigDecimal unitario = serviciosSeleccionados.stream().map(s -> s.precio()).reduce(BigDecimal.ZERO, BigDecimal::add).add(actividadesSeleccionadas.stream().map(a -> a.precio()).reduce(BigDecimal.ZERO, BigDecimal::add));
        String codigo = "RS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Reserva r = reservas.guardar(new Reserva(null, codigo, c.usuarioIdExterno(), c.salidaId(), c.cantidadParticipantes(), unitario.multiply(BigDecimal.valueOf(c.cantidadParticipantes())), "PENDIENTE", c.correlacionId(), OffsetDateTime.now()));
        participantes.guardarTodos(c.participantes().stream().map(p -> new co.edu.corhuila.rutasur.reservas.domain.model.ParticipanteReserva(null, r.id(), p.nombres(), p.apellidos(), p.tipoDocumento(), p.numeroDocumento())).toList());
        outbox.registrar("ReservaCreada", r.id(), c.correlacionId(), serializar(new ReservaCreadaDatos(r.id(), r.codigo(), r.salidaId(), r.usuarioIdExterno(), r.cantidadParticipantes(), r.total())));
        salidas.buscarPorId(c.salidaId()).ifPresent(salida -> outbox.registrar("SalidaActualizada", salida.id(), c.correlacionId(), serializar(new SalidaSincronizadaDatos(salida.id(), salida.experienciaIdExterna(), salida.fechaHoraInicio(), salida.cuposDisponibles(), salida.estado()))));
        log.info("correlationId={} servicio=reservas casoUso=crearReserva reservaId={} salidaId={}", c.correlacionId() == null ? UUID.randomUUID() : c.correlacionId(), r.id(), c.salidaId());
        return r;
    }

    private String serializar(Object datos) {
        try {
            return objectMapper.writeValueAsString(datos);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No fue posible serializar el evento de reserva.", e);
        }
    }
}
