package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import co.edu.corhuila.rutasur.catalogo.application.port.out.ActividadPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.model.Actividad;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA de actividades del catálogo.
 */
@Component
@RequiredArgsConstructor
public class ActividadPersistenciaAdaptador implements ActividadPuertoSalida {
    private final ActividadRepositorioJpa repositorio;
    private final ActividadPersistenciaMapper mapper;

    @Override
    public Actividad guardar(Actividad actividad) {
        return mapper.aDominio(repositorio.save(mapper.aEntidad(actividad)));
    }

    @Override
    public Optional<Actividad> buscarPorId(UUID identificador) {
        return repositorio.findById(identificador).map(mapper::aDominio);
    }

    @Override
    public boolean existeActivaPorExperienciaYNombre(UUID experienciaId, String nombre) {
        return repositorio.existsByExperienciaIdAndNombreIgnoreCaseAndActivaTrue(experienciaId, nombre);
    }

    @Override
    public List<Actividad> consultarActivasPorExperiencia(UUID experienciaId) {
        return repositorio.findAllByExperienciaIdAndActivaTrueOrderByNombreAsc(experienciaId).stream().map(mapper::aDominio).toList();
    }
}
