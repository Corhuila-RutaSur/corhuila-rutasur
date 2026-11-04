# Persistencia y migraciones V2

Cada microservicio posee una carpeta Flyway propia bajo `apps/api/<servicio>/src/main/resources/db/migration`. La primera migración ya existe para Identidad, Catálogo, Reservas, Pagos, Operación, Notificaciones y Consultas; cada una crea exclusivamente objetos de su base correspondiente.

## Regla de propiedad

Cada microservicio tendrá una base de datos PostgreSQL, un usuario técnico y una ruta de migraciones Flyway propios. En desarrollo local pueden coexistir en la misma instancia de PostgreSQL, pero no comparten esquema, tablas ni historial Flyway.

RutaSur usa el PostgreSQL instalado localmente por el equipo. Docker se reserva para Floci; no se levanta un contenedor PostgreSQL alterno. El script de creación única de bases está en `apps/api/infra/postgres/crear-bases-v2-locales.sql`.

| Servicio | Base de datos propuesta | Fuente de verdad |
| --- | --- | --- |
| Identidad | `rutasur_identidad_db` | Usuarios, roles y sesiones. |
| Catálogo | `rutasur_catalogo_db` | Experiencias y servicios comerciales. |
| Reservas | `rutasur_reservas_db` | Cupos, bloqueos y reservas. |
| Pagos | `rutasur_pagos_db` | Pagos e intentos. |
| Operación | `rutasur_operacion_db` | Recursos y ejecución. |
| Notificaciones | `rutasur_notificaciones_db` | Entregas e intentos. |
| Consultas | `rutasur_consultas_db` | Proyecciones CQRS. |

## Convenciones Flyway

- Una migración aplicada nunca se edita; se agrega una nueva versión.
- La ruta de cada servicio seguirá `src/main/resources/db/migration` dentro de su propio proyecto.
- Las migraciones usan el patrón `V<numero>__<descripcion_en_snake_case>.sql`.
- Cada tabla, columna, índice, restricción y dato semilla relevante debe incluir documentación PostgreSQL mediante `COMMENT ON`.
- Las tablas que publican eventos incluyen `outbox_evento`; las que consumen eventos persistentes incluyen `inbox_evento`.
- Los UUID externos no son llaves foráneas entre bases. Se nombran con el sufijo `_externo` o con un nombre explícito que indique su procedencia.

## Modelo de referencia

El diseño lógico completo está en [modelo-v2.dbml](modelo-v2.dbml). Debe actualizarse junto con la migración que afecte el modelo. La explicación visual y las decisiones de propiedad están en el [modelo de datos V2](../architecture/modelo-datos-v2.md).

> La base única `rutasur` y sus migraciones pertenecen a la evidencia histórica de [V1](../v1/database/README.md). No se modificarán durante la migración V2.
