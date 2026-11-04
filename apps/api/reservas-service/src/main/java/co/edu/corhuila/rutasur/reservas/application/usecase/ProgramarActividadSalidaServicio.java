package co.edu.corhuila.rutasur.reservas.application.usecase;

import co.edu.corhuila.rutasur.reservas.application.port.in.ProgramarActividadCommand;
import co.edu.corhuila.rutasur.reservas.application.port.out.*;
import co.edu.corhuila.rutasur.reservas.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.reservas.domain.model.ProgramacionActividad;

import java.math.*;
import java.util.*;
import co.edu.corhuila.rutasur.contratos.evento.ProgramacionActividadSincronizadaDatos;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Habilita actividades y fija su precio para una salida programada.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProgramarActividadSalidaServicio {
    private final SalidaPuertoSalida salidas;
    private final ProgramacionActividadPuertoSalida programaciones;
    private final EventoOutboxPuertoSalida outbox;
    private final ObjectMapper objectMapper;

    @Transactional
    public ProgramacionActividad ejecutar(UUID salidaId, ProgramarActividadCommand c) {
        if (c.precio() == null || c.precio().compareTo(BigDecimal.ZERO) < 0)
            throw new ExcepcionReglaNegocio("programacion.precio.invalido");
        if (salidas.buscarPorId(salidaId).filter(s -> "PROGRAMADA".equals(s.estado())).isEmpty())
            throw new ExcepcionReglaNegocio("salida.no.disponible");
        ProgramacionActividad creada = programaciones.guardar(new ProgramacionActividad(null, salidaId, c.actividadIdExterna(), c.nombre().trim(), c.precio(), true));
        publicar(creada, c.correlacionId());
        log.info("correlationId={} servicio=reservas casoUso=programarActividad salidaId={} programacionId={}", c.correlacionId() == null ? UUID.randomUUID() : c.correlacionId(), salidaId, creada.id());
        return creada;
    }

    private void publicar(ProgramacionActividad valor, UUID correlacionId) {
        try { outbox.registrar("ProgramacionActividadActualizada", valor.id(), correlacionId, objectMapper.writeValueAsString(new ProgramacionActividadSincronizadaDatos(valor.id(), valor.salidaId(), valor.actividadIdExterna(), valor.nombre(), valor.precio(), valor.activo()))); }
        catch (Exception error) { throw new IllegalStateException("No fue posible serializar la programación de actividad.", error); }
    }
}
