package co.edu.corhuila.rutasur.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Datos editables de una experiencia existente; el autor permanece inmutable.
 */
public record ExperienciaActualizacionRequest(
        @NotNull @Positive Long tipoExperienciaId,
        @NotBlank @Size(max = 150) String nombre,
        @NotBlank String descripcion,
        @NotBlank @Size(max = 150) String destino,
        @Positive Integer duracionHoras,
        String recomendaciones, @Valid List<ExperienciaImagenRequest> imagenes) {
}
