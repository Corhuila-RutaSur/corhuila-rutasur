package co.edu.corhuila.rutasur.reservas.adapter.in.http;

import co.edu.corhuila.rutasur.reservas.application.port.in.CrearSalidaCommand;
import co.edu.corhuila.rutasur.reservas.domain.model.Salida;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper entre los contratos HTTP y el modelo de salida.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SalidaHttpMapper {
    CrearSalidaCommand aComando(SalidaRequest solicitud, UUID correlacionId);

    SalidaResponse aRespuesta(Salida salida);
}
