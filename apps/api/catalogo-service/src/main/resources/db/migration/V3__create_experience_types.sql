CREATE TABLE tipo_experiencia
(
    id             UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    nombre         VARCHAR(80) NOT NULL,
    descripcion    TEXT,
    activo         BOOLEAN     NOT NULL DEFAULT TRUE,
    creado_en      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMPTZ,
    CONSTRAINT uk_tipo_experiencia_nombre UNIQUE (nombre)
);

INSERT INTO tipo_experiencia (nombre, descripcion)
VALUES ('ASTROTURISMO', 'Experiencias de observación astronómica y del cielo nocturno.'),
       ('AVENTURA', 'Actividades turísticas de reto físico y exploración.'),
       ('NATURALEZA', 'Experiencias de ecosistemas, senderos y paisajes.'),
       ('CULTURAL', 'Experiencias de patrimonio, comunidades y tradiciones.'),
       ('GASTRONOMICA', 'Experiencias culinarias y de productos regionales.'),
       ('BIENESTAR', 'Experiencias de descanso, salud y conexión natural.'),
       ('RURAL_AGROTURISMO', 'Experiencias en fincas y procesos campesinos.'),
       ('EVENTO_ESPECIAL', 'Experiencias asociadas a fechas o eventos puntuales.');

CREATE INDEX ix_tipo_experiencia_activo_nombre ON tipo_experiencia (activo, nombre);

COMMENT
ON TABLE tipo_experiencia IS 'Catálogo administrable de clasificaciones turísticas para experiencias.';
COMMENT
ON COLUMN tipo_experiencia.id IS 'Identificador interno UUID del tipo de experiencia.';
COMMENT
ON COLUMN tipo_experiencia.nombre IS 'Nombre único que clasifica la experiencia.';
COMMENT
ON COLUMN tipo_experiencia.descripcion IS 'Descripción funcional de la clasificación.';
COMMENT
ON COLUMN tipo_experiencia.activo IS 'Indica si el tipo puede asignarse a nuevas experiencias.';
