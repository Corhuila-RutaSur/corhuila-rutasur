# Base de datos y migraciones

## Creacion inicial

Flyway ejecuta migraciones *dentro* de una base de datos a la que la aplicacion ya puede conectarse. Por ello, la base `rutasur` se crea una sola vez con PostgreSQL o pgAdmin; el script de apoyo esta en `crear-base-rutasur.sql`.

Luego se configura la aplicacion mediante variables de entorno:

```text
DB_URL=jdbc:postgresql://localhost:5432/rutasur
DB_USERNAME=postgres
DB_PASSWORD=tu_contrasena
```

## Versionado

| Archivo | Proposito |
| --- | --- |
| `V1__create_initial_schema.sql` | Crea tipos, tablas, restricciones, indices y reglas de integridad del modelo V1. |
| `V2__seed_initial_catalogs.sql` | Carga roles y tipos de experiencia iniciales. |
| `V3__create_authentication_schema.sql` | Crea sesiones, refresh tokens de uso único, revocación de access tokens y auditoría. |

Las migraciones viven en `apps/api/src/main/resources/db/migration/`. Una migracion que ya fue aplicada en un ambiente compartido no debe editarse: un cambio posterior se incorpora en una nueva version, por ejemplo `V3__add_x.sql`.

Todas las tablas y columnas de las migraciones incluyen comentarios PostgreSQL (`COMMENT ON`). Estos se pueden consultar desde pgAdmin y otras herramientas para entender el propósito de cada elemento sin salir de la base de datos.
