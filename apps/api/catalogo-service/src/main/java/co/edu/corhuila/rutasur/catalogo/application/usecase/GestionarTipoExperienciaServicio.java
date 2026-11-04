package co.edu.corhuila.rutasur.catalogo.application.usecase;

import co.edu.corhuila.rutasur.catalogo.application.port.in.ActualizarTipoExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.in.CrearTipoExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.in.GestionarTipoExperienciaUseCase;
import co.edu.corhuila.rutasur.catalogo.application.port.out.TipoExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.catalogo.domain.model.TipoExperiencia;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementa las reglas para crear, editar, consultar e inactivar tipos turísticos.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GestionarTipoExperienciaServicio implements GestionarTipoExperienciaUseCase {
    private final TipoExperienciaPuertoSalida tipos;

    @Override
    @Transactional
    public TipoExperiencia crear(CrearTipoExperienciaCommand comando) {
        String nombre = normalizarNombre(comando.nombre());
        if (tipos.existeActivoPorNombre(nombre)) throw new ExcepcionReglaNegocio("tipo.experiencia.nombre.duplicado");
        TipoExperiencia creado = tipos.guardar(new TipoExperiencia(null, nombre, limpiarOpcional(comando.descripcion()), true, OffsetDateTime.now(), null));
        registrar(comando.correlacionId(), "crear", creado.id());
        return creado;
    }

    @Override
    @Transactional
    public TipoExperiencia actualizar(UUID identificador, ActualizarTipoExperienciaCommand comando) {
        TipoExperiencia actual = obtenerVigente(identificador);
        String nombre = normalizarNombre(comando.nombre());
        if (!actual.nombre().equals(nombre) && tipos.existeActivoPorNombre(nombre))
            throw new ExcepcionReglaNegocio("tipo.experiencia.nombre.duplicado");
        TipoExperiencia actualizado = tipos.guardar(new TipoExperiencia(actual.id(), nombre, limpiarOpcional(comando.descripcion()), true, actual.creadoEn(), OffsetDateTime.now()));
        registrar(comando.correlacionId(), "actualizar", actualizado.id());
        return actualizado;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoExperiencia> consultarActivos() {
        return tipos.consultarActivos();
    }

    @Override
    @Transactional
    public void inactivar(UUID identificador) {
        TipoExperiencia actual = obtenerVigente(identificador);
        tipos.guardar(new TipoExperiencia(actual.id(), actual.nombre(), actual.descripcion(), false, actual.creadoEn(), OffsetDateTime.now()));
        registrar(null, "inactivar", identificador);
    }

    private TipoExperiencia obtenerVigente(UUID identificador) {
        return tipos.buscarPorId(identificador).filter(TipoExperiencia::activo)
                .orElseThrow(() -> new ExcepcionReglaNegocio("tipo.experiencia.no.encontrado"));
    }

    private String normalizarNombre(String valor) {
        return valor.trim().toUpperCase(java.util.Locale.ROOT);
    }

    private String limpiarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private void registrar(UUID correlacionId, String operacion, UUID identificador) {
        log.info("correlationId={} servicio=catalogo casoUso=tipoExperiencia operacion={} tipoId={}", correlacionId == null ? UUID.randomUUID() : correlacionId, operacion, identificador);
    }
}
