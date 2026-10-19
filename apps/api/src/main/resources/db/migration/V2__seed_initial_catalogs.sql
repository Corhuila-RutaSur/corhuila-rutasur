-- Datos iniciales de catalogo. No contiene usuarios ni datos operativos.

insert into tipo_usuario (nombre, descripcion) values
    ('CLIENTE', 'Persona que compra o participa en experiencias'),
    ('ADMINISTRADOR', 'Persona que administra el sistema'),
    ('GUIA', 'Persona que acompana la experiencia durante su ejecucion'),
    ('CONDUCTOR', 'Persona responsable de conducir un vehiculo')
on conflict (nombre) do nothing;

insert into tipo_experiencia (nombre, descripcion) values
    ('ASTROTURISMO', 'Observacion de estrellas, astronomia y experiencias nocturnas'),
    ('AVENTURA', 'Senderismo, actividades de adrenalina y recorridos activos'),
    ('NATURALEZA', 'Avistamiento, recorridos ecologicos y contacto con el entorno'),
    ('CULTURAL', 'Historia, patrimonio, artesanias y comunidades locales'),
    ('GASTRONOMICA', 'Rutas culinarias, degustaciones y cocina local'),
    ('BIENESTAR', 'Descanso, relajacion y conexion con la naturaleza'),
    ('RURAL_AGROTURISMO', 'Fincas, procesos campesinos y vida rural'),
    ('EVENTO_ESPECIAL', 'Festivales, celebraciones y actividades en fechas puntuales')
on conflict (nombre) do nothing;
