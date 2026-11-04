package co.edu.corhuila.rutasur.identidad.adapter.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA privado del adaptador de Identidad.
 */
interface UsuarioRepositorioJpa extends JpaRepository<UsuarioEntidad, UUID> {
    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByTelefono(String telefono);

    /**
     * Asigna un rol mediante una operación explícita sin cargar el agregado completo.
     */
    @Modifying
    @Query(value = "insert into usuario_rol (usuario_id, rol_id) values (:usuarioId, :rolId) on conflict do nothing", nativeQuery = true)
    void asignarRol(@Param("usuarioId") UUID usuarioId, @Param("rolId") UUID rolId);
}
