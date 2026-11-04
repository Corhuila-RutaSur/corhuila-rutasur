package co.edu.corhuila.rutasur.catalogo.application.usecase;

import co.edu.corhuila.rutasur.catalogo.application.port.in.CrearExperienciaCommand;
import co.edu.corhuila.rutasur.catalogo.application.port.in.CrearExperienciaUseCase;
import co.edu.corhuila.rutasur.catalogo.application.port.out.ExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.application.port.out.TipoExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.exception.ExcepcionReglaNegocio;
import co.edu.corhuila.rutasur.catalogo.domain.model.Experiencia;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Crea experiencias comerciales dentro del límite de Catálogo.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CrearExperienciaServicio implements CrearExperienciaUseCase {
    private final ExperienciaPuertoSalida experiencias;
    private final TipoExperienciaPuertoSalida tipos;
    private final PublicadorCambiosCatalogo eventos;

    @Override
    @Transactional
    public Experiencia crear(CrearExperienciaCommand comando) {
        if (comando.duracionHoras() <= 0) throw new ExcepcionReglaNegocio("experiencia.duracion.invalida");
        UUID correlacionId = comando.correlacionId() == null ? UUID.randomUUID() : comando.correlacionId();
        var tipo = tipos.buscarPorId(comando.tipoExperienciaId()).filter(t -> t.activo())
                .orElseThrow(() -> new ExcepcionReglaNegocio("tipo.experiencia.no.encontrado"));
        Experiencia creada = experiencias.guardar(new Experiencia(null, tipo.id(), tipo.nombre(), limpiar(comando.nombre()), limpiar(comando.destino()), limpiar(comando.descripcion()), comando.duracionHoras(), limpiarOpcional(comando.recomendaciones()), true, OffsetDateTime.now(), null));
        eventos.experiencia(creada, correlacionId);
        log.info("correlationId={} servicio=catalogo casoUso=crearExperiencia evento=exito experienciaId={}", correlacionId, creada.id());
        return creada;
    }

    private String limpiar(String valor) {
        return valor.trim();
    }

    private String limpiarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
