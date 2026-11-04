package co.edu.corhuila.rutasur.dto.response;

import java.math.BigDecimal;

public record ProgramacionServicioResponse(Long id, Long salidaExperienciaId, Long servicioExperienciaId,
                                           BigDecimal precioUnitario, Integer cuposTotales, Integer cuposDisponibles,
                                           boolean activo) {
}
