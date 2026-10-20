package co.edu.corhuila.rutasur.dto.response;

/**
 * Imagen visible dentro de la galería pública de una experiencia.
 */
public record ExperienciaImagenResponse(Long id, String url, String descripcion, Integer orden, boolean principal) {
}
