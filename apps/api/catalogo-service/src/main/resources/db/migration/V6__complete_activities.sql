ALTER TABLE actividad
    ADD CONSTRAINT uk_actividad_experiencia_nombre UNIQUE (experiencia_id, nombre);
CREATE INDEX ix_actividad_experiencia_nombre ON actividad (experiencia_id, nombre);
COMMENT
ON CONSTRAINT uk_actividad_experiencia_nombre ON actividad IS 'Impide repetir el nombre de una actividad dentro de una experiencia.';
