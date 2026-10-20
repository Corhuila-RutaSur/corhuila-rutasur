package co.edu.corhuila.rutasur.service;

import co.edu.corhuila.rutasur.dto.request.ExperienciaActualizacionRequest;
import co.edu.corhuila.rutasur.dto.request.ExperienciaCreacionRequest;
import co.edu.corhuila.rutasur.dto.response.ExperienciaResponse;
import co.edu.corhuila.rutasur.entity.Experiencia;
import co.edu.corhuila.rutasur.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.mapper.ExperienciaMapper;
import co.edu.corhuila.rutasur.repository.ExperienciaRepositorio;
import co.edu.corhuila.rutasur.repository.TipoExperienciaRepositorio;
import co.edu.corhuila.rutasur.repository.UsuarioRepositorio;
import co.edu.corhuila.rutasur.repository.ExperienciaImagenRepositorio;
import co.edu.corhuila.rutasur.entity.ExperienciaImagen;
import co.edu.corhuila.rutasur.dto.request.ExperienciaImagenRequest;
import co.edu.corhuila.rutasur.dto.response.ExperienciaImagenResponse;
import org.springframework.web.multipart.MultipartFile;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Implementa el CRUD lógico del catálogo de experiencias. */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperienciaServicio {
    private final ExperienciaRepositorio experienciaRepositorio;
    private final TipoExperienciaRepositorio tipoExperienciaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ExperienciaMapper experienciaMapper;
    private final ExperienciaImagenRepositorio experienciaImagenRepositorio;
    private final AlmacenamientoImagenServicio almacenamientoImagenServicio;
    /** Crea una experiencia para un usuario activo y un tipo turístico activo. */
    @Transactional public ExperienciaResponse crear(ExperienciaCreacionRequest solicitud) {
        log.info("evento=experiencia_creacion_inicio tipoId={} creadorId={}", solicitud.tipoExperienciaId(), solicitud.creadoPorUsuarioId());
        validarTipo(solicitud.tipoExperienciaId()); validarAutor(solicitud.creadoPorUsuarioId());
        Experiencia experiencia = experienciaMapper.aEntidad(solicitud); normalizar(experiencia); experiencia.setActivo(true);
        Experiencia creada = experienciaRepositorio.save(experiencia); guardarImagenes(creada.getId(), solicitud.imagenes()); log.info("evento=experiencia_creacion_exitosa experienciaId={}", creada.getId()); return respuestaConImagenes(creada);
    }
    /** Consulta una experiencia activa. */
    @Transactional(readOnly = true) public ExperienciaResponse consultarPorId(Long id) { log.info("evento=experiencia_consulta_inicio experienciaId={}", id); return respuestaConImagenes(obtenerActiva(id)); }
    /** Lista experiencias activas del catálogo. */
    @Transactional(readOnly = true) public List<ExperienciaResponse> consultarActivas() { log.info("evento=experiencias_consulta_activas_inicio"); List<ExperienciaResponse> experiencias=experienciaRepositorio.findByActivoTrueOrderByNombreAsc().stream().map(this::respuestaConImagenes).toList(); log.info("evento=experiencias_consulta_activas_exitosa cantidad={}", experiencias.size()); return experiencias; }
    /** Actualiza una experiencia sin alterar su autor. */
    @Transactional public ExperienciaResponse actualizar(Long id, ExperienciaActualizacionRequest solicitud) {
        log.info("evento=experiencia_actualizacion_inicio experienciaId={}", id);
        validarTipo(solicitud.tipoExperienciaId()); Experiencia experiencia = obtenerActiva(id);
        experienciaMapper.actualizarEntidad(solicitud, experiencia); normalizar(experiencia); reemplazarImagenes(id, solicitud.imagenes()); log.info("evento=experiencia_actualizacion_exitosa experienciaId={}", id); return respuestaConImagenes(experiencia);
    }
    /** Inactiva una experiencia sin borrar sus reservas históricas. */
    @Transactional public void eliminarLogicamente(Long id) { log.info("evento=experiencia_inactivacion_inicio experienciaId={}", id); if (experienciaRepositorio.inactivarPorId(id, OffsetDateTime.now()) == 0) throw new ExcepcionReglaNegocio("experiencia.no_encontrada"); log.info("evento=experiencia_inactivacion_exitosa experienciaId={}", id); }
    /** Adjunta una imagen física a una experiencia activa. */
    @Transactional public ExperienciaImagenResponse adjuntarImagen(Long id, MultipartFile archivo, boolean principal, int orden) { obtenerActiva(id); ExperienciaImagen imagen=almacenamientoImagenServicio.guardar(id,archivo,principal,orden); log.info("evento=experiencia_imagen_adjuntada experienciaId={} imagenId={}",id,imagen.getId()); return new ExperienciaImagenResponse(imagen.getId(),imagen.getUrl(),imagen.getDescripcion(),imagen.getOrden(),imagen.isPrincipal()); }
    /** Obtiene una experiencia activa. */
    private Experiencia obtenerActiva(Long id) { return experienciaRepositorio.findByIdAndActivoTrue(id).orElseThrow(() -> new ExcepcionReglaNegocio("experiencia.no_encontrada")); }
    /** Comprueba que el tipo pueda utilizarse en catálogo. */
    private void validarTipo(Long tipoId) { if (tipoExperienciaRepositorio.findByIdAndActivoTrue(tipoId).isEmpty()) throw new ExcepcionReglaNegocio("experiencia.tipo.no_encontrado"); }
    /** Comprueba que el autor de la experiencia esté activo. */
    private void validarAutor(Long usuarioId) { if (usuarioRepositorio.findByIdAndActivoTrue(usuarioId).isEmpty()) throw new ExcepcionReglaNegocio("usuario.no.encontrado"); }
    /** Elimina espacios accidentales en valores de texto. */
    private void normalizar(Experiencia experiencia) { experiencia.setNombre(experiencia.getNombre().trim()); experiencia.setDescripcion(experiencia.getDescripcion().trim()); experiencia.setDestino(experiencia.getDestino().trim()); if (experiencia.getRecomendaciones() != null) experiencia.setRecomendaciones(experiencia.getRecomendaciones().trim()); }
    private void reemplazarImagenes(Long experienciaId, List<ExperienciaImagenRequest> imagenes) { experienciaImagenRepositorio.deleteByExperienciaId(experienciaId); guardarImagenes(experienciaId, imagenes); }
    private void guardarImagenes(Long experienciaId, List<ExperienciaImagenRequest> imagenes) { if (imagenes == null) return; imagenes.forEach(solicitud -> { ExperienciaImagen imagen=new ExperienciaImagen(); imagen.setExperienciaId(experienciaId); imagen.setUrl(solicitud.url().trim()); imagen.setDescripcion(solicitud.descripcion()); imagen.setOrden(solicitud.orden()); imagen.setPrincipal(solicitud.principal()); experienciaImagenRepositorio.save(imagen); }); }
    private ExperienciaResponse respuestaConImagenes(Experiencia experiencia) { ExperienciaResponse base=experienciaMapper.aRespuesta(experiencia); List<ExperienciaImagenResponse> imagenes=experienciaImagenRepositorio.findByExperienciaIdOrderByPrincipalDescOrdenAsc(experiencia.getId()).stream().map(imagen -> new ExperienciaImagenResponse(imagen.getId(),imagen.getUrl(),imagen.getDescripcion(),imagen.getOrden(),imagen.isPrincipal())).toList(); return new ExperienciaResponse(base.id(),base.tipoExperienciaId(),base.creadoPorUsuarioId(),base.nombre(),base.descripcion(),base.destino(),base.duracionHoras(),base.recomendaciones(),base.activo(),base.creadoEn(),base.actualizadoEn(),imagenes); }
}
