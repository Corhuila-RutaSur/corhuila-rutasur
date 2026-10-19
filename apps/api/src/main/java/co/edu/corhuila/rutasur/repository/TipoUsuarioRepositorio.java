package co.edu.corhuila.rutasur.repository;

import co.edu.corhuila.rutasur.entity.TipoUsuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Consulta los roles configurados en el catalogo de usuarios.
 */
public interface TipoUsuarioRepositorio extends JpaRepository<TipoUsuario, Long> {

    Optional<TipoUsuario> findByNombreAndActivoTrue(String nombre);
}
