CREATE
EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE experiencia
(
    id              UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    tipo            VARCHAR(50)  NOT NULL,
    nombre          VARCHAR(150) NOT NULL,
    destino         VARCHAR(150) NOT NULL,
    descripcion     TEXT         NOT NULL,
    duracion_horas  INTEGER      NOT NULL,
    recomendaciones TEXT,
    activa          BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en  TIMESTAMPTZ
);
CREATE TABLE servicio_experiencia
(
    id             UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    experiencia_id UUID         NOT NULL REFERENCES experiencia (id),
    tipo           VARCHAR(50)  NOT NULL,
    nombre         VARCHAR(150) NOT NULL,
    descripcion    TEXT,
    activo         BOOLEAN      NOT NULL DEFAULT TRUE,
    creado_en      TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE experiencia_imagen
(
    id             UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    experiencia_id UUID         NOT NULL REFERENCES experiencia (id),
    ruta           VARCHAR(500) NOT NULL,
    principal      BOOLEAN      NOT NULL DEFAULT FALSE,
    orden          INTEGER      NOT NULL DEFAULT 0,
    creado_en      TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE outbox_evento
(
    id                   UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    tipo                 VARCHAR(100) NOT NULL,
    version              INTEGER      NOT NULL DEFAULT 1,
    agregado_id          UUID         NOT NULL,
    correlacion_id       UUID,
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
CREATE INDEX ix_experiencia_activa_nombre ON experiencia (activa, nombre);
CREATE INDEX ix_outbox_catalogo_pendiente ON outbox_evento (ocurrido_en) WHERE publicado_en IS NULL;
COMMENT
ON TABLE experiencia IS 'Fuente de verdad del catálogo de experiencias turísticas.';
COMMENT
ON COLUMN experiencia.id IS 'UUID interno de la experiencia.'; COMMENT
ON COLUMN experiencia.tipo IS 'Clasificación turística.'; COMMENT
ON COLUMN experiencia.nombre IS 'Nombre comercial.'; COMMENT
ON COLUMN experiencia.destino IS 'Lugar principal.'; COMMENT
ON COLUMN experiencia.descripcion IS 'Descripción pública.'; COMMENT
ON COLUMN experiencia.duracion_horas IS 'Duración estimada.'; COMMENT
ON COLUMN experiencia.recomendaciones IS 'Recomendaciones para el visitante.'; COMMENT
ON COLUMN experiencia.activa IS 'Indica disponibilidad en catálogo.'; COMMENT
ON COLUMN experiencia.creado_en IS 'Fecha de creación.'; COMMENT
ON COLUMN experiencia.actualizado_en IS 'Última actualización.';
COMMENT
ON TABLE servicio_experiencia IS 'Servicios configurables ofrecidos por una experiencia.'; COMMENT
ON COLUMN servicio_experiencia.id IS 'UUID del servicio.'; COMMENT
ON COLUMN servicio_experiencia.experiencia_id IS 'Experiencia propietaria.'; COMMENT
ON COLUMN servicio_experiencia.tipo IS 'Tipo de servicio.'; COMMENT
ON COLUMN servicio_experiencia.nombre IS 'Nombre mostrado.'; COMMENT
ON COLUMN servicio_experiencia.descripcion IS 'Detalle del servicio.'; COMMENT
ON COLUMN servicio_experiencia.activo IS 'Habilitación comercial.'; COMMENT
ON COLUMN servicio_experiencia.creado_en IS 'Fecha de creación.';
COMMENT
ON TABLE experiencia_imagen IS 'Metadatos de imágenes; el archivo vive en almacenamiento externo.'; COMMENT
ON COLUMN experiencia_imagen.id IS 'UUID de imagen.'; COMMENT
ON COLUMN experiencia_imagen.experiencia_id IS 'Experiencia propietaria.'; COMMENT
ON COLUMN experiencia_imagen.ruta IS 'Ruta segura del archivo.'; COMMENT
ON COLUMN experiencia_imagen.principal IS 'Imagen de portada.'; COMMENT
ON COLUMN experiencia_imagen.orden IS 'Orden de galería.'; COMMENT
ON COLUMN experiencia_imagen.creado_en IS 'Fecha de carga.';
COMMENT
ON TABLE outbox_evento IS 'Hechos de Catálogo pendientes de publicar.'; COMMENT
ON TABLE inbox_evento IS 'Eventos consumidos por Catálogo para idempotencia.';
