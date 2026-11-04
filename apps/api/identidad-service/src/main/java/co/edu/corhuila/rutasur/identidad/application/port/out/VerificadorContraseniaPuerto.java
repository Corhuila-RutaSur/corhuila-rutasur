package co.edu.corhuila.rutasur.identidad.application.port.out;

/**
 * Puerto para comparar una contraseña recibida contra su hash persistido.
 */
public interface VerificadorContraseniaPuerto {

    /**
     * Indica si la contraseña en texto plano corresponde al hash BCrypt.
     */
    boolean coincide(String contrasenia, String hash);
}
