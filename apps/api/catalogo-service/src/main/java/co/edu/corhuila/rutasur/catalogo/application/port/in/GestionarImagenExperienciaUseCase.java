package co.edu.corhuila.rutasur.catalogo.application.port.in;

import co.edu.corhuila.rutasur.catalogo.domain.model.ImagenExperiencia;
import java.util.List;
import java.util.UUID;

/** Casos de uso para administrar la galería de una experiencia. */
public interface GestionarImagenExperienciaUseCase {
    ImagenExperiencia cargar(UUID experienciaId, ArchivoImagenCommand archivo, boolean principal, int orden, UUID correlacionId);
    List<ImagenExperiencia> listar(UUID experienciaId);
    ImagenExperiencia actualizar(UUID experienciaId, UUID imagenId, ActualizarImagenExperienciaCommand comando);
    void inactivar(UUID experienciaId, UUID imagenId, UUID correlacionId);
}
