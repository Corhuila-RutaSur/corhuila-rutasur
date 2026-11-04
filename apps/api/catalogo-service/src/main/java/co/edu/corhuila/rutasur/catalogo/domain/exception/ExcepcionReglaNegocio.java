package co.edu.corhuila.rutasur.catalogo.domain.exception;

/**
 * Error funcional identificado por una clave internacionalizable.
 */
public class ExcepcionReglaNegocio extends RuntimeException {
    public ExcepcionReglaNegocio(String codigo) {
        super(codigo);
    }
}
