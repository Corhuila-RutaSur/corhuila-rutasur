package co.edu.corhuila.rutasur.catalogo.application.port.out;

import co.edu.corhuila.rutasur.catalogo.domain.model.Experiencia;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia del agregado Experiencia.
 */
public interface ExperienciaPuertoSalida {
    Experiencia guardar(Experiencia experiencia);

    Optional<Experiencia> buscarPorId(UUID identificador);

    /** Obtiene y bloquea la experiencia durante un cambio concurrente de su galería. */
    Optional<Experiencia> bloquearPorId(UUID identificador);

    List<Experiencia> consultarActivas();
}
