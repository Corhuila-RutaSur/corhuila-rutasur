package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Datos mínimos de una experiencia creada desde administración.
 */
public record ExperienciaCreacionRequest(
        @NotNull(message = "experiencia.tipo.obligatorio") java.util.UUID tipoExperienciaId,
        @NotBlank(message = "experiencia.nombre.obligatorio") @Size(max = 150, message = "experiencia.nombre.longitud") String nombre,
        @NotBlank(message = "experiencia.destino.obligatorio") @Size(max = 150, message = "experiencia.destino.longitud") String destino,
        @NotBlank(message = "experiencia.descripcion.obligatoria") String descripcion,
        @Min(value = 1, message = "experiencia.duracion.invalida") int duracionHoras,
        String recomendaciones) {
}
