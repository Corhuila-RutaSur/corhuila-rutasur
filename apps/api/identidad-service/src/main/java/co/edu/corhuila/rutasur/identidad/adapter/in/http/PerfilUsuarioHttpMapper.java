package co.edu.corhuila.rutasur.identidad.adapter.in.http;

import co.edu.corhuila.rutasur.identidad.domain.model.PerfilUsuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapea la proyección de dominio al contrato HTTP sin exponer datos sensibles.
 */
@Mapper(componentModel = "spring")
public interface PerfilUsuarioHttpMapper {

    /**
     * Convierte los roles de dominio en sus nombres autorizados para el cliente.
     */
    @Mapping(target = "roles", expression = "java(perfil.roles().stream().map(Enum::name).toList())")
    PerfilUsuarioResponse aRespuesta(PerfilUsuario perfil);
}
