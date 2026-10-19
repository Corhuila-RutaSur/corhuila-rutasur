package co.edu.corhuila.rutasur.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Datos obligatorios para crear una experiencia de catálogo. */
public record ExperienciaCreacionRequest(
        @NotNull @Positive Long tipoExperienciaId,
        @NotNull @Positive Long creadoPorUsuarioId,
        @NotBlank @Size(max = 150) String nombre,
        @NotBlank String descripcion,
        @NotBlank @Size(max = 150) String destino,
        @Positive Integer duracionHoras,
        String recomendaciones) { }
