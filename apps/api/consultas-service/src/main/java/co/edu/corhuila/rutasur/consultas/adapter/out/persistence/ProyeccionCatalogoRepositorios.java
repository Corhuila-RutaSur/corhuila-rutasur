package co.edu.corhuila.rutasur.consultas.adapter.out.persistence;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

interface ExperienciaProyeccionRepositorio extends JpaRepository<ExperienciaProyeccionEntidad, UUID> {
    List<ExperienciaProyeccionEntidad> findAllByActivaTrueOrderByNombreAsc();
}

interface ServicioProyeccionRepositorio extends JpaRepository<ServicioProyeccionEntidad, UUID> {
    List<ServicioProyeccionEntidad> findAllByExperienciaIdAndActivoTrue(UUID id);
}

interface ActividadProyeccionRepositorio extends JpaRepository<ActividadProyeccionEntidad, UUID> {
    List<ActividadProyeccionEntidad> findAllByExperienciaIdAndActivaTrue(UUID id);
}

interface SalidaProyeccionRepositorio extends JpaRepository<SalidaProyeccionEntidad, UUID> {
    List<SalidaProyeccionEntidad> findAllByExperienciaIdAndEstadoAndFechaHoraInicioAfterOrderByFechaHoraInicioAsc(UUID id, String estado, java.time.OffsetDateTime fecha);
}

interface InboxRepositorio extends JpaRepository<InboxEntidad, UUID> {
}

interface ImagenExperienciaProyeccionRepositorio extends JpaRepository<ImagenExperienciaProyeccionEntidad, UUID> {
    List<ImagenExperienciaProyeccionEntidad> findAllByExperienciaIdAndActivaTrueOrderByOrdenAsc(UUID experienciaId);
    Optional<ImagenExperienciaProyeccionEntidad> findFirstByExperienciaIdAndActivaTrueAndPrincipalTrue(UUID experienciaId);
}

interface ProgramacionServicioProyeccionRepositorio extends JpaRepository<ProgramacionServicioProyeccionEntidad, UUID> {
    List<ProgramacionServicioProyeccionEntidad> findAllBySalidaIdAndActivoTrueOrderByNombreAsc(UUID salidaId);
}

interface ProgramacionActividadProyeccionRepositorio extends JpaRepository<ProgramacionActividadProyeccionEntidad, UUID> {
    List<ProgramacionActividadProyeccionEntidad> findAllBySalidaIdAndActivoTrueOrderByNombreAsc(UUID salidaId);
}
