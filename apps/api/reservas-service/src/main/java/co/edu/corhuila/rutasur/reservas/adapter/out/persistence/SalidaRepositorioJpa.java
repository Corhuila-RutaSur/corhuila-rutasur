package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA interno de salidas.
 */
interface SalidaRepositorioJpa extends JpaRepository<SalidaEntidad, UUID> {
    List<SalidaEntidad> findAllByEstadoAndFechaHoraInicioAfterOrderByFechaHoraInicioAsc(String estado, OffsetDateTime fecha);

    @Modifying
    @Query("update SalidaEntidad s set s.cuposDisponibles=s.cuposDisponibles-:cantidad where s.id=:id and s.estado='PROGRAMADA' and s.cuposDisponibles>=:cantidad")
    int descontarCupos(@Param("id") UUID id, @Param("cantidad") int cantidad);

    @Modifying
    @Query("update SalidaEntidad s set s.cuposDisponibles=s.cuposDisponibles+:cantidad where s.id=:id and s.estado='PROGRAMADA' and s.cuposDisponibles+:cantidad<=s.cuposTotales")
    int liberarCupos(@Param("id") UUID id, @Param("cantidad") int cantidad);
}
