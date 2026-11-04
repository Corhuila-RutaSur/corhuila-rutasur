package co.edu.corhuila.rutasur.catalogo.application.port.out;

import co.edu.corhuila.rutasur.catalogo.domain.model.ServicioExperiencia;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de persistencia para los servicios configurables de una experiencia.
 */
public interface ServicioExperienciaPuertoSalida {
    ServicioExperiencia guardar(ServicioExperiencia servicio);

    Optional<ServicioExperiencia> buscarPorId(UUID identificador);

    boolean existeActivoPorExperienciaYNombre(UUID experienciaId, String nombre);

    List<ServicioExperiencia> consultarActivosPorExperiencia(UUID experienciaId);
}
