package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Repositorio JPA interno para metadatos de imágenes. */
interface ImagenExperienciaRepositorioJpa extends JpaRepository<ImagenExperienciaEntidad, UUID> {
    List<ImagenExperienciaEntidad> findAllByExperienciaIdOrderByActivaDescOrdenAscCreadoEnAsc(UUID experienciaId);
    List<ImagenExperienciaEntidad> findAllByExperienciaIdAndActivaTrueOrderByOrdenAscCreadoEnAsc(UUID experienciaId);
    @Modifying
    @Query("update ImagenExperienciaEntidad imagen set imagen.principal = false, imagen.actualizadoEn = :fecha where imagen.experienciaId = :experienciaId and imagen.activa = true and imagen.principal = true")
    void quitarPortadaActiva(@Param("experienciaId") UUID experienciaId, @Param("fecha") OffsetDateTime fecha);
}
