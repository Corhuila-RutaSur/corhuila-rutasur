CREATE
EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE pago
(
    id                   UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    reserva_id_externa   UUID           NOT NULL,
    correlacion_id       UUID           NOT NULL UNIQUE,
    monto                NUMERIC(14, 2) NOT NULL CHECK (monto >= 0),
    moneda               VARCHAR(3)     NOT NULL DEFAULT 'COP',
    estado               VARCHAR(30)    NOT NULL,
    referencia_proveedor VARCHAR(100),
    creado_en            TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en       TIMESTAMPTZ
);
CREATE TABLE reembolso
(
    id        UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    pago_id   UUID           NOT NULL REFERENCES pago (id),
    monto     NUMERIC(14, 2) NOT NULL CHECK (monto >= 0),
    estado    VARCHAR(30)    NOT NULL,
    motivo    VARCHAR(255),
    creado_en TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP
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
COMMENT
ON TABLE pago IS 'Fuente de verdad de intenciones y resultados de pago.'; COMMENT
ON COLUMN pago.id IS 'UUID del pago.'; COMMENT
ON COLUMN pago.reserva_id_externa IS 'Referencia a Reserva sin FK.'; COMMENT
ON COLUMN pago.correlacion_id IS 'Identificador idempotente del flujo.'; COMMENT
ON COLUMN pago.monto IS 'Monto del pago.'; COMMENT
ON COLUMN pago.moneda IS 'Moneda ISO.'; COMMENT
ON COLUMN pago.estado IS 'Estado financiero.'; COMMENT
ON COLUMN pago.referencia_proveedor IS 'Referencia del simulador/proveedor.'; COMMENT
ON COLUMN pago.creado_en IS 'Fecha de creación.'; COMMENT
ON COLUMN pago.actualizado_en IS 'Último cambio.'; COMMENT
ON TABLE reembolso IS 'Compensaciones o devoluciones de pagos.'; COMMENT
ON TABLE outbox_evento IS 'Hechos de pago por publicar.'; COMMENT
ON TABLE inbox_evento IS 'Eventos consumidos idempotentemente.';
