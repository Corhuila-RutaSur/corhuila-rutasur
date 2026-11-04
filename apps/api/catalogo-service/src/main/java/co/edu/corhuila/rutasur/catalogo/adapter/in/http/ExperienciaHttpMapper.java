package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import co.edu.corhuila.rutasur.catalogo.application.port.in.CrearExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.in.ActualizarExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.domain.model.Experiencia;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapea contratos HTTP hacia y desde el caso de uso de experiencias.
 */
@Mapper(componentModel = "spring")
public interface ExperienciaHttpMapper {
    @Mapping(target = "correlacionId", source = "correlacionId")
    CrearExperienciaCommand aComando(ExperienciaCreacionRequest solicitud, UUID correlacionId);

    ActualizarExperienciaCommand aComandoActualizar(ExperienciaCreacionRequest solicitud, UUID correlacionId);

    ExperienciaResponse aRespuesta(Experiencia experiencia);
}
