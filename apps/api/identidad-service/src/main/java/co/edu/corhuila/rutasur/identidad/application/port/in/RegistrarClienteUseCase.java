package co.edu.corhuila.rutasur.identidad.application.port.in;

import co.edu.corhuila.rutasur.identidad.domain.model.Usuario;

/**
 * Caso de uso para el registro público de una cuenta cliente.
 */
public interface RegistrarClienteUseCase {
    Usuario registrar(RegistrarClienteCommand comando);
}
