CREATE
EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE vehiculo
(
    id        UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    placa     VARCHAR(10) NOT NULL UNIQUE,
    capacidad INTEGER     NOT NULL CHECK (capacidad > 0),
    activo    BOOLEAN     NOT NULL DEFAULT TRUE
);
CREATE TABLE ejecucion_salida
(
    id                   UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    salida_id_externa    UUID        NOT NULL UNIQUE,
    guia_id_externo      UUID,
    conductor_id_externo UUID,
    vehiculo_id          UUID REFERENCES vehiculo (id),
    fecha_hora_real      TIMESTAMPTZ,
    estado               VARCHAR(30) NOT NULL,
    creado_en            TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE asistencia
(
    id                      UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    ejecucion_id            UUID        NOT NULL REFERENCES ejecucion_salida (id),
    reserva_id_externa      UUID        NOT NULL,
    participante_id_externo UUID        NOT NULL,
    asistio                 BOOLEAN     NOT NULL,
    marcada_en              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (ejecucion_id, participante_id_externo)
);
CREATE TABLE outbox_evento
(
    id             UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    tipo           VARCHAR(100) NOT NULL,
    version        INTEGER      NOT NULL DEFAULT 1,
    agregado_id    UUID         NOT NULL,
    correlacion_id UUID,
    datos          JSONB        NOT NULL,
    ocurrido_en    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    publicado_en   TIMESTAMPTZ
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
COMMENT
ON TABLE vehiculo IS 'Vehículos operativos propios de Operación.'; COMMENT
ON TABLE ejecucion_salida IS 'Ejecución real de una salida referenciada externamente.'; COMMENT
ON TABLE asistencia IS 'Asistencia registrada por participante.'; COMMENT
ON TABLE outbox_evento IS 'Hechos operativos por publicar.'; COMMENT
ON TABLE inbox_evento IS 'Eventos operativos recibidos de forma idempotente.';
