package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import co.edu.corhuila.rutasur.catalogo.domain.model.Experiencia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Traduce el agregado de Catálogo en el borde de persistencia.
 */
@Mapper(componentModel = "spring")
interface ExperienciaPersistenciaMapper {
    Experiencia aDominio(ExperienciaEntidad entidad);

    @Mapping(target = "id", expression = "java(experiencia.id() == null ? java.util.UUID.randomUUID() : experiencia.id())")
    ExperienciaEntidad aEntidad(Experiencia experiencia);
}
