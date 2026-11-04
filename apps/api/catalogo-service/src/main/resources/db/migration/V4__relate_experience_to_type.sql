ALTER TABLE experiencia
    ADD COLUMN tipo_experiencia_id UUID;

INSERT INTO tipo_experiencia (nombre, descripcion)
SELECT DISTINCT UPPER(BTRIM(tipo)), 'Tipo migrado desde experiencia existente.'
FROM experiencia
WHERE BTRIM(tipo) <> '' ON CONFLICT (nombre) DO NOTHING;

UPDATE experiencia experiencia
SET tipo_experiencia_id = tipo_experiencia.id FROM tipo_experiencia
WHERE tipo_experiencia.nombre = UPPER (BTRIM(experiencia.tipo));

ALTER TABLE experiencia
    ALTER COLUMN tipo_experiencia_id SET NOT NULL;
ALTER TABLE experiencia
    ADD CONSTRAINT fk_experiencia_tipo_experiencia
        FOREIGN KEY (tipo_experiencia_id) REFERENCES tipo_experiencia (id);
CREATE INDEX ix_experiencia_tipo_experiencia ON experiencia (tipo_experiencia_id);

COMMENT
ON COLUMN experiencia.tipo_experiencia_id IS 'Tipo administrable asignado a la experiencia.';
COMMENT
ON COLUMN experiencia.tipo IS 'Nombre histórico y de lectura del tipo; se sincroniza desde tipo_experiencia al crear o editar.';
