package co.edu.corhuila.rutasur.identidad.adapter.out.persistence;

import co.edu.corhuila.rutasur.identidad.domain.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Traduce el agregado de identidad en el borde de persistencia.
 */
@Mapper(componentModel = "spring")
interface UsuarioPersistenciaMapper {
    Usuario aDominio(UsuarioEntidad entidad);

    @Mapping(target = "id", expression = "java(usuario.id() == null ? java.util.UUID.randomUUID() : usuario.id())")
    UsuarioEntidad aEntidad(Usuario usuario);
}
