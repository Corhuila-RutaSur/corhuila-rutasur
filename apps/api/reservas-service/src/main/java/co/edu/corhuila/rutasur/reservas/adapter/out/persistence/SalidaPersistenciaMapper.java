package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import co.edu.corhuila.rutasur.reservas.domain.model.Salida;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper entre el modelo de salida y la entidad JPA privada.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface SalidaPersistenciaMapper {
    Salida aDominio(SalidaEntidad entidad);

    @Mapping(target = "id", expression = "java(salida.id() == null ? java.util.UUID.randomUUID() : salida.id())")
    SalidaEntidad aEntidad(Salida salida);
}
