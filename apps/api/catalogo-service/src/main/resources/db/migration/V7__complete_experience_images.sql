ALTER TABLE experiencia_imagen
    ADD COLUMN activa BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN actualizado_en TIMESTAMPTZ;

CREATE INDEX ix_experiencia_imagen_activa_orden
    ON experiencia_imagen (experiencia_id, activa, orden);

WITH portadas_duplicadas AS (
    SELECT id,
           ROW_NUMBER() OVER (PARTITION BY experiencia_id ORDER BY orden, creado_en, id) AS posicion
    FROM experiencia_imagen
    WHERE activa = TRUE AND principal = TRUE
)
UPDATE experiencia_imagen imagen
SET principal = FALSE,
    actualizado_en = CURRENT_TIMESTAMP
FROM portadas_duplicadas duplicada
WHERE imagen.id = duplicada.id
  AND duplicada.posicion > 1;

CREATE UNIQUE INDEX uk_experiencia_imagen_portada_activa
    ON experiencia_imagen (experiencia_id)
    WHERE principal AND activa;

COMMENT ON COLUMN experiencia_imagen.activa IS 'Indica si la imagen permanece visible en la galería.';
COMMENT ON COLUMN experiencia_imagen.actualizado_en IS 'Fecha de la ultima actualización de portada, orden o estado.';
