CREATE TABLE salida_proyeccion
(
    salida_id         UUID PRIMARY KEY,
    experiencia_id    UUID        NOT NULL,
    fecha_hora_inicio TIMESTAMPTZ NOT NULL,
    cupos_disponibles INTEGER     NOT NULL CHECK (cupos_disponibles >= 0),
    estado            VARCHAR(30) NOT NULL,
    actualizado_en    TIMESTAMPTZ NOT NULL
);
CREATE INDEX ix_salida_proyeccion_experiencia_fecha ON salida_proyeccion (experiencia_id, fecha_hora_inicio);
COMMENT
ON TABLE salida_proyeccion IS 'Vista reconstruible de salidas reservables publicada por Reservas.';
