# RutaSur API

Backend REST de RutaSur. Gestionará el catálogo de experiencias turísticas, salidas, reservas configurables, pagos, operación, asistencia, autenticación y sesiones.

La API se construye con Spring Boot, Java, PostgreSQL y Flyway. Su contrato se publica automáticamente en OpenAPI y puede explorarse desde Swagger UI o Scalar.

## Estado actual

La base técnica de la V1 está preparada:

- Migraciones Flyway para el modelo turístico, catálogos y autenticación.
- CORS configurable para Angular.
- Seguridad sin sesión y cerrada por defecto.
- OpenAPI, Swagger UI y Scalar habilitados.
- Reglas de arquitectura por capas y pruebas ArchUnit.

Los controladores y casos de uso se implementarán a partir de las especificaciones de `docs/specs/`.

## Tecnologías

| Componente | Tecnología |
| --- | --- |
| Lenguaje | Java 25 |
| Framework | Spring Boot 4 |
| API | Spring MVC + Spring Validation |
| Persistencia | Spring Data JPA + PostgreSQL |
| Migraciones | Flyway |
| Seguridad | Spring Security; JWT y refresh tokens planificados por modelo y specs |
| Documentación | Springdoc OpenAPI, Swagger UI y Scalar |
| Pruebas | JUnit 5, ArchUnit; Testcontainers cuando haya integración de persistencia |

## Arquitectura V1

```text
api → controller → service → repository → PostgreSQL
                 ↓
          mapper / DTO / entity
```

| Paquete | Responsabilidad |
| --- | --- |
| `api/` | Interfaces con rutas, contrato HTTP y anotaciones OpenAPI. |
| `controller/` | Implementa contratos API y delega en servicios. |
| `service/` | Casos de uso, transacciones y reglas de negocio. |
| `repository/` | Consultas y persistencia con Spring Data JPA. |
| `entity/` | Mapeos JPA del modelo persistente. |
| `dto/request` | Contratos de entrada y validaciones. |
| `dto/response` | Contratos de salida. |
| `mapper/` | Transformaciones con MapStruct. |
| `exception/` | Excepciones, códigos de error, mensajes internacionalizados y manejador global. |
| `config/` | CORS, seguridad, OpenAPI y configuración transversal. |

No se exponen entidades JPA a HTTP, y los controladores no acceden directamente a repositorios.

## Inicio rápido

### Requisitos

- Java 25.
- PostgreSQL disponible en `localhost:5433`, o variables de conexión equivalentes.

### Crear la base de datos

Conéctate a PostgreSQL y crea la base una única vez:

```sql
CREATE DATABASE rutasur;
```

Flyway creará las tablas al iniciar la aplicación. No es necesario crear entidades JPA antes del primer arranque: `ddl-auto: validate` solo valida las entidades que existan, sin crear ni alterar el esquema.

### Variables de entorno

```text
DB_URL=jdbc:postgresql://localhost:5433/rutasur
DB_USERNAME=postgres
DB_PASSWORD=tu_contrasena
CORS_ALLOWED_ORIGINS=http://localhost:4200
SERVER_PORT=8090
```

### Ejecutar

Desde la carpeta `apps/api`:

```powershell
.\mvnw.cmd spring-boot:run
```

## Servicios locales

| Servicio | Dirección |
| --- | --- |
| API | `http://localhost:8090` |
| Health | `http://localhost:8090/actuator/health` |
| Swagger UI | `http://localhost:8090/swagger-ui/index.html` |
| Scalar | `http://localhost:8090/scalar` |
| OpenAPI JSON | `http://localhost:8090/v3/api-docs` |

Swagger UI y Scalar consumen el mismo contrato OpenAPI; no se mantiene un HTML de Scalar manualmente.

## Base de datos y migraciones

Las migraciones se ubican en `src/main/resources/db/migration/`.

| Migración | Contenido |
| --- | --- |
| `V1__create_initial_schema.sql` | Modelo de experiencias, salidas, reservas, pagos y operación; tablas y columnas documentadas con comentarios PostgreSQL. |
| `V2__seed_initial_catalogs.sql` | Roles de usuario y tipos de experiencia iniciales. |
| `V3__create_authentication_schema.sql` | Sesiones, refresh tokens de un solo uso, access tokens revocados y auditoría. |

Reglas importantes:

- Flyway es la única vía para cambiar estructuras o datos semilla.
- Una migración aplicada no se modifica; se crea una versión nueva.
- Hibernate valida el modelo mediante `ddl-auto: validate`; no crea ni modifica tablas.
- Los tokens no se guardan en texto plano: solo hashes de refresh token y `jti` revocados.

## Seguridad y acceso

La configuración actual es sin sesión HTTP (`STATELESS`) y deniega rutas por defecto. Son públicas únicamente:

```text
/actuator/health
/actuator/info
/v3/api-docs/**
/swagger-ui/**
/scalar/**
/api/v1/public/**
```

El catálogo y las salidas públicas se expondrán bajo `/api/v1/public/**`. Reservas, pagos, operación y datos privados requerirán autenticación; la especificación de JWT está en `docs/specs/011-autenticacion-y-sesiones.md`.

## Calidad y pruebas

```powershell
.\mvnw.cmd test
```

ArchUnit verifica que las capas no se acoplen indebidamente. Cada caso de uso futuro debe incluir pruebas unitarias JUnit 5, y las consultas/migraciones PostgreSQL se comprobarán con Testcontainers.

## Documentación y reglas obligatorias

Antes de implementar, consulta:

1. `docs/specs/`: qué debe hacer la funcionalidad.
2. `docs/architecture/estructura-del-proyecto.md`: límites entre capas.
3. `docs/standards/backend-spring.md`: MapStruct, contratos OpenAPI, consultas, complejidad, Javadoc y errores.
4. `docs/standards/pruebas.md`: estrategia de pruebas.
5. `AGENTS.md`: reglas transversales del repositorio.

Todo cambio debe mantener alineadas especificaciones, migraciones, modelo, OpenAPI, Wiki y README cuando corresponda.
