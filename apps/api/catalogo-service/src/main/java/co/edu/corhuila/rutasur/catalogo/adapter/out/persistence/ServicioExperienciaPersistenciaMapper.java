package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import co.edu.corhuila.rutasur.catalogo.domain.model.ServicioExperiencia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper entre el dominio y JPA para servicios de experiencia.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ServicioExperienciaPersistenciaMapper {
    ServicioExperiencia aDominio(ServicioExperienciaEntidad entidad);

    @Mapping(target = "id", expression = "java(servicio.id() == null ? java.util.UUID.randomUUID() : servicio.id())")
    ServicioExperienciaEntidad aEntidad(ServicioExperiencia servicio);
}
