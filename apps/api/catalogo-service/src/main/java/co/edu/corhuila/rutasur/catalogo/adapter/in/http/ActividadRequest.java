package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Solicitud de creación o edición de una actividad turística.
 */
public record ActividadRequest(
        @NotBlank(message = "actividad.nombre.obligatorio") @Size(max = 150, message = "actividad.nombre.longitud") String nombre,
        @NotBlank(message = "actividad.descripcion.obligatoria") @Size(max = 1000, message = "actividad.descripcion.longitud") String descripcion,
        @Min(value = 1, message = "actividad.duracion.invalida") int duracionHoras) {
}
