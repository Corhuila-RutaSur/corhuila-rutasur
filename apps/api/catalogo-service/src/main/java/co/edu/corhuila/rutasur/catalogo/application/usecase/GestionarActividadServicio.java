package co.edu.corhuila.rutasur.catalogo.application.usecase;

import co.edu.corhuila.rutasur.catalogo.application.port.in.ActividadCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.in.GestionarActividadUseCase;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ActividadPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.catalogo.domain.model.Actividad;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reglas de negocio para actividades propias de una experiencia.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GestionarActividadServicio implements GestionarActividadUseCase {
    private final ExperienciaPuertoSalida experiencias;
    private final ActividadPuertoSalida actividades;
    private final PublicadorCambiosCatalogo eventos;

    @Override
    @Transactional
    public Actividad crear(UUID experienciaId, ActividadCommand comando) {
        validarDuracion(comando.duracionHoras());
        validarExperiencia(experienciaId);
        String nombre = limpiar(comando.nombre());
        if (actividades.existeActivaPorExperienciaYNombre(experienciaId, nombre))
            throw new ExcepcionReglaNegocio("actividad.nombre.duplicado");
        Actividad creada = actividades.guardar(new Actividad(null, experienciaId, nombre, limpiar(comando.descripcion()), comando.duracionHoras(), true, OffsetDateTime.now(), null));
        eventos.actividad(creada, comando.correlacionId());
        registrar(comando.correlacionId(), "crear", experienciaId, creada.id());
        return creada;
    }

    @Override
    @Transactional
    public Actividad actualizar(UUID experienciaId, UUID actividadId, ActividadCommand comando) {
        validarDuracion(comando.duracionHoras());
        validarExperiencia(experienciaId);
        Actividad actual = obtenerActiva(experienciaId, actividadId);
        String nombre = limpiar(comando.nombre());
        if (!actual.nombre().equalsIgnoreCase(nombre) && actividades.existeActivaPorExperienciaYNombre(experienciaId, nombre))
            throw new ExcepcionReglaNegocio("actividad.nombre.duplicado");
        Actividad actualizada = actividades.guardar(new Actividad(actual.id(), experienciaId, nombre, limpiar(comando.descripcion()), comando.duracionHoras(), true, actual.creadoEn(), OffsetDateTime.now()));
        eventos.actividad(actualizada, comando.correlacionId());
        registrar(comando.correlacionId(), "actualizar", experienciaId, actividadId);
        return actualizada;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Actividad> consultarActivas(UUID experienciaId) {
        validarExperiencia(experienciaId);
        return actividades.consultarActivasPorExperiencia(experienciaId);
    }

    @Override
    @Transactional
    public void inactivar(UUID experienciaId, UUID actividadId) {
        validarExperiencia(experienciaId);
        Actividad actual = obtenerActiva(experienciaId, actividadId);
        Actividad inactiva = actividades.guardar(new Actividad(actual.id(), actual.experienciaId(), actual.nombre(), actual.descripcion(), actual.duracionHoras(), false, actual.creadoEn(), OffsetDateTime.now()));
        eventos.actividad(inactiva, null);
        registrar(null, "inactivar", experienciaId, actividadId);
    }

    private void validarExperiencia(UUID id) {
        if (experiencias.buscarPorId(id).filter(e -> e.activa()).isEmpty())
            throw new ExcepcionReglaNegocio("experiencia.no.encontrada");
    }

    private void validarDuracion(int valor) {
        if (valor <= 0) throw new ExcepcionReglaNegocio("actividad.duracion.invalida");
    }

    private Actividad obtenerActiva(UUID experienciaId, UUID actividadId) {
        return actividades.buscarPorId(actividadId).filter(a -> a.activa() && a.experienciaId().equals(experienciaId)).orElseThrow(() -> new ExcepcionReglaNegocio("actividad.no.encontrada"));
    }

    private String limpiar(String valor) {
        return valor.trim();
    }

    private void registrar(UUID correlacionId, String operacion, UUID experienciaId, UUID actividadId) {
        log.info("correlationId={} servicio=catalogo casoUso=actividad operacion={} experienciaId={} actividadId={}", correlacionId == null ? UUID.randomUUID() : correlacionId, operacion, experienciaId, actividadId);
    }
}
