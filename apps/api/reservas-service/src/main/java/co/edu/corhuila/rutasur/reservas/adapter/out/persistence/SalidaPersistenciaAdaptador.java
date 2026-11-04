package co.edu.corhuila.rutasur.reservas.adapter.out.persistence;

import co.edu.corhuila.rutasur.reservas.application.port.out.SalidaPuertoSalida;
import co.edu.corhuila.rutasur.reservas.domain.model.Salida;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Adaptador JPA que implementa el puerto de salidas.
 */
@Component
@RequiredArgsConstructor
public class SalidaPersistenciaAdaptador implements SalidaPuertoSalida {
    private final SalidaRepositorioJpa repositorio;
    private final SalidaPersistenciaMapper mapper;

    @Override
    public Salida guardar(Salida salida) {
        return mapper.aDominio(repositorio.save(mapper.aEntidad(salida)));
    }

    @Override
    public Optional<Salida> buscarPorId(UUID id) {
        return repositorio.findById(id).map(mapper::aDominio);
    }

    @Override
    public boolean descontarCuposSiDisponibles(UUID id, int cantidad) {
        return repositorio.descontarCupos(id, cantidad) == 1;
    }

    @Override
    public boolean liberarCupos(UUID id, int cantidad) {
        return repositorio.liberarCupos(id, cantidad) == 1;
    }

    @Override
    public List<Salida> consultarDisponibles() {
        return repositorio.findAllByEstadoAndFechaHoraInicioAfterOrderByFechaHoraInicioAsc("PROGRAMADA", OffsetDateTime.now()).stream().filter(salida -> salida.getCuposDisponibles() > 0).map(mapper::aDominio).toList();
    }
}
