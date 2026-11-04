CREATE
EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE salida
(
    id                     UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    experiencia_id_externa UUID         NOT NULL,
    experiencia_nombre     VARCHAR(150) NOT NULL,
    fecha_hora_inicio      TIMESTAMPTZ  NOT NULL,
    cupos_totales          INTEGER      NOT NULL CHECK (cupos_totales > 0),
    cupos_disponibles      INTEGER      NOT NULL CHECK (cupos_disponibles >= 0),
    estado                 VARCHAR(30)  NOT NULL,
    creado_en              TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE programacion_servicio
(
    id                  UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    salida_id           UUID           NOT NULL REFERENCES salida (id),
    servicio_id_externo UUID           NOT NULL,
    nombre              VARCHAR(150)   NOT NULL,
    precio              NUMERIC(14, 2) NOT NULL CHECK (precio >= 0),
    activo              BOOLEAN        NOT NULL DEFAULT TRUE
);
CREATE TABLE programacion_actividad
(
    id                   UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    salida_id            UUID           NOT NULL REFERENCES salida (id),
    actividad_id_externa UUID           NOT NULL,
    nombre               VARCHAR(150)   NOT NULL,
    precio               NUMERIC(14, 2) NOT NULL CHECK (precio >= 0),
    activo               BOOLEAN        NOT NULL DEFAULT TRUE
);
CREATE TABLE reserva
(
    id                     UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    codigo                 VARCHAR(30)    NOT NULL UNIQUE,
    usuario_id_externo     UUID           NOT NULL,
    salida_id              UUID           NOT NULL REFERENCES salida (id),
    cantidad_participantes INTEGER        NOT NULL CHECK (cantidad_participantes > 0),
    total                  NUMERIC(14, 2) NOT NULL CHECK (total >= 0),
    estado                 VARCHAR(30)    NOT NULL,
    correlacion_id         UUID           NOT NULL,
    creada_en              TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizada_en         TIMESTAMPTZ
);
CREATE TABLE participante_reserva
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reserva_id       UUID         NOT NULL REFERENCES reserva (id),
    nombres          VARCHAR(100) NOT NULL,
    apellidos        VARCHAR(100) NOT NULL,
    tipo_documento   VARCHAR(20)  NOT NULL,
    numero_documento VARCHAR(30)  NOT NULL,
    asistencia       BOOLEAN
);
CREATE TABLE detalle_reserva
(
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reserva_id               UUID           NOT NULL REFERENCES reserva (id),
    programacion_servicio_id UUID           NOT NULL REFERENCES programacion_servicio (id),
    precio_unitario          NUMERIC(14, 2) NOT NULL,
    cantidad                 INTEGER        NOT NULL CHECK (cantidad > 0)
);
CREATE TABLE detalle_reserva_actividad
(
    id                        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reserva_id                UUID           NOT NULL REFERENCES reserva (id),
    programacion_actividad_id UUID           NOT NULL REFERENCES programacion_actividad (id),
    precio_unitario           NUMERIC(14, 2) NOT NULL,
    cantidad                  INTEGER        NOT NULL CHECK (cantidad > 0)
);
CREATE TABLE outbox_evento
(
    id                   UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    tipo                 VARCHAR(100) NOT NULL,
    version              INTEGER      NOT NULL DEFAULT 1,
    agregado_id          UUID         NOT NULL,
    correlacion_id       UUID         NOT NULL,
    datos                JSONB        NOT NULL,
    ocurrido_en          TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    publicado_en         TIMESTAMPTZ,
    intentos_publicacion INTEGER      NOT NULL DEFAULT 0,
    ultimo_error         TEXT
);
CREATE TABLE inbox_evento
(
    id_evento    UUID PRIMARY KEY,
    tipo         VARCHAR(100) NOT NULL,
    origen       VARCHAR(100) NOT NULL,
    recibido_en  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    procesado_en TIMESTAMPTZ,
    estado       VARCHAR(20)  NOT NULL DEFAULT 'RECIBIDO'
);
CREATE TABLE referencia_experiencia
(
    id_externo     UUID PRIMARY KEY,
    nombre         VARCHAR(150) NOT NULL,
    destino        VARCHAR(150) NOT NULL,
    activa         BOOLEAN      NOT NULL,
    actualizado_en TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE referencia_servicio
(
    id_externo             UUID PRIMARY KEY,
    experiencia_id_externa UUID         NOT NULL,
    tipo                   VARCHAR(50)  NOT NULL,
    nombre                 VARCHAR(150) NOT NULL,
    activo                 BOOLEAN      NOT NULL,
    actualizado_en         TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE referencia_actividad
(
    id_externo             UUID PRIMARY KEY,
    experiencia_id_externa UUID         NOT NULL,
    nombre                 VARCHAR(150) NOT NULL,
    activo                 BOOLEAN      NOT NULL,
    actualizado_en         TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX ix_salida_experiencia_fecha ON salida (experiencia_id_externa, fecha_hora_inicio);
CREATE INDEX ix_programacion_actividad_salida_activa ON programacion_actividad (salida_id, activo);
CREATE INDEX ix_reserva_usuario ON reserva (usuario_id_externo, creada_en DESC);
CREATE INDEX ix_outbox_reservas_pendiente ON outbox_evento (ocurrido_en) WHERE publicado_en IS NULL;
COMMENT
ON TABLE salida IS 'Fuente de verdad de salidas y capacidad reservable.'; COMMENT
ON COLUMN salida.id IS 'UUID de salida.'; COMMENT
ON COLUMN salida.experiencia_id_externa IS 'Referencia sin FK a Catálogo.'; COMMENT
ON COLUMN salida.experiencia_nombre IS 'Snapshot comercial.'; COMMENT
ON COLUMN salida.fecha_hora_inicio IS 'Inicio programado.'; COMMENT
ON COLUMN salida.cupos_totales IS 'Capacidad máxima.'; COMMENT
ON COLUMN salida.cupos_disponibles IS 'Capacidad restante, protegida ACID.'; COMMENT
ON COLUMN salida.estado IS 'Estado comercial.'; COMMENT
ON COLUMN salida.creado_en IS 'Fecha de creación.';
COMMENT
ON TABLE reserva IS 'Agregado de reserva y fuente de verdad de cupos comprometidos.'; COMMENT
ON COLUMN reserva.id IS 'UUID de reserva.'; COMMENT
ON COLUMN reserva.codigo IS 'Código público único.'; COMMENT
ON COLUMN reserva.usuario_id_externo IS 'Referencia a Identidad sin FK.'; COMMENT
ON COLUMN reserva.salida_id IS 'Salida reservada.'; COMMENT
ON COLUMN reserva.cantidad_participantes IS 'Cantidad de viajeros.'; COMMENT
ON COLUMN reserva.total IS 'Total snapshot.'; COMMENT
ON COLUMN reserva.estado IS 'Estado de reserva.'; COMMENT
ON COLUMN reserva.correlacion_id IS 'Traza distribuida.'; COMMENT
ON COLUMN reserva.creada_en IS 'Creación.'; COMMENT
ON COLUMN reserva.actualizada_en IS 'Último cambio.';
COMMENT
ON TABLE programacion_servicio IS 'Servicios habilitados y precio para una salida.'; COMMENT
ON TABLE programacion_actividad IS 'Actividades habilitadas y precio para una salida.'; COMMENT
ON TABLE participante_reserva IS 'Participantes de una reserva.'; COMMENT
ON TABLE detalle_reserva IS 'Servicios comunes seleccionados para la reserva.'; COMMENT
ON TABLE detalle_reserva_actividad IS 'Actividades comunes seleccionadas para la reserva.'; COMMENT
ON TABLE outbox_evento IS 'Hechos de Reservas pendientes de publicar.'; COMMENT
ON TABLE inbox_evento IS 'Eventos consumidos de forma idempotente.';
