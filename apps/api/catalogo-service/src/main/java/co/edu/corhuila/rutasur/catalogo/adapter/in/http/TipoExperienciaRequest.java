package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Solicitud HTTP para crear o editar un tipo de experiencia.
 */
public record TipoExperienciaRequest(
        @NotBlank(message = "tipo.experiencia.nombre.obligatorio")
        @Size(max = 80, message = "tipo.experiencia.nombre.longitud") String nombre,
        @Size(max = 500, message = "tipo.experiencia.descripcion.longitud") String descripcion) {
}
