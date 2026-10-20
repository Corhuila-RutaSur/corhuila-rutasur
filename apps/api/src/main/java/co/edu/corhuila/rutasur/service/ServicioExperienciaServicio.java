package co.edu.corhuila.rutasur.service;

import co.edu.corhuila.rutasur.dto.request.ServicioExperienciaRequest;
import co.edu.corhuila.rutasur.dto.response.ServicioExperienciaResponse;
import co.edu.corhuila.rutasur.entity.*;
import co.edu.corhuila.rutasur.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.repository.*;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gestiona los servicios configurables de una experiencia.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ServicioExperienciaServicio {
    private final ServicioExperienciaRepositorio repositorio;
    private final ExperienciaRepositorio experiencias;

    @Transactional
    public ServicioExperienciaResponse crear(Long experienciaId, ServicioExperienciaRequest solicitud) {
        validarExperiencia(experienciaId);
        if (repositorio.existsByExperienciaIdAndNombreIgnoreCase(experienciaId, solicitud.nombre().trim()))
            throw new ExcepcionReglaNegocio("servicio.experiencia.duplicado");
        ServicioExperiencia servicio = new ServicioExperiencia();
        servicio.setExperienciaId(experienciaId);
        aplicar(servicio, solicitud);
        ServicioExperiencia creado = repositorio.save(servicio);
        log.info("evento=servicio_experiencia_creado experienciaId={} servicioId={}", experienciaId, creado.getId());
        return respuesta(creado);
    }

    @Transactional(readOnly = true)
    public List<ServicioExperienciaResponse> listar(Long experienciaId) {
        validarExperiencia(experienciaId);
        return repositorio.findByExperienciaIdAndActivoTrueOrderByNombreAsc(experienciaId).stream().map(this::respuesta).toList();
    }

    @Transactional
    public ServicioExperienciaResponse actualizar(Long experienciaId, Long id, ServicioExperienciaRequest solicitud) {
        ServicioExperiencia servicio = obtener(experienciaId, id);
        aplicar(servicio, solicitud);
        return respuesta(servicio);
    }

    @Transactional
    public void inactivar(Long experienciaId, Long id) {
        ServicioExperiencia servicio = obtener(experienciaId, id);
        servicio.setActivo(false);
        log.info("evento=servicio_experiencia_inactivado experienciaId={} servicioId={}", experienciaId, id);
    }

    private void validarExperiencia(Long id) {
        if (experiencias.findByIdAndActivoTrue(id).isEmpty())
            throw new ExcepcionReglaNegocio("experiencia.no_encontrada");
    }

    private ServicioExperiencia obtener(Long experienciaId, Long id) {
        return repositorio.findByIdAndExperienciaIdAndActivoTrue(id, experienciaId).orElseThrow(() -> new ExcepcionReglaNegocio("servicio.experiencia.no_encontrado"));
    }

    private void aplicar(ServicioExperiencia servicio, ServicioExperienciaRequest s) {
        servicio.setTipo(s.tipo());
        servicio.setNombre(s.nombre().trim());
        servicio.setDescripcion(s.descripcion() == null ? null : s.descripcion().trim());
    }

    private ServicioExperienciaResponse respuesta(ServicioExperiencia s) {
        return new ServicioExperienciaResponse(s.getId(), s.getExperienciaId(), s.getTipo(), s.getNombre(), s.getDescripcion(), s.isActivo());
    }
}
