package co.edu.corhuila.rutasur.identidad.domain.exception;

/**
 * Error funcional expresado mediante una clave internacionalizable.
 */
public class ExcepcionReglaNegocio extends RuntimeException {
    public ExcepcionReglaNegocio(String codigo) {
        super(codigo);
    }
}
