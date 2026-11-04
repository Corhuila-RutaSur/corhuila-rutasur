package co.edu.corhuila.rutasur.service;

import co.edu.corhuila.rutasur.dto.request.ProgramacionServicioRequest;
import co.edu.corhuila.rutasur.dto.response.ProgramacionServicioResponse;
import co.edu.corhuila.rutasur.entity.ProgramacionServicio;
import co.edu.corhuila.rutasur.entity.SalidaExperiencia;
import co.edu.corhuila.rutasur.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.repository.ProgramacionServicioRepositorio;
import co.edu.corhuila.rutasur.repository.SalidaExperienciaRepositorio;
import co.edu.corhuila.rutasur.repository.ServicioExperienciaRepositorio;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gestiona los servicios concretos que una salida pone a disposición de sus reservas.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProgramacionServicioServicio {

    private final ProgramacionServicioRepositorio programacionRepositorio;
    private final SalidaExperienciaRepositorio salidaRepositorio;
    private final ServicioExperienciaRepositorio servicioRepositorio;

    /**
     * Crea el precio y la disponibilidad de un servicio para una salida específica.
     */
    @Transactional
    public ProgramacionServicioResponse crear(Long salidaId, ProgramacionServicioRequest solicitud) {
        log.info("evento=programacion_servicio_creacion_inicio salidaId={} servicioId={}",
                salidaId, solicitud.servicioExperienciaId());
        SalidaExperiencia salida = obtenerSalida(salidaId);
        validarServicioPerteneceExperiencia(solicitud.servicioExperienciaId(), salida.getExperienciaId());
        validarNoDuplicada(salidaId, solicitud.servicioExperienciaId());

        ProgramacionServicio programacion = new ProgramacionServicio();
        programacion.setSalidaExperienciaId(salidaId);
        aplicarSolicitud(programacion, solicitud);
        ProgramacionServicio creada = programacionRepositorio.save(programacion);

        log.info("evento=programacion_servicio_creacion_exitosa salidaId={} programacionId={}", salidaId, creada.getId());
        return aRespuesta(creada);
    }

    /**
     * Lista las programaciones que siguen vigentes para una salida.
     */
    @Transactional(readOnly = true)
    public List<ProgramacionServicioResponse> listar(Long salidaId) {
        log.info("evento=programaciones_consulta_inicio salidaId={}", salidaId);
        return programacionRepositorio.findBySalidaExperienciaIdAndActivoTrue(salidaId).stream()
                .map(this::aRespuesta)
                .toList();
    }

    /**
     * Actualiza precio o cupos de una programación vigente.
     */
    @Transactional
    public ProgramacionServicioResponse actualizar(Long salidaId, Long id, ProgramacionServicioRequest solicitud) {
        log.info("evento=programacion_servicio_actualizacion_inicio salidaId={} programacionId={}", salidaId, id);
        ProgramacionServicio programacion = obtenerProgramacionActiva(salidaId, id);
        aplicarSolicitud(programacion, solicitud);
        log.info("evento=programacion_servicio_actualizacion_exitosa salidaId={} programacionId={}", salidaId, id);
        return aRespuesta(programacion);
    }

    /**
     * Inactiva una programación sin eliminar las reservas históricas asociadas.
     */
    @Transactional
    public void inactivar(Long salidaId, Long id) {
        log.info("evento=programacion_servicio_inactivacion_inicio salidaId={} programacionId={}", salidaId, id);
        obtenerProgramacionActiva(salidaId, id).setActivo(false);
        log.info("evento=programacion_servicio_inactivacion_exitosa salidaId={} programacionId={}", salidaId, id);
    }

    /**
     * Obtiene la salida requerida para la programación.
     */
    private SalidaExperiencia obtenerSalida(Long salidaId) {
        return salidaRepositorio.findById(salidaId)
                .orElseThrow(() -> new ExcepcionReglaNegocio("salida.no.encontrada"));
    }

    /**
     * Verifica que un servicio activo pertenezca a la misma experiencia de la salida.
     */
    private void validarServicioPerteneceExperiencia(Long servicioId, Long experienciaId) {
        servicioRepositorio.findByIdAndExperienciaIdAndActivoTrue(servicioId, experienciaId)
                .orElseThrow(() -> new ExcepcionReglaNegocio("servicio.experiencia.no_encontrado"));
    }

    /**
     * Evita programar dos veces la misma opción comercial en una salida.
     */
    private void validarNoDuplicada(Long salidaId, Long servicioId) {
        if (programacionRepositorio.existsBySalidaExperienciaIdAndServicioExperienciaId(salidaId, servicioId)) {
            throw new ExcepcionReglaNegocio("programacion.duplicada");
        }
    }

    /**
     * Busca una programación activa bajo la salida indicada.
     */
    private ProgramacionServicio obtenerProgramacionActiva(Long salidaId, Long id) {
        return programacionRepositorio.findByIdAndSalidaExperienciaIdAndActivoTrue(id, salidaId)
                .orElseThrow(() -> new ExcepcionReglaNegocio("programacion.no.encontrada"));
    }

    /**
     * Copia el contrato de programación a la entidad y sincroniza cupos iniciales.
     */
    private void aplicarSolicitud(ProgramacionServicio programacion, ProgramacionServicioRequest solicitud) {
        programacion.setServicioExperienciaId(solicitud.servicioExperienciaId());
        programacion.setPrecioUnitario(solicitud.precioUnitario());
        programacion.setCuposTotales(solicitud.cuposTotales());
        programacion.setCuposDisponibles(solicitud.cuposTotales());
    }

    /**
     * Convierte la entidad persistida al contrato seguro que se expone a la API.
     */
    private ProgramacionServicioResponse aRespuesta(ProgramacionServicio programacion) {
        return new ProgramacionServicioResponse(programacion.getId(), programacion.getSalidaExperienciaId(),
                programacion.getServicioExperienciaId(), programacion.getPrecioUnitario(),
                programacion.getCuposTotales(), programacion.getCuposDisponibles(), programacion.isActivo());
    }
}
