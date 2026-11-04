package co.edu.corhuila.rutasur.catalogo.adapter.out.persistence;

import co.edu.corhuila.rutasur.catalogo.application.port.out.ExperienciaPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.model.Experiencia;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA que persiste el agregado de experiencia.
 */
@Component
@RequiredArgsConstructor
public class ExperienciaPersistenciaAdaptador implements ExperienciaPuertoSalida {
    private final ExperienciaRepositorioJpa repositorio;
    private final ExperienciaPersistenciaMapper mapper;

    @Override
    public Experiencia guardar(Experiencia experiencia) {
        return mapper.aDominio(repositorio.save(mapper.aEntidad(experiencia)));
    }

    @Override
    public Optional<Experiencia> buscarPorId(UUID identificador) {
        return repositorio.findById(identificador).map(mapper::aDominio);
    }

    @Override
    public Optional<Experiencia> bloquearPorId(UUID identificador) {
        return repositorio.findWithLockById(identificador).map(mapper::aDominio);
    }

    @Override
    public List<Experiencia> consultarActivas() {
        return repositorio.findAllByActivaTrueOrderByNombreAsc().stream().map(mapper::aDominio).toList();
    }
}
