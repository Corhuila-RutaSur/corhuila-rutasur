package co.edu.corhuila.rutasur.repository;

import co.edu.corhuila.rutasur.entity.UsuarioTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Persiste las asignaciones de roles de las cuentas.
 */
public interface UsuarioTipoRepositorio extends JpaRepository<UsuarioTipo, Long> {
    @Query("select asignacion.tipoUsuario.nombre from UsuarioTipo asignacion where asignacion.usuario.id = :usuarioId and asignacion.tipoUsuario.activo = true")
    List<String> buscarRolesActivosPorUsuarioId(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);
}
