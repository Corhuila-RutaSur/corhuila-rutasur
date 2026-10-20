package co.edu.corhuila.rutasur.mapper;

import co.edu.corhuila.rutasur.dto.request.ExperienciaActualizacionRequest;
import co.edu.corhuila.rutasur.dto.request.ExperienciaCreacionRequest;
import co.edu.corhuila.rutasur.dto.response.ExperienciaResponse;
import co.edu.corhuila.rutasur.entity.Experiencia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/** Convierte contratos HTTP y entidad de experiencia sin aplicar reglas de negocio. */
@Mapper(componentModel = "spring")
public interface ExperienciaMapper {
    @Mapping(target = "id", ignore = true) @Mapping(target = "activo", ignore = true)
    @Mapping(target = "creadoEn", ignore = true) @Mapping(target = "actualizadoEn", ignore = true)
    Experiencia aEntidad(ExperienciaCreacionRequest solicitud);
    @Mapping(target = "id", ignore = true) @Mapping(target = "creadoPorUsuarioId", ignore = true)
    @Mapping(target = "activo", ignore = true) @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    void actualizarEntidad(ExperienciaActualizacionRequest solicitud, @MappingTarget Experiencia experiencia);
    @Mapping(target = "imagenes", ignore = true)
    ExperienciaResponse aRespuesta(Experiencia experiencia);
}
