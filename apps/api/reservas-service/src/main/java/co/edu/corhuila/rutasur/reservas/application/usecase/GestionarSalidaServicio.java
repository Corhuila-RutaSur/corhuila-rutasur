package co.edu.corhuila.rutasur.reservas.application.usecase;

import co.edu.corhuila.rutasur.reservas.application.port.in.CrearSalidaCommand;
import co.edu.corhuila.rutasur.reservas.application.port.in.GestionarSalidaUseCase;
import co.edu.corhuila.rutasur.reservas.application.port.out.SalidaPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.EventoOutboxPuertoSalida;
import co.edu.corhuila.rutasur.reservas.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.reservas.domain.model.Salida;
import co.edu.corhuila.rutasur.contratos.evento.SalidaSincronizadaDatos;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Crea salidas y establece su disponibilidad inicial bajo una transacción local.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GestionarSalidaServicio implements GestionarSalidaUseCase {
    private final SalidaPuertoSalida salidas;
    private final EventoOutboxPuertoSalida outbox;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Salida crear(CrearSalidaCommand comando) {
        if (comando.cuposTotales() <= 0) throw new ExcepcionReglaNegocio("salida.cupos.invalidos");
        if (!comando.fechaHoraInicio().isAfter(OffsetDateTime.now()))
            throw new ExcepcionReglaNegocio("salida.fecha.invalida");
        Salida creada = salidas.guardar(new Salida(null, comando.experienciaIdExterna(), comando.experienciaNombre().trim(), comando.fechaHoraInicio(), comando.cuposTotales(), comando.cuposTotales(), "PROGRAMADA", OffsetDateTime.now()));
        try {
            outbox.registrar("SalidaActualizada", creada.id(), comando.correlacionId(), objectMapper.writeValueAsString(
                    new SalidaSincronizadaDatos(creada.id(), creada.experienciaIdExterna(), creada.fechaHoraInicio(), creada.cuposDisponibles(), creada.estado())));
        } catch (Exception error) {
            throw new IllegalStateException("No fue posible serializar la salida para Consultas.", error);
        }
        log.info("correlationId={} servicio=reservas casoUso=crearSalida evento=exito salidaId={}", comando.correlacionId() == null ? UUID.randomUUID() : comando.correlacionId(), creada.id());
        return creada;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Salida> consultarDisponibles() {
        return salidas.consultarDisponibles();
    }
}
