package co.edu.corhuila.rutasur.mapper;

import co.edu.corhuila.rutasur.dto.response.ReservaResponse;
import co.edu.corhuila.rutasur.entity.Reserva;
import org.mapstruct.Mapper;

/**
 * Convierte la cabecera de reserva a su contrato de salida.
 */
@Mapper(componentModel = "spring")
public interface ReservaMapper {
    ReservaResponse aRespuesta(Reserva reserva);
}
