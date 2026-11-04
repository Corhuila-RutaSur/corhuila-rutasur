package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import co.edu.corhuila.rutasur.catalogo.application.port.in.ActualizarTipoExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.in.CrearTipoExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.domain.model.TipoExperiencia;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper del adaptador HTTP de tipos turísticos.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TipoExperienciaHttpMapper {
    CrearTipoExperienciaCommand aComandoCrear(TipoExperienciaRequest solicitud, UUID correlacionId);

    ActualizarTipoExperienciaCommand aComandoActualizar(TipoExperienciaRequest solicitud, UUID correlacionId);

    TipoExperienciaResponse aRespuesta(TipoExperiencia tipo);
}
