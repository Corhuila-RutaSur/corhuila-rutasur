CREATE
EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE notificacion
(
    id           UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    evento_id    UUID         NOT NULL,
    destinatario VARCHAR(200) NOT NULL,
    canal        VARCHAR(30)  NOT NULL,
    asunto       VARCHAR(200),
    contenido    TEXT         NOT NULL,
    estado       VARCHAR(30)  NOT NULL,
    intentos     INTEGER      NOT NULL DEFAULT 0,
    creada_en    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    enviada_en   TIMESTAMPTZ,
    UNIQUE (evento_id, destinatario, canal)
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
ON TABLE notificacion IS 'Entrega e historial de mensajes derivados de eventos.'; COMMENT
ON COLUMN notificacion.evento_id IS 'Evento origen idempotente.'; COMMENT
ON COLUMN notificacion.destinatario IS 'Destino del mensaje.'; COMMENT
ON COLUMN notificacion.canal IS 'Canal de entrega.'; COMMENT
ON COLUMN notificacion.asunto IS 'Asunto opcional.'; COMMENT
ON COLUMN notificacion.contenido IS 'Contenido renderizado.'; COMMENT
ON COLUMN notificacion.estado IS 'Estado de entrega.'; COMMENT
ON COLUMN notificacion.intentos IS 'Intentos realizados.'; COMMENT
ON COLUMN notificacion.creada_en IS 'Creación.'; COMMENT
ON COLUMN notificacion.enviada_en IS 'Entrega exitosa.'; COMMENT
ON TABLE inbox_evento IS 'Eventos procesados idempotentemente.';
