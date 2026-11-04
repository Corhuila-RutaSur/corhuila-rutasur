package co.edu.corhuila.rutasur.catalogo.application.port.in;

import co.edu.corhuila.rutasur.catalogo.domain.model.ServicioExperiencia;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de entrada para el catálogo de servicios de una experiencia.
 */
public interface GestionarServicioExperienciaUseCase {
    ServicioExperiencia crear(UUID experienciaId, ServicioExperienciaCommand comando);

    ServicioExperiencia actualizar(UUID experienciaId, UUID servicioId, ServicioExperienciaCommand comando);

    List<ServicioExperiencia> consultarActivos(UUID experienciaId);

    void inactivar(UUID experienciaId, UUID servicioId);
}
