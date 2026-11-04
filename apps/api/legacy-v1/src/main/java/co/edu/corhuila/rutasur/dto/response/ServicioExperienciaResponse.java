package co.edu.corhuila.rutasur.dto.response;

import co.edu.corhuila.rutasur.entity.TipoServicio;

public record ServicioExperienciaResponse(Long id, Long experienciaId, TipoServicio tipo, String nombre,
                                          String descripcion, boolean activo) {
}
