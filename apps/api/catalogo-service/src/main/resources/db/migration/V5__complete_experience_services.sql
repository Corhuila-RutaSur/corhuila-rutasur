ALTER TABLE servicio_experiencia
    ADD COLUMN actualizado_en TIMESTAMPTZ;
ALTER TABLE servicio_experiencia
    ADD CONSTRAINT uk_servicio_experiencia_nombre UNIQUE (experiencia_id, nombre);
CREATE INDEX ix_servicio_experiencia_activo ON servicio_experiencia (experiencia_id, activo, nombre);
COMMENT
ON COLUMN servicio_experiencia.actualizado_en IS 'Fecha de la última modificación del servicio ofrecido.';
