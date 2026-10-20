package co.edu.corhuila.rutasur.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Imagen URL que se asociará a una experiencia.
 */
public record ExperienciaImagenRequest(@NotBlank @Size(max = 500) String url, @Size(max = 255) String descripcion,
                                       @NotNull @Positive Integer orden, boolean principal) {
}
