package co.edu.corhuila.rutasur.repository;

import co.edu.corhuila.rutasur.entity.EstadoReserva;
import co.edu.corhuila.rutasur.entity.Reserva;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Repositorio de cabeceras de reserva y de disponibilidad transaccional. */
public interface ReservaRepositorio extends JpaRepository<Reserva, Long> {
    Optional<Reserva> findByIdAndEstadoNot(Long id, EstadoReserva estado);
    List<Reserva> findByUsuarioIdOrderByFechaReservaDesc(Long usuarioId);

    @Query(value = "select id, cupos_disponibles as \"cuposDisponibles\", estado::text as estado "
            + "from salida_experiencia where id = :salidaId for update", nativeQuery = true)
    Optional<SalidaReservaProyeccion> bloquearSalida(@Param("salidaId") Long salidaId);

    @Query(value = "select id, precio_unitario as \"precioUnitario\", cupos_disponibles as \"cuposDisponibles\", activo "
            + "from programacion_servicio where salida_experiencia_id = :salidaId and id in (:ids) for update", nativeQuery = true)
    List<ProgramacionReservaProyeccion> bloquearProgramaciones(
            @Param("salidaId") Long salidaId, @Param("ids") List<Long> ids);

    @Modifying
    @Query(value = "update salida_experiencia set cupos_disponibles = cupos_disponibles - :cantidad "
            + "where id = :salidaId and cupos_disponibles >= :cantidad", nativeQuery = true)
    int descontarCuposSalida(@Param("salidaId") Long salidaId, @Param("cantidad") int cantidad);

    @Modifying
    @Query(value = "update salida_experiencia set cupos_disponibles = cupos_disponibles + :cantidad where id = :salidaId", nativeQuery = true)
    int restaurarCuposSalida(@Param("salidaId") Long salidaId, @Param("cantidad") int cantidad);

    @Modifying
    @Query(value = "update programacion_servicio set cupos_disponibles = cupos_disponibles - :cantidad "
            + "where id = :programacionId and cupos_disponibles >= :cantidad", nativeQuery = true)
    int descontarCuposServicio(@Param("programacionId") Long programacionId, @Param("cantidad") int cantidad);

    @Modifying
    @Query(value = "update programacion_servicio set cupos_disponibles = cupos_disponibles + :cantidad where id = :programacionId", nativeQuery = true)
    int restaurarCuposServicio(@Param("programacionId") Long programacionId, @Param("cantidad") int cantidad);
}
