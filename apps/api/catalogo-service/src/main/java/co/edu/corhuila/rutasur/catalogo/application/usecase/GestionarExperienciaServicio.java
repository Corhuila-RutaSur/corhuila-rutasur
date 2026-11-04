package co.edu.corhuila.rutasur.catalogo.application.usecase;

import co.edu.corhuila.rutasur.catalogo.application.port.in.ActualizarExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.in.GestionarExperienciaUseCase;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.application.port.out.TipoExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.catalogo.domain.model.Experiencia;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Casos de uso administrativos de consulta, edición e inactivación de experiencias.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GestionarExperienciaServicio implements GestionarExperienciaUseCase {
    private final ExperienciaPuertoSalida experiencias;
    private final TipoExperienciaPuertoSalida tipos;
    private final PublicadorCambiosCatalogo eventos;

    @Override
    @Transactional
    public Experiencia actualizar(UUID identificador, ActualizarExperienciaCommand comando) {
        if (comando.duracionHoras() <= 0) throw new ExcepcionReglaNegocio("experiencia.duracion.invalida");
        Experiencia actual = obtenerActiva(identificador);
        var tipo = tipos.buscarPorId(comando.tipoExperienciaId()).filter(t -> t.activo())
                .orElseThrow(() -> new ExcepcionReglaNegocio("tipo.experiencia.no.encontrado"));
        Experiencia actualizada = experiencias.guardar(new Experiencia(actual.id(), tipo.id(), tipo.nombre(), limpiar(comando.nombre()), limpiar(comando.destino()), limpiar(comando.descripcion()), comando.duracionHoras(), limpiarOpcional(comando.recomendaciones()), true, actual.creadoEn(), OffsetDateTime.now()));
        eventos.experiencia(actualizada, comando.correlacionId());
        registrar(comando.correlacionId(), "actualizar", identificador);
        return actualizada;
    }

    @Override
    @Transactional(readOnly = true)
    public Experiencia consultarPorId(UUID identificador) {
        return obtenerActiva(identificador);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Experiencia> consultarActivas() {
        return experiencias.consultarActivas();
    }

    @Override
    @Transactional
    public void inactivar(UUID identificador) {
        Experiencia actual = obtenerActiva(identificador);
        Experiencia inactiva = experiencias.guardar(new Experiencia(actual.id(), actual.tipoExperienciaId(), actual.tipo(), actual.nombre(), actual.destino(), actual.descripcion(), actual.duracionHoras(), actual.recomendaciones(), false, actual.creadoEn(), OffsetDateTime.now()));
        eventos.experiencia(inactiva, null);
        registrar(null, "inactivar", identificador);
    }

    private Experiencia obtenerActiva(UUID identificador) {
        return experiencias.buscarPorId(identificador).filter(Experiencia::activa).orElseThrow(() -> new ExcepcionReglaNegocio("experiencia.no.encontrada"));
    }

    private String limpiar(String valor) {
        return valor.trim();
    }

    private String limpiarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private void registrar(UUID correlacionId, String operacion, UUID identificador) {
        log.info("correlationId={} servicio=catalogo casoUso=experiencia operacion={} experienciaId={}", correlacionId == null ? UUID.randomUUID() : correlacionId, operacion, identificador);
    }
}
