package co.edu.corhuila.rutasur.consultas.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador de lectura CQRS: compone el catálogo exclusivamente desde proyecciones locales.
 */
@Component
@RequiredArgsConstructor
public class ConsultaCatalogoPublicoAdaptador {
    private final ExperienciaProyeccionRepositorio experiencias;
    private final ServicioProyeccionRepositorio servicios;
    private final ActividadProyeccionRepositorio actividades;
    private final SalidaProyeccionRepositorio salidas;
    private final ImagenExperienciaProyeccionRepositorio imagenes;
    private final ProgramacionServicioProyeccionRepositorio serviciosProgramados;
    private final ProgramacionActividadProyeccionRepositorio actividadesProgramadas;

    public List<ExperienciaResumen> listar() {
        return experiencias.findAllByActivaTrueOrderByNombreAsc().stream().map(this::resumen).toList();
    }

    public ExperienciaDetalle consultar(UUID id) {
        ExperienciaProyeccionEntidad experiencia = experiencias.findById(id).filter(ExperienciaProyeccionEntidad::isActiva).orElseThrow(() -> new ExperienciaNoEncontradaException(id));
        return new ExperienciaDetalle(experiencia.getId(), experiencia.getNombre(), experiencia.getDestino(), experiencia.getDescripcion(), portada(experiencia), experiencia.getCalificacionPromedio(),
                imagenes.findAllByExperienciaIdAndActivaTrueOrderByOrdenAsc(id).stream().map(imagen -> new Imagen(imagen.getId(), imagen.getRuta(), imagen.isPrincipal(), imagen.getOrden())).toList(),
                servicios.findAllByExperienciaIdAndActivoTrue(id).stream().map(s -> new Servicio(s.getId(), s.getTipo(), s.getNombre(), s.getDescripcion())).toList(),
                actividades.findAllByExperienciaIdAndActivaTrue(id).stream().map(a -> new Actividad(a.getId(), a.getNombre(), a.getDescripcion(), a.getDuracionHoras())).toList(),
                salidas.findAllByExperienciaIdAndEstadoAndFechaHoraInicioAfterOrderByFechaHoraInicioAsc(id, "PROGRAMADA", OffsetDateTime.now()).stream().filter(s -> s.getCuposDisponibles() > 0).map(s -> new Salida(s.getId(), s.getFechaHoraInicio(), s.getCuposDisponibles(), s.getEstado())).toList());
    }

    /** Devuelve la disponibilidad que el usuario puede seleccionar antes de crear una reserva. */
    public DisponibilidadSalida consultarDisponibilidad(UUID salidaId) {
        SalidaProyeccionEntidad salida = salidas.findById(salidaId)
                .filter(item -> "PROGRAMADA".equals(item.getEstado()) && item.getCuposDisponibles() > 0)
                .orElseThrow(() -> new SalidaNoDisponibleException(salidaId));
        return new DisponibilidadSalida(salida.getId(), salida.getExperienciaId(), salida.getFechaHoraInicio(), salida.getCuposDisponibles(), salida.getEstado(),
                serviciosProgramados.findAllBySalidaIdAndActivoTrueOrderByNombreAsc(salidaId).stream().filter(item -> item.getCuposDisponibles() > 0).map(item -> new OpcionServicio(item.getId(), item.getServicioIdExterno(), item.getNombre(), item.getPrecio(), item.getCuposDisponibles(), item.isActivo())).toList(),
                actividadesProgramadas.findAllBySalidaIdAndActivoTrueOrderByNombreAsc(salidaId).stream().map(item -> new OpcionActividad(item.getId(), item.getActividadIdExterna(), item.getNombre(), item.getPrecio(), item.isActivo())).toList());
    }

    private ExperienciaResumen resumen(ExperienciaProyeccionEntidad e) {
        return new ExperienciaResumen(e.getId(), e.getNombre(), e.getDestino(), e.getDescripcion(), portada(e), e.getCalificacionPromedio());
    }

    private String portada(ExperienciaProyeccionEntidad experiencia) {
        return imagenes.findFirstByExperienciaIdAndActivaTrueAndPrincipalTrue(experiencia.getId()).map(ImagenExperienciaProyeccionEntidad::getRuta).orElse(experiencia.getImagenPrincipal());
    }

    public record ExperienciaResumen(UUID id, String nombre, String destino, String descripcion, String imagenPrincipal,
                                     BigDecimal calificacionPromedio) {
    }

    public record ExperienciaDetalle(UUID id, String nombre, String destino, String descripcion, String imagenPrincipal,
                                     BigDecimal calificacionPromedio, List<Imagen> imagenes, List<Servicio> servicios,
                                     List<Actividad> actividades, List<Salida> salidas) {
    }

    public record Imagen(UUID id, String ruta, boolean principal, int orden) {
    }

    public record Servicio(UUID id, String tipo, String nombre, String descripcion) {
    }

    public record Actividad(UUID id, String nombre, String descripcion, int duracionHoras) {
    }

    public record Salida(UUID id, OffsetDateTime fechaHoraInicio, int cuposDisponibles, String estado) {
    }

    public record DisponibilidadSalida(UUID salidaId, UUID experienciaId, OffsetDateTime fechaHoraInicio, int cuposDisponibles,
                                      String estado, List<OpcionServicio> servicios, List<OpcionActividad> actividades) {
    }
    public record OpcionServicio(UUID programacionId, UUID servicioIdExterno, String nombre, BigDecimal precio, int cuposDisponibles, boolean activo) { }
    public record OpcionActividad(UUID programacionId, UUID actividadIdExterna, String nombre, BigDecimal precio, boolean activo) { }

    public static class ExperienciaNoEncontradaException extends RuntimeException {
        public ExperienciaNoEncontradaException(UUID id) {
            super("No existe una experiencia pública con id " + id);
        }
    }

    public static class SalidaNoDisponibleException extends RuntimeException {
        public SalidaNoDisponibleException(UUID id) { super("No existe una salida pública disponible con id " + id); }
    }
}
