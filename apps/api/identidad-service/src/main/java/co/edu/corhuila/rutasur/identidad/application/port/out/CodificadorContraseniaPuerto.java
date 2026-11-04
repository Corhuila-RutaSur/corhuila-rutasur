package co.edu.corhuila.rutasur.identidad.application.port.out;

/**
 * Puerto que evita acoplar el dominio a la librería de cifrado.
 */
public interface CodificadorContraseniaPuerto {
    String codificar(String contrasenia);
}
