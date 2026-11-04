package co.edu.corhuila.rutasur.catalogo.application.port.out;

import co.edu.corhuila.rutasur.catalogo.domain.model.ImagenExperiencia;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Puerto de persistencia de la galería de experiencias. */
public interface ImagenExperienciaPuertoSalida {
    ImagenExperiencia guardar(ImagenExperiencia imagen);
    Optional<ImagenExperiencia> buscarPorId(UUID imagenId);
    List<ImagenExperiencia> listarPorExperiencia(UUID experienciaId);
    List<ImagenExperiencia> listarActivasPorExperiencia(UUID experienciaId);
    void quitarPortadaActiva(UUID experienciaId);
}
