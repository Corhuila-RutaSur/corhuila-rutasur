package co.edu.corhuila.rutasur.identidad.domain.exception;

/**
 * Error de autenticación expresado por una clave internacionalizable y segura.
 */
public class ExcepcionAutenticacion extends RuntimeException {

    /**
     * Crea un error que no revela internamente la causa criptográfica del rechazo.
     */
    public ExcepcionAutenticacion(String codigo) {
        super(codigo);
    }
}
