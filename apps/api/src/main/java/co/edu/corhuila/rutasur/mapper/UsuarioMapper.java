package co.edu.corhuila.rutasur.mapper;

import co.edu.corhuila.rutasur.dto.request.UsuarioActualizacionRequest;
import co.edu.corhuila.rutasur.dto.request.UsuarioRegistroRequest;
import co.edu.corhuila.rutasur.dto.response.UsuarioResponse;
import co.edu.corhuila.rutasur.entity.Usuario;
import co.edu.corhuila.rutasur.repository.UsuarioConsultaProyeccion;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Centraliza las conversiones sin logica de negocio entre contratos y persistencia de usuarios.
 */
@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    Usuario aEntidad(UsuarioRegistroRequest solicitud);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    void actualizarEntidad(UsuarioActualizacionRequest solicitud, @MappingTarget Usuario usuario);

    UsuarioResponse aRespuesta(Usuario usuario);

    UsuarioResponse aRespuesta(UsuarioConsultaProyeccion usuario);
}
