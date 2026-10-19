package co.edu.corhuila.rutasur.repository;

import java.time.OffsetDateTime;

/**
 * Vista ligera de los datos visibles de un usuario para consultas de listado.
 */
public interface UsuarioConsultaProyeccion {

    Long getId();

    String getTipoDocumento();

    String getNumeroDocumento();

    String getNombres();

    String getApellidos();

    String getCorreo();

    String getTelefono();

    boolean isActivo();

    OffsetDateTime getCreadoEn();

    OffsetDateTime getActualizadoEn();
}
