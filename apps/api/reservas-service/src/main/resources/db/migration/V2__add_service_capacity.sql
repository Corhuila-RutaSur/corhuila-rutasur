ALTER TABLE programacion_servicio
    ADD COLUMN cupos_totales INTEGER,
    ADD COLUMN cupos_disponibles INTEGER;

UPDATE programacion_servicio programacion
SET cupos_totales = salida.cupos_totales,
    cupos_disponibles = salida.cupos_disponibles
FROM salida
WHERE salida.id = programacion.salida_id;

ALTER TABLE programacion_servicio
    ALTER COLUMN cupos_totales SET NOT NULL,
    ALTER COLUMN cupos_disponibles SET NOT NULL,
    ADD CONSTRAINT ck_programacion_servicio_cupos_totales CHECK (cupos_totales > 0),
    ADD CONSTRAINT ck_programacion_servicio_cupos_disponibles CHECK (cupos_disponibles >= 0 AND cupos_disponibles <= cupos_totales);

CREATE INDEX ix_programacion_servicio_salida_activo ON programacion_servicio (salida_id, activo);
COMMENT ON COLUMN programacion_servicio.cupos_totales IS 'Capacidad máxima específica del servicio dentro de la salida.';
COMMENT ON COLUMN programacion_servicio.cupos_disponibles IS 'Capacidad restante específica del servicio, protegida ACID.';
