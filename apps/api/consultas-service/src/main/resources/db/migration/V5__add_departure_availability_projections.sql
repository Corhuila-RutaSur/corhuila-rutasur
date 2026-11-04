CREATE TABLE programacion_servicio_proyeccion
(
    programacion_id      UUID PRIMARY KEY,
    salida_id            UUID           NOT NULL,
    servicio_id_externo  UUID           NOT NULL,
    nombre               VARCHAR(150)   NOT NULL,
    precio               NUMERIC(14, 2) NOT NULL,
    activo               BOOLEAN        NOT NULL,
    actualizado_en       TIMESTAMPTZ    NOT NULL
);

CREATE TABLE programacion_actividad_proyeccion
(
    programacion_id      UUID PRIMARY KEY,
    salida_id            UUID           NOT NULL,
    actividad_id_externa UUID           NOT NULL,
    nombre               VARCHAR(150)   NOT NULL,
    precio               NUMERIC(14, 2) NOT NULL,
    activo               BOOLEAN        NOT NULL,
    actualizado_en       TIMESTAMPTZ    NOT NULL
);

CREATE INDEX ix_programacion_servicio_proyeccion_salida ON programacion_servicio_proyeccion (salida_id, activo);
CREATE INDEX ix_programacion_actividad_proyeccion_salida ON programacion_actividad_proyeccion (salida_id, activo);
COMMENT ON TABLE programacion_servicio_proyeccion IS 'Vista pública reconstruible de servicios disponibles por salida.';
COMMENT ON TABLE programacion_actividad_proyeccion IS 'Vista pública reconstruible de actividades disponibles por salida.';
