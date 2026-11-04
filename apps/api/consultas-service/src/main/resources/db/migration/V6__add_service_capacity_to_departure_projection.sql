ALTER TABLE programacion_servicio_proyeccion
    ADD COLUMN cupos_disponibles INTEGER NOT NULL DEFAULT 0;
ALTER TABLE programacion_servicio_proyeccion
    ADD CONSTRAINT ck_programacion_servicio_proyeccion_cupos CHECK (cupos_disponibles >= 0);
COMMENT ON COLUMN programacion_servicio_proyeccion.cupos_disponibles IS 'Cupos disponibles específicos del servicio para la salida.';
