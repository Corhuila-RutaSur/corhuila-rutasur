package co.edu.corhuila.rutasur.catalogo.application.port.in;

import co.edu.corhuila.rutasur.catalogo.domain.model.TipoExperiencia;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de entrada para administrar el catálogo de tipos de experiencia.
 */
public interface GestionarTipoExperienciaUseCase {
    TipoExperiencia crear(CrearTipoExperienciaCommand comando);

    TipoExperiencia actualizar(UUID identificador, ActualizarTipoExperienciaCommand comando);

    List<TipoExperiencia> consultarActivos();

    void inactivar(UUID identificador);
}
