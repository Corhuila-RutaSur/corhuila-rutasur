package co.edu.corhuila.rutasur.identidad.adapter.out.messaging;

import co.edu.corhuila.rutasur.identidad.application.port.out.EventoIdentidadPuertoSalida;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Inserta hechos de integración en Outbox dentro de la transacción local.
 */
@Component
@RequiredArgsConstructor
public class OutboxIdentidadAdaptador implements EventoIdentidadPuertoSalida {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void registrarUsuarioCreado(UUID usuarioId, UUID correlacionId) {
        String datos = "{\"usuarioId\":\"" + usuarioId + "\"}";
        jdbcTemplate.update("insert into outbox_evento (id, tipo, agregado_id, correlacion_id, datos) values (?, ?, ?, ?, cast(? as jsonb))",
                UUID.randomUUID(), "UsuarioCreado", usuarioId, correlacionId, datos);
    }
}
