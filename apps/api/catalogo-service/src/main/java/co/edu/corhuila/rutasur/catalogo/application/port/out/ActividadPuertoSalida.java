package co.edu.corhuila.rutasur.catalogo.application.port.out;

import co.edu.corhuila.rutasur.catalogo.domain.model.Actividad;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia de actividades turísticas.
 */
public interface ActividadPuertoSalida {
    Actividad guardar(Actividad actividad);

    Optional<Actividad> buscarPorId(UUID identificador);

    boolean existeActivaPorExperienciaYNombre(UUID experienciaId, String nombre);

    List<Actividad> consultarActivasPorExperiencia(UUID experienciaId);
}
