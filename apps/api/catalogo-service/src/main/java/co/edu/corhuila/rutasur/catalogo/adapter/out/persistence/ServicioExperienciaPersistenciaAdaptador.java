package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import co.edu.corhuila.rutasur.catalogo.application.port.out.ServicioExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.model.ServicioExperiencia;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA para el puerto de servicios de experiencia.
 */
@Component
@RequiredArgsConstructor
public class ServicioExperienciaPersistenciaAdaptador implements ServicioExperienciaPuertoSalida {
    private final ServicioExperienciaRepositorioJpa repositorio;
    private final ServicioExperienciaPersistenciaMapper mapper;

    @Override
    public ServicioExperiencia guardar(ServicioExperiencia servicio) {
        return mapper.aDominio(repositorio.save(mapper.aEntidad(servicio)));
    }

    @Override
    public Optional<ServicioExperiencia> buscarPorId(UUID identificador) {
        return repositorio.findById(identificador).map(mapper::aDominio);
    }

    @Override
    public boolean existeActivoPorExperienciaYNombre(UUID experienciaId, String nombre) {
        return repositorio.existsByExperienciaIdAndNombreIgnoreCaseAndActivoTrue(experienciaId, nombre);
    }

    @Override
    public List<ServicioExperiencia> consultarActivosPorExperiencia(UUID experienciaId) {
        return repositorio.findAllByExperienciaIdAndActivoTrueOrderByNombreAsc(experienciaId).stream().map(mapper::aDominio).toList();
    }
}
