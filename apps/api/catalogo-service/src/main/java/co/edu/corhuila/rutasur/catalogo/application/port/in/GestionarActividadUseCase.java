package co.edu.corhuila.rutasur.catalogo.application.port.in;

import co.edu.corhuila.rutasur.catalogo.domain.model.Actividad;

import java.util.List;
import java.util.UUID;

/**
 * Puerto de entrada para actividades de una experiencia.
 */
public interface GestionarActividadUseCase {
    Actividad crear(UUID experienciaId, ActividadCommand comando);

    Actividad actualizar(UUID experienciaId, UUID actividadId, ActividadCommand comando);

    List<Actividad> consultarActivas(UUID experienciaId);

    void inactivar(UUID experienciaId, UUID actividadId);
}
