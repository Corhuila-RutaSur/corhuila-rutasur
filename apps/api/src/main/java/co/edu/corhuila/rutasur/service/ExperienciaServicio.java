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
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Implementa el CRUD lógico del catálogo de experiencias. */
@Service
public class ExperienciaServicio {
    private final ExperienciaRepositorio experienciaRepositorio;
    private final TipoExperienciaRepositorio tipoExperienciaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ExperienciaMapper experienciaMapper;
    /** Construye el caso de uso de experiencias. */
    public ExperienciaServicio(ExperienciaRepositorio experienciaRepositorio, TipoExperienciaRepositorio tipoExperienciaRepositorio,
            UsuarioRepositorio usuarioRepositorio, ExperienciaMapper experienciaMapper) {
        this.experienciaRepositorio = experienciaRepositorio; this.tipoExperienciaRepositorio = tipoExperienciaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio; this.experienciaMapper = experienciaMapper;
    }
    /** Crea una experiencia para un usuario activo y un tipo turístico activo. */
    @Transactional public ExperienciaResponse crear(ExperienciaCreacionRequest solicitud) {
        validarTipo(solicitud.tipoExperienciaId()); validarAutor(solicitud.creadoPorUsuarioId());
        Experiencia experiencia = experienciaMapper.aEntidad(solicitud); normalizar(experiencia); experiencia.setActivo(true);
        return experienciaMapper.aRespuesta(experienciaRepositorio.save(experiencia));
    }
    /** Consulta una experiencia activa. */
    @Transactional(readOnly = true) public ExperienciaResponse consultarPorId(Long id) { return experienciaMapper.aRespuesta(obtenerActiva(id)); }
    /** Lista experiencias activas del catálogo. */
    @Transactional(readOnly = true) public List<ExperienciaResponse> consultarActivas() { return experienciaRepositorio.findByActivoTrueOrderByNombreAsc().stream().map(experienciaMapper::aRespuesta).toList(); }
    /** Actualiza una experiencia sin alterar su autor. */
    @Transactional public ExperienciaResponse actualizar(Long id, ExperienciaActualizacionRequest solicitud) {
        validarTipo(solicitud.tipoExperienciaId()); Experiencia experiencia = obtenerActiva(id);
        experienciaMapper.actualizarEntidad(solicitud, experiencia); normalizar(experiencia); return experienciaMapper.aRespuesta(experiencia);
    }
    /** Inactiva una experiencia sin borrar sus reservas históricas. */
    @Transactional public void eliminarLogicamente(Long id) { if (experienciaRepositorio.inactivarPorId(id, OffsetDateTime.now()) == 0) throw new ExcepcionReglaNegocio("experiencia.no_encontrada"); }
    /** Obtiene una experiencia activa. */
    private Experiencia obtenerActiva(Long id) { return experienciaRepositorio.findByIdAndActivoTrue(id).orElseThrow(() -> new ExcepcionReglaNegocio("experiencia.no_encontrada")); }
    /** Comprueba que el tipo pueda utilizarse en catálogo. */
    private void validarTipo(Long tipoId) { if (tipoExperienciaRepositorio.findByIdAndActivoTrue(tipoId).isEmpty()) throw new ExcepcionReglaNegocio("experiencia.tipo.no_encontrado"); }
    /** Comprueba que el autor de la experiencia esté activo. */
    private void validarAutor(Long usuarioId) { if (usuarioRepositorio.findByIdAndActivoTrue(usuarioId).isEmpty()) throw new ExcepcionReglaNegocio("usuario.no.encontrado"); }
    /** Elimina espacios accidentales en valores de texto. */
    private void normalizar(Experiencia experiencia) { experiencia.setNombre(experiencia.getNombre().trim()); experiencia.setDescripcion(experiencia.getDescripcion().trim()); experiencia.setDestino(experiencia.getDestino().trim()); if (experiencia.getRecomendaciones() != null) experiencia.setRecomendaciones(experiencia.getRecomendaciones().trim()); }
}
