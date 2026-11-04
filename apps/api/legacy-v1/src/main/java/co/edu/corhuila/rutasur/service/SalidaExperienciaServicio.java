package co.edu.corhuila.rutasur.service;

import co.edu.corhuila.rutasur.dto.request.SalidaExperienciaRequest;
import co.edu.corhuila.rutasur.dto.response.SalidaExperienciaResponse;
import co.edu.corhuila.rutasur.entity.*;
import co.edu.corhuila.rutasur.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.repository.*;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalidaExperienciaServicio {
    private final SalidaExperienciaRepositorio salidas;
    private final ExperienciaRepositorio experiencias;

    @Transactional
    public SalidaExperienciaResponse crear(Long experienciaId, SalidaExperienciaRequest r) {
        validarExperiencia(experienciaId);
        SalidaExperiencia salida = new SalidaExperiencia();
        salida.setExperienciaId(experienciaId);
        aplicar(salida, r, true);
        return respuesta(salidas.save(salida));
    }

    @Transactional(readOnly = true)
    public List<SalidaExperienciaResponse> listarPublicas(Long experienciaId) {
        return salidas.findByExperienciaIdAndEstadoOrderByFechaHoraInicioAsc(experienciaId, EstadoSalida.ABIERTA).stream().map(this::respuesta).toList();
    }

    @Transactional(readOnly = true)
    public List<SalidaExperienciaResponse> listar(Long experienciaId) {
        validarExperiencia(experienciaId);
        return salidas.findByExperienciaIdOrderByFechaHoraInicioAsc(experienciaId).stream().map(this::respuesta).toList();
    }

    @Transactional
    public SalidaExperienciaResponse actualizar(Long experienciaId, Long id, SalidaExperienciaRequest r) {
        SalidaExperiencia salida = obtener(experienciaId, id);
        aplicar(salida, r, false);
        return respuesta(salida);
    }

    @Transactional
    public void inactivar(Long experienciaId, Long id) {
        SalidaExperiencia salida = obtener(experienciaId, id);
        salida.setEstado(EstadoSalida.CANCELADA);
    }

    @Transactional(readOnly = true)
    public SalidaExperienciaResponse consultarPublica(Long id) {
        return respuesta(salidas.findById(id).orElseThrow(() -> new ExcepcionReglaNegocio("salida.no.encontrada")));
    }

    private void validarExperiencia(Long id) {
        if (experiencias.findByIdAndActivoTrue(id).isEmpty())
            throw new ExcepcionReglaNegocio("experiencia.no_encontrada");
    }

    private SalidaExperiencia obtener(Long e, Long id) {
        return salidas.findByIdAndExperienciaId(id, e).orElseThrow(() -> new ExcepcionReglaNegocio("salida.no.encontrada"));
    }

    private void aplicar(SalidaExperiencia s, SalidaExperienciaRequest r, boolean nueva) {
        if (r.fechaHoraFin() != null && !r.fechaHoraFin().isAfter(r.fechaHoraInicio()))
            throw new ExcepcionReglaNegocio("salida.fechas.invalidas");
        s.setFechaHoraInicio(r.fechaHoraInicio());
        s.setFechaHoraFin(r.fechaHoraFin());
        if (nueva) {
            s.setCuposTotales(r.cuposTotales());
            s.setCuposDisponibles(r.cuposTotales());
        } else if (r.cuposTotales() < s.getCuposTotales() - s.getCuposDisponibles())
            throw new ExcepcionReglaNegocio("salida.cupos.invalidos");
        else {
            s.setCuposDisponibles(s.getCuposDisponibles() + r.cuposTotales() - s.getCuposTotales());
            s.setCuposTotales(r.cuposTotales());
        }
        s.setEstado(r.estado());
        s.setObservaciones(r.observaciones());
    }

    private SalidaExperienciaResponse respuesta(SalidaExperiencia s) {
        return new SalidaExperienciaResponse(s.getId(), s.getExperienciaId(), s.getFechaHoraInicio(), s.getFechaHoraFin(), s.getCuposTotales(), s.getCuposDisponibles(), s.getEstado(), s.getObservaciones());
    }
}
