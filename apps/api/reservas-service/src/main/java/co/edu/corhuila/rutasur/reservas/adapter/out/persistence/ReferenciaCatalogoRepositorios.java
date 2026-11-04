package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

interface ReferenciaExperienciaRepositorio extends JpaRepository<ReferenciaExperienciaEntidad, UUID> {
}

interface ReferenciaServicioRepositorio extends JpaRepository<ReferenciaServicioEntidad, UUID> {
}

interface ReferenciaActividadRepositorio extends JpaRepository<ReferenciaActividadEntidad, UUID> {
}
