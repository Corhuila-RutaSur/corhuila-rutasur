package co.edu.corhuila.rutasur.catalogo.application.usecase;

import co.edu.corhuila.rutasur.catalogo.application.port.in.GestionarServicioExperienciaUseCase;
import co.edu.corhuila.rutasur.catalogo.application.port.in.ServicioExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ServicioExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.catalogo.domain.model.ServicioExperiencia;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reglas de administración de servicios ofrecidos por cada experiencia.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GestionarServicioExperienciaServicio implements GestionarServicioExperienciaUseCase {
    private final ExperienciaPuertoSalida experiencias;
    private final ServicioExperienciaPuertoSalida servicios;
    private final PublicadorCambiosCatalogo eventos;

    @Override
    @Transactional
    public ServicioExperiencia crear(UUID experienciaId, ServicioExperienciaCommand comando) {
        validarExperiencia(experienciaId);
        String nombre = limpiar(comando.nombre());
        if (servicios.existeActivoPorExperienciaYNombre(experienciaId, nombre))
            throw new ExcepcionReglaNegocio("servicio.experiencia.nombre.duplicado");
        ServicioExperiencia creado = servicios.guardar(new ServicioExperiencia(null, experienciaId, limpiar(comando.tipo()), nombre, limpiarOpcional(comando.descripcion()), true, OffsetDateTime.now(), null));
        eventos.servicio(creado, comando.correlacionId());
        registrar(comando.correlacionId(), "crear", experienciaId, creado.id());
        return creado;
    }

    @Override
    @Transactional
    public ServicioExperiencia actualizar(UUID experienciaId, UUID servicioId, ServicioExperienciaCommand comando) {
        validarExperiencia(experienciaId);
        ServicioExperiencia actual = obtenerActivo(experienciaId, servicioId);
        String nombre = limpiar(comando.nombre());
        if (!actual.nombre().equalsIgnoreCase(nombre) && servicios.existeActivoPorExperienciaYNombre(experienciaId, nombre))
            throw new ExcepcionReglaNegocio("servicio.experiencia.nombre.duplicado");
        ServicioExperiencia actualizado = servicios.guardar(new ServicioExperiencia(actual.id(), experienciaId, limpiar(comando.tipo()), nombre, limpiarOpcional(comando.descripcion()), true, actual.creadoEn(), OffsetDateTime.now()));
        eventos.servicio(actualizado, comando.correlacionId());
        registrar(comando.correlacionId(), "actualizar", experienciaId, servicioId);
        return actualizado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioExperiencia> consultarActivos(UUID experienciaId) {
        validarExperiencia(experienciaId);
        return servicios.consultarActivosPorExperiencia(experienciaId);
    }

    @Override
    @Transactional
    public void inactivar(UUID experienciaId, UUID servicioId) {
        validarExperiencia(experienciaId);
        ServicioExperiencia actual = obtenerActivo(experienciaId, servicioId);
        ServicioExperiencia inactivo = servicios.guardar(new ServicioExperiencia(actual.id(), actual.experienciaId(), actual.tipo(), actual.nombre(), actual.descripcion(), false, actual.creadoEn(), OffsetDateTime.now()));
        eventos.servicio(inactivo, null);
        registrar(null, "inactivar", experienciaId, servicioId);
    }

    private void validarExperiencia(UUID identificador) {
        if (experiencias.buscarPorId(identificador).filter(e -> e.activa()).isEmpty())
            throw new ExcepcionReglaNegocio("experiencia.no.encontrada");
    }

    private ServicioExperiencia obtenerActivo(UUID experienciaId, UUID servicioId) {
        return servicios.buscarPorId(servicioId).filter(s -> s.activo() && s.experienciaId().equals(experienciaId)).orElseThrow(() -> new ExcepcionReglaNegocio("servicio.experiencia.no.encontrado"));
    }

    private String limpiar(String valor) {
        return valor.trim();
    }

    private String limpiarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private void registrar(UUID correlacionId, String operacion, UUID experienciaId, UUID servicioId) {
        log.info("correlationId={} servicio=catalogo casoUso=servicioExperiencia operacion={} experienciaId={} servicioId={}", correlacionId == null ? UUID.randomUUID() : correlacionId, operacion, experienciaId, servicioId);
    }
}
