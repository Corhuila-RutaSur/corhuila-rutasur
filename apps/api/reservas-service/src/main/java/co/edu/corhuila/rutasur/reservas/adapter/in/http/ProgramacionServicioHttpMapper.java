package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import co.edu.corhuila.rutasur.reservas.application.port.in.ProgramarServicioCommand;
import co.edu.corhuila.rutasur.reservas.domain.model.ProgramacionServicio;

import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Mapper HTTP de servicios programados.
 */
@Component
public class ProgramacionServicioHttpMapper {
    /** Conserva explícitamente el identificador de correlación del comando HTTP. */
    public ProgramarServicioCommand aComando(ProgramacionServicioRequest solicitud, UUID correlacionId) {
        return new ProgramarServicioCommand(solicitud.servicioIdExterno(), solicitud.nombre(), solicitud.precio(),
                solicitud.cuposTotales(), correlacionId);
    }

    public ProgramacionServicioResponse aRespuesta(ProgramacionServicio programacion) {
        return new ProgramacionServicioResponse(programacion.id(), programacion.salidaId(),
                programacion.servicioIdExterno(), programacion.nombre(), programacion.precio(),
                programacion.cuposTotales(), programacion.cuposDisponibles(), programacion.activo());
    }
}
