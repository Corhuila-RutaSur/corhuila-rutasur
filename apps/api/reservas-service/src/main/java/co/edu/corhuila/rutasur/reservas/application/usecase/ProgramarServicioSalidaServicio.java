package co.edu.corhuila.rutasur.reservas.application.usecase;

import co.edu.corhuila.rutasur.reservas.application.port.in.ProgramarServicioCommand;
import co.edu.corhuila.rutasur.reservas.application.port.out.ProgramacionServicioPuertoSalida;
import co.edu.corhuila.rutasur.reservas.application.port.out.SalidaPuertoSalida;
import co.edu.corhuila.rutasur.reservas.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.reservas.domain.model.ProgramacionServicio;

import java.math.BigDecimal;
import java.util.UUID;
import co.edu.corhuila.rutasur.reservas.application.port.out.EventoOutboxPuertoSalida;
import co.edu.corhuila.rutasur.contratos.evento.ProgramacionServicioSincronizadaDatos;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Habilita servicios y fija su precio en una salida programada.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProgramarServicioSalidaServicio {
    private final SalidaPuertoSalida salidas;
    private final ProgramacionServicioPuertoSalida programaciones;
    private final EventoOutboxPuertoSalida outbox;
    private final ObjectMapper objectMapper;

    @Transactional
    public ProgramacionServicio ejecutar(UUID salidaId, ProgramarServicioCommand c) {
        if (c.precio() == null || c.precio().compareTo(BigDecimal.ZERO) < 0)
            throw new ExcepcionReglaNegocio("programacion.precio.invalido");
        var salida = salidas.buscarPorId(salidaId).filter(s -> "PROGRAMADA".equals(s.estado()))
                .orElseThrow(() -> new ExcepcionReglaNegocio("salida.no.disponible"));
        if (c.cuposTotales() <= 0 || c.cuposTotales() > salida.cuposTotales())
            throw new ExcepcionReglaNegocio("programacion.cupos.invalidos");
        ProgramacionServicio creada = programaciones.guardar(new ProgramacionServicio(null, salidaId, c.servicioIdExterno(), c.nombre().trim(), c.precio(), c.cuposTotales(), c.cuposTotales(), true));
        UUID correlacionId = c.correlacionId() == null ? UUID.randomUUID() : c.correlacionId();
        publicar(creada, correlacionId);
        log.info("correlationId={} servicio=reservas casoUso=programarServicio salidaId={} programacionId={}", correlacionId, salidaId, creada.id());
        return creada;
    }

    private void publicar(ProgramacionServicio valor, UUID correlacionId) {
        try { outbox.registrar("ProgramacionServicioActualizada", valor.id(), correlacionId, objectMapper.writeValueAsString(new ProgramacionServicioSincronizadaDatos(valor.id(), valor.salidaId(), valor.servicioIdExterno(), valor.nombre(), valor.precio(), valor.cuposDisponibles(), valor.activo()))); }
        catch (Exception error) { throw new IllegalStateException("No fue posible serializar la programación de servicio.", error); }
    }
}
