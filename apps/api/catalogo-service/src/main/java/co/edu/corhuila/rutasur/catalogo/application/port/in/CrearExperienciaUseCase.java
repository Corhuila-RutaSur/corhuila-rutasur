package co.edu.corhuila.rutasur.catalogo.application.port.in;

import co.edu.corhuila.rutasur.catalogo.domain.model.Experiencia;

/**
 * Caso de uso administrativo de creación de experiencia.
 */
public interface CrearExperienciaUseCase {
    Experiencia crear(CrearExperienciaCommand comando);
}
