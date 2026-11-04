package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import co.edu.corhuila.rutasur.catalogo.domain.model.TipoExperiencia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper aislado entre el modelo de dominio y la entidad JPA privada.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface TipoExperienciaPersistenciaMapper {
    TipoExperiencia aDominio(TipoExperienciaEntidad entidad);

    @Mapping(target = "id", expression = "java(tipo.id() == null ? java.util.UUID.randomUUID() : tipo.id())")
    TipoExperienciaEntidad aEntidad(TipoExperiencia tipo);
}
