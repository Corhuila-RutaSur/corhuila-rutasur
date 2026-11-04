package co.edu.corhuila.rutasur.catalogo.adapter.in.http;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Solicitud HTTP de un servicio que se ofrece dentro de una experiencia.
 */
public record ServicioExperienciaRequest(
        @NotBlank(message = "servicio.experiencia.tipo.obligatorio") @Size(max = 50, message = "servicio.experiencia.tipo.longitud") String tipo,
        @NotBlank(message = "servicio.experiencia.nombre.obligatorio") @Size(max = 150, message = "servicio.experiencia.nombre.longitud") String nombre,
        @Size(max = 500, message = "servicio.experiencia.descripcion.longitud") String descripcion) {
}
