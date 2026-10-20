package co.edu.corhuila.rutasur.dto.response;
import co.edu.corhuila.rutasur.entity.EstadoSalida; import java.time.OffsetDateTime;
public record SalidaExperienciaResponse(Long id,Long experienciaId,OffsetDateTime fechaHoraInicio,OffsetDateTime fechaHoraFin,Integer cuposTotales,Integer cuposDisponibles,EstadoSalida estado,String observaciones){}
