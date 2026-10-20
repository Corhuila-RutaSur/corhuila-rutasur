package co.edu.corhuila.rutasur.repository;

import co.edu.corhuila.rutasur.entity.Usuario;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Gestiona la persistencia y las consultas de cuentas de usuario.
 */
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByIdAndActivoTrue(Long id);

    Optional<Usuario> findByCorreoIgnoreCaseAndActivoTrue(String correo);

    List<UsuarioConsultaProyeccion> findByActivoTrueOrderByNombresAscApellidosAsc();

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumentoAndIdNot(String numeroDocumento, Long id);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Long id);

    boolean existsByTelefono(String telefono);

    boolean existsByTelefonoAndIdNot(String telefono, Long id);

    /**
     * Desactiva una cuenta sin eliminar su trazabilidad ni sus relaciones historicas.
     *
     * @param id                 identificador del usuario.
     * @param fechaActualizacion instante en que se realiza la inactivacion.
     * @return cantidad de filas modificadas.
     */
    @Modifying
    @Query("update Usuario usuario set usuario.activo = false, usuario.actualizadoEn = :fechaActualizacion "
            + "where usuario.id = :id and usuario.activo = true")
    int desactivarPorId(@Param("id") Long id, @Param("fechaActualizacion") OffsetDateTime fechaActualizacion);
}
