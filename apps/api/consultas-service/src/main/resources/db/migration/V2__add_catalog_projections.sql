CREATE TABLE servicio_proyeccion
(
    servicio_id    UUID PRIMARY KEY,
    experiencia_id UUID         NOT NULL,
    tipo           VARCHAR(50)  NOT NULL,
    nombre         VARCHAR(150) NOT NULL,
    descripcion    TEXT,
    activo         BOOLEAN      NOT NULL,
    actualizado_en TIMESTAMPTZ  NOT NULL
);
CREATE TABLE actividad_proyeccion
(
    actividad_id   UUID PRIMARY KEY,
    experiencia_id UUID         NOT NULL,
    nombre         VARCHAR(150) NOT NULL,
    descripcion    TEXT         NOT NULL,
    duracion_horas INTEGER      NOT NULL,
    activa         BOOLEAN      NOT NULL,
    actualizado_en TIMESTAMPTZ  NOT NULL
);
COMMENT
ON TABLE servicio_proyeccion IS 'Vista reconstruible de servicios del catálogo.';
COMMENT
ON TABLE actividad_proyeccion IS 'Vista reconstruible de actividades del catálogo.';
