package co.edu.corhuila.rutasur.identidad.application.port.in;

import co.edu.corhuila.rutasur.identidad.domain.model.SesionIniciada;

/**
 * Caso de uso que valida credenciales y abre una sesión controlada.
 */
public interface IniciarSesionUseCase {

    /**
     * Inicia sesión y devuelve los tokens emitidos para el cliente autenticado.
     */
    SesionIniciada iniciar(IniciarSesionCommand comando);
}
