package co.edu.corhuila.rutasur.catalogo.application.port.in;

import co.edu.corhuila.rutasur.catalogo.domain.model.Experiencia;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de entrada para consultar, editar e inactivar experiencias.
 */
public interface GestionarExperienciaUseCase {
    Experiencia actualizar(UUID identificador, ActualizarExperienciaCommand comando);

    Experiencia consultarPorId(UUID identificador);

    List<Experiencia> consultarActivas();

    void inactivar(UUID identificador);
}
