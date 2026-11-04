CREATE TABLE imagen_experiencia_proyeccion
(
    imagen_id      UUID PRIMARY KEY,
    experiencia_id UUID         NOT NULL,
    ruta           VARCHAR(500) NOT NULL,
    principal      BOOLEAN      NOT NULL,
    orden          INTEGER      NOT NULL,
    activa         BOOLEAN      NOT NULL,
    actualizado_en TIMESTAMPTZ  NOT NULL
);
CREATE INDEX ix_imagen_proyeccion_experiencia_activa_orden ON imagen_experiencia_proyeccion (experiencia_id, activa, orden);
CREATE UNIQUE INDEX uk_imagen_proyeccion_portada_activa ON imagen_experiencia_proyeccion (experiencia_id) WHERE principal AND activa;
COMMENT ON TABLE imagen_experiencia_proyeccion IS 'Vista reconstruible de la galería pública de Catálogo.';
