package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import co.edu.corhuila.rutasur.catalogo.domain.model.Actividad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper aislado de actividad entre JPA y el dominio.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ActividadPersistenciaMapper {
    Actividad aDominio(ActividadEntidad entidad);

    @Mapping(target = "id", expression = "java(actividad.id() == null ? java.util.UUID.randomUUID() : actividad.id())")
    ActividadEntidad aEntidad(Actividad actividad);
}
