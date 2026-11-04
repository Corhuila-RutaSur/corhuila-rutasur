package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import co.edu.corhuila.rutasur.catalogo.application.port.out.TipoExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.model.TipoExperiencia;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador que implementa el puerto de tipos con JPA.
 */
@Component
@RequiredArgsConstructor
public class TipoExperienciaPersistenciaAdaptador implements TipoExperienciaPuertoSalida {
    private final TipoExperienciaRepositorioJpa repositorio;
    private final TipoExperienciaPersistenciaMapper mapper;

    @Override
    public boolean existeActivoPorNombre(String nombre) {
        return repositorio.existsByNombreIgnoreCaseAndActivoTrue(nombre);
    }

    @Override
    public TipoExperiencia guardar(TipoExperiencia tipo) {
        return mapper.aDominio(repositorio.save(mapper.aEntidad(tipo)));
    }

    @Override
    public Optional<TipoExperiencia> buscarPorId(UUID identificador) {
        return repositorio.findById(identificador).map(mapper::aDominio);
    }

    @Override
    public List<TipoExperiencia> consultarActivos() {
        return repositorio.findAllByActivoTrueOrderByNombreAsc().stream().map(mapper::aDominio).toList();
    }
}
