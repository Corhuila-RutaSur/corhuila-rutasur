package co.edu.corhuila.rutasur.dto.request;

import co.edu.corhuila.rutasur.entity.TipoServicio;
import jakarta.validation.constraints.*;

public record ServicioExperienciaRequest(@NotNull TipoServicio tipo, @NotBlank @Size(max = 150) String nombre,
                                         String descripcion) {
}
