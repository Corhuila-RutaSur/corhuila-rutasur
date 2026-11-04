package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import co.edu.corhuila.rutasur.catalogo.application.port.out.ImagenExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.model.ImagenExperiencia;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Adaptador JPA de la galería de cada experiencia. */
@Component
@RequiredArgsConstructor
public class ImagenExperienciaPersistenciaAdaptador implements ImagenExperienciaPuertoSalida {
    private final ImagenExperienciaRepositorioJpa repositorio;
    @Override public ImagenExperiencia guardar(ImagenExperiencia imagen) {
        ImagenExperienciaEntidad entidad = new ImagenExperienciaEntidad();
        entidad.setId(imagen.id()); entidad.setExperienciaId(imagen.experienciaId()); entidad.setRuta(imagen.ruta());
        entidad.setPrincipal(imagen.principal()); entidad.setOrden(imagen.orden()); entidad.setActiva(imagen.activa());
        entidad.setCreadoEn(imagen.creadoEn()); entidad.setActualizadoEn(imagen.actualizadoEn());
        return aDominio(repositorio.save(entidad));
    }
    @Override public Optional<ImagenExperiencia> buscarPorId(UUID imagenId) { return repositorio.findById(imagenId).map(this::aDominio); }
    @Override public List<ImagenExperiencia> listarPorExperiencia(UUID experienciaId) { return repositorio.findAllByExperienciaIdOrderByActivaDescOrdenAscCreadoEnAsc(experienciaId).stream().map(this::aDominio).toList(); }
    @Override public List<ImagenExperiencia> listarActivasPorExperiencia(UUID experienciaId) { return repositorio.findAllByExperienciaIdAndActivaTrueOrderByOrdenAscCreadoEnAsc(experienciaId).stream().map(this::aDominio).toList(); }
    @Override public void quitarPortadaActiva(UUID experienciaId) { repositorio.quitarPortadaActiva(experienciaId, OffsetDateTime.now()); }
    private ImagenExperiencia aDominio(ImagenExperienciaEntidad e) { return new ImagenExperiencia(e.getId(), e.getExperienciaId(), e.getRuta(), e.isPrincipal(), e.getOrden(), e.isActiva(), e.getCreadoEn(), e.getActualizadoEn()); }
}
