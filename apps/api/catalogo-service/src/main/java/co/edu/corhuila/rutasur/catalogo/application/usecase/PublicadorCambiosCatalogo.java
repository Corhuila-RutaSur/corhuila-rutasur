package co.edu.corhuila.rutasur.catalogo.application.usecase;

import co.edu.corhuila.rutasur.catalogo.application.port.out.EventoOutboxPuertoSalida;
import co.edu.corhuila.rutasur.catalogo.domain.model.*;
import co.edu.corhuila.rutasur.contratos.evento.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Traduce cambios propios de Catálogo a contratos de integración versionados.
 */
@Component
@RequiredArgsConstructor
public class PublicadorCambiosCatalogo {
    private final EventoOutboxPuertoSalida outbox;
    private final ObjectMapper mapper;

    public void experiencia(Experiencia item, UUID correlacionId) {
        registrar("ExperienciaActualizada", item.id(), correlacionId, new ExperienciaSincronizadaDatos(item.id(), item.nombre(), item.destino(), item.descripcion(), item.duracionHoras(), item.activa()));
    }

    public void servicio(ServicioExperiencia item, UUID correlacionId) {
        registrar("ServicioActualizado", item.id(), correlacionId, new ServicioSincronizadoDatos(item.id(), item.experienciaId(), item.tipo(), item.nombre(), item.descripcion(), item.activo()));
    }

    public void actividad(Actividad item, UUID correlacionId) {
        registrar("ActividadActualizada", item.id(), correlacionId, new ActividadSincronizadaDatos(item.id(), item.experienciaId(), item.nombre(), item.descripcion(), item.duracionHoras(), item.activa()));
    }

    /** Publica el cambio de galería para que Consultas reconstruya portada y detalle. */
    public void imagen(ImagenExperiencia item, UUID correlacionId) {
        registrar("ImagenExperienciaActualizada", item.id(), correlacionId,
                new ImagenExperienciaSincronizadaDatos(item.id(), item.experienciaId(), item.ruta(), item.principal(), item.orden(), item.activa()));
    }

    private void registrar(String tipo, UUID agregadoId, UUID correlacionId, Object datos) {
        try {
            outbox.registrar(tipo, agregadoId, correlacionId, mapper.writeValueAsString(datos));
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("No fue posible serializar un cambio de catálogo.", error);
        }
    }
}
