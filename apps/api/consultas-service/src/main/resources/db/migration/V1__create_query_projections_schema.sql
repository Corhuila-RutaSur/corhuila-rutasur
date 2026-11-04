CREATE
EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE experiencia_proyeccion
(
    experiencia_id        UUID PRIMARY KEY,
    nombre                VARCHAR(150) NOT NULL,
    destino               VARCHAR(150) NOT NULL,
    descripcion           TEXT         NOT NULL,
    imagen_principal      VARCHAR(500),
    calificacion_promedio NUMERIC(3, 2),
    activa                BOOLEAN      NOT NULL,
    actualizado_en        TIMESTAMPTZ  NOT NULL
);
CREATE TABLE reserva_proyeccion
(
    reserva_id             UUID PRIMARY KEY,
    usuario_id_externo     UUID           NOT NULL,
    codigo                 VARCHAR(30)    NOT NULL,
    experiencia_id_externa UUID           NOT NULL,
    experiencia_nombre     VARCHAR(150)   NOT NULL,
    destino                VARCHAR(150),
    fecha_salida           TIMESTAMPTZ,
    total                  NUMERIC(14, 2) NOT NULL,
    estado                 VARCHAR(30)    NOT NULL,
    actualizado_en         TIMESTAMPTZ    NOT NULL
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
ON TABLE experiencia_proyeccion IS 'Vista reconstruible del catálogo público.'; COMMENT
ON COLUMN experiencia_proyeccion.experiencia_id IS 'Identificador externo de Catálogo.'; COMMENT
ON TABLE reserva_proyeccion IS 'Vista reconstruible de reservas por usuario.'; COMMENT
ON COLUMN reserva_proyeccion.usuario_id_externo IS 'Identificador externo de Identidad.'; COMMENT
ON TABLE inbox_evento IS 'Eventos usados para reconstruir proyecciones de forma idempotente.';
