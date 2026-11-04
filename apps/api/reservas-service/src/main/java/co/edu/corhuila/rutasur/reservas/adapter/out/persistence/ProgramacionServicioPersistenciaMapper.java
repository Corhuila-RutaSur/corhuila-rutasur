package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import co.edu.corhuila.rutasur.reservas.domain.model.ProgramacionServicio;
import org.mapstruct.*;

/**
 * Mapper de servicios programados entre dominio y JPA.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface ProgramacionServicioPersistenciaMapper {
    ProgramacionServicio aDominio(ProgramacionServicioEntidad entidad);

    @Mapping(target = "id", expression = "java(valor.id()==null ? java.util.UUID.randomUUID() : valor.id())")
    ProgramacionServicioEntidad aEntidad(ProgramacionServicio valor);
}
