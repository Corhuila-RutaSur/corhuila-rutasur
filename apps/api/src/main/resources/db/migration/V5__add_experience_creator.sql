-- Relaciona cada experiencia con el usuario responsable de crearla.

alter table experiencia
    add column creado_por_usuario_id bigint not null references usuario(id);

comment on column experiencia.creado_por_usuario_id is 'Usuario que creo la experiencia de catalogo; no se modifica posteriormente.';
