package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import co.edu.corhuila.rutasur.catalogo.application.port.in.ActividadCommand;
import co.edu.corhuila.rutasur.catalogo.domain.model.Actividad;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper HTTP de actividades turísticas.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ActividadHttpMapper {
    ActividadCommand aComando(ActividadRequest solicitud, UUID correlacionId);

    ActividadResponse aRespuesta(Actividad actividad);
}
