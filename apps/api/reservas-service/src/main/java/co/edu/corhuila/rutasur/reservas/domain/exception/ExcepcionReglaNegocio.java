package co.edu.corhuila.rutasur.reservas.domain.exception;

/**
 * Regla funcional expresada mediante una clave internacionalizable.
 */
public class ExcepcionReglaNegocio extends RuntimeException {
    public ExcepcionReglaNegocio(String codigo) {
        super(codigo);
    }
}
