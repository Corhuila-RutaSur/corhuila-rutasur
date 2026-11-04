package co.edu.corhuila.rutasur.exception;

/**
 * Representa una regla de negocio incumplida mediante una clave de mensaje estable.
 */
public class ExcepcionReglaNegocio extends RuntimeException {

    private final String codigo;

    /**
     * Crea una excepcion de negocio.
     *
     * @param codigo clave de internacionalizacion que identifica el error.
     */
    public ExcepcionReglaNegocio(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Obtiene la clave funcional del error.
     *
     * @return codigo estable de error.
     */
    public String getCodigo() {
        return codigo;
    }
}
