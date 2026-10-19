package co.edu.corhuila.rutasur.repository;

import co.edu.corhuila.rutasur.entity.UsuarioTipo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persiste las asignaciones de roles de las cuentas.
 */
public interface UsuarioTipoRepositorio extends JpaRepository<UsuarioTipo, Long> {
}
