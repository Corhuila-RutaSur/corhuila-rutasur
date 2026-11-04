package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import co.edu.corhuila.rutasur.catalogo.application.port.in.ServicioExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.domain.model.ServicioExperiencia;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper del adaptador HTTP para servicios de experiencia.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServicioExperienciaHttpMapper {
    ServicioExperienciaCommand aComando(ServicioExperienciaRequest solicitud, UUID correlacionId);

    ServicioExperienciaResponse aRespuesta(ServicioExperiencia servicio);
}
