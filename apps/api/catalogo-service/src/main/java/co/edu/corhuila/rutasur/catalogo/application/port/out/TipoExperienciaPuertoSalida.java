package co.edu.corhuila.rutasur.catalogo.application.port.out;

import co.edu.corhuila.rutasur.catalogo.domain.model.TipoExperiencia;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida que aísla la persistencia de tipos de experiencia.
 */
public interface TipoExperienciaPuertoSalida {
    boolean existeActivoPorNombre(String nombre);

    TipoExperiencia guardar(TipoExperiencia tipo);

    Optional<TipoExperiencia> buscarPorId(UUID identificador);

    List<TipoExperiencia> consultarActivos();
}
