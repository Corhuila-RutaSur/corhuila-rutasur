# RutaSur

RutaSur es una plataforma web para descubrir, configurar, reservar y operar experiencias turísticas. Una experiencia puede ofrecer transporte, alojamiento, alimentación y actividades; cada cliente construye su propia combinación para una fecha de salida específica.

> El catálogo es público. Para crear, pagar, consultar o calificar una reserva, el usuario debe iniciar sesión.

## Estado del proyecto

El proyecto se encuentra en la fase de definición de la V1: modelo de datos, reglas de negocio, migraciones, arquitectura y configuraciones base ya están establecidos. Los módulos funcionales y sus endpoints se implementarán guiados por las especificaciones de `docs/specs/`.

## Problema que resuelve

RutaSur evita administrar experiencias como paquetes rígidos. Por ejemplo, una persona que visita la Tatacoa puede adquirir solo una caminata y una fogata; otra puede añadir transporte, alojamiento y alimentación. Sin embargo, los servicios de una reserva siempre pertenecen a la misma salida, por lo que no se mezclan destinos o fechas incompatibles.

## Arquitectura

```text
Angular + Angular Material
           ↓ HTTP / JSON
Spring Boot REST API
           ↓
PostgreSQL + Flyway
```

La V1 usa arquitectura por capas:

```text
controller → service → repository → PostgreSQL
```

| Componente | Tecnología | Responsabilidad |
| --- | --- | --- |
| Frontend | Angular 22 + Angular Material | Interfaz web y experiencia de usuario. |
| Backend | Spring Boot 4 + Java 25 | API REST, reglas de negocio, seguridad y documentación OpenAPI. |
| Persistencia | PostgreSQL + Flyway | Modelo relacional, versionado y trazabilidad de cambios. |
| API docs | Swagger UI + Scalar | Exploración y prueba de los endpoints futuros sobre un único contrato OpenAPI. |

## Dominio en una mirada

```text
Experiencia
  └── Servicios seleccionables
        └── Salida programada (fecha, cupos, precio)
              └── Reserva configurable
                    └── Participantes, pagos, asistencia y ejecución
```

Consulta el [modelo entidad–relación](docs/architecture/modelo-entidad-relacion-v1.md) y el [diccionario de datos](docs/database/modelo-de-datos-v1.md) para el detalle.

## Estructura del repositorio

```text
apps/
  api/                 Backend Spring Boot
  web/                 Frontend Angular
docs/
  specs/               Fuente de verdad funcional y criterios de aceptación
  architecture/        Arquitectura, diagramas y contratos
  database/            Guía de PostgreSQL y migraciones
  adr/                 Decisiones arquitectónicas
```

## Documentación principal

| Documento | Para qué sirve |
| --- | --- |
| [Especificaciones funcionales](docs/specs/README.md) | Define el comportamiento antes de implementar. |
| [Reglas de negocio](docs/specs/009-reglas-de-negocio.md) | Reglas transversales del dominio. |
| [Autenticación y sesiones](docs/specs/011-autenticacion-y-sesiones.md) | Acceso público, JWT, refresh tokens y revocación. |
| [Gestión de usuarios](docs/specs/013-gestion-de-usuarios.md) | Registro, perfil, validación de datos e inactivación lógica. |
| [Modelo de datos](docs/database/modelo-de-datos-v1.md) | Propósito de cada tabla y regla de persistencia. |
| [Modelo entidad–relación](docs/architecture/modelo-entidad-relacion-v1.md) | Relaciones principales entre entidades. |
| [Migraciones](docs/database/README.md) | Creación de base, versionado y orden de Flyway. |
| [Documentación de API](docs/architecture/documentacion-api.md) | Swagger UI, Scalar y OpenAPI JSON. |
| [Estándar backend Spring](docs/standards/backend-spring.md) | DTOs, MapStruct, contratos OpenAPI, complejidad, errores, proyecciones y persistencia. |
| [Estándar de pruebas](docs/standards/pruebas.md) | JUnit 5, ArchUnit, pruebas web, integración y calidad. |
| [Estándar frontend Angular](docs/standards/frontend-angular.md) | UI reutilizable, formularios, validadores, estilos, servicios, guards y pruebas. |
| [Componentes reutilizables del frontend](docs/architecture/componentes-reutilizables-frontend.md) | Catalogo, validacion y reglas de uso de la interfaz. |
| [Decisiones de arquitectura](docs/adr/README.md) | ADRs vigentes del proyecto. |

La documentación fuente y versionada se mantiene en `docs/`. La [Wiki publicada](https://github.com/Corhuila-RutaSur/corhuila-rutasur/wiki) replica las páginas de consulta general y debe sincronizarse cuando cambie información funcional o técnica.

## Inicio rápido local

### Requisitos

- Java 25.
- Node.js y npm compatibles con Angular 22.
- PostgreSQL en ejecución.

### Base de datos

Crear una única vez la base `rutasur` siguiendo [la guía de base de datos](docs/database/README.md). Flyway aplicará automáticamente las migraciones al iniciar el backend.

Variables locales esperadas:

```text
DB_URL=jdbc:postgresql://localhost:5433/rutasur
DB_USERNAME=postgres
DB_PASSWORD=tu_contrasena
CORS_ALLOWED_ORIGINS=http://localhost:4200
JWT_SECRET=secreto_base64_de_al_menos_32_bytes
```

### Backend

```powershell
cd apps/api
.\mvnw.cmd spring-boot:run
```

```text
API:         http://localhost:8090
Swagger UI:  http://localhost:8090/swagger-ui/index.html
Scalar:      http://localhost:8090/scalar
OpenAPI:     http://localhost:8090/v3/api-docs
Health:      http://localhost:8090/actuator/health
```

### Frontend

```powershell
cd apps/web
npm install
npm start
```

La aplicación web se sirve normalmente en `http://localhost:4200`.

## Contrato de respuestas y autenticación

Todas las respuestas de la API usan `RespuestaGeneral<T>`: `codigoHttp`, `codigo`, `mensaje` y `data`. Los campos nulos se omiten. Un access token vencido devuelve `401` con `autenticacion.token.expirado`, señal para que el frontend intente renovar la sesión; un token inválido, revocado o malformado devuelve `autenticacion.token.invalido`.

## Reglas de contribución y documentación

1. Antes de implementar, consulta o crea la especificación correspondiente en `docs/specs/`.
2. Todo cambio de comportamiento, flujo, estado, permiso, contrato HTTP, modelo de datos o configuración relevante debe actualizar la documentación afectada en el mismo cambio.
3. Cuando el cambio altere la visión general, arquitectura, arranque local, enlaces, estado del proyecto o forma de usarlo, también se debe actualizar este `README.md`.
4. Una migración Flyway aplicada no se modifica: se crea una nueva versión.
5. No se incluyen secretos, contraseñas ni tokens reales en el repositorio o en la documentación.

La norma verificable completa está en [012 — Gobierno de la documentación](docs/specs/012-gobierno-documentacion.md).
