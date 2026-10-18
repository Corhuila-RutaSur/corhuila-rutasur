# Arquitectura V1 por capas

RutaSur implementa en su primera versión una arquitectura por capas. La prioridad es tener una solución clara, fácil de presentar y simple de mantener.

```text
HTTP Request
     ↓
Controller
     ↓
Service
     ↓
Repository
     ↓
PostgreSQL
```

## Capas backend

| Capa | Responsabilidad |
| --- | --- |
| `controller` | Expone endpoints REST, recibe DTOs, devuelve respuestas HTTP. |
| `service` | Implementa reglas de negocio y coordina operaciones. |
| `repository` | Consulta y persiste datos con Spring Data JPA. |
| `entity` | Modela tablas y relaciones JPA. |
| `dto` | Define contratos de entrada y salida de la API. |
| `mapper` | Convierte entre entidades y DTOs. |
| `exception` | Centraliza errores de negocio y respuestas de error. |
| `config` | Contiene configuración transversal y seguridad. |

## Reglas

- Los controladores no contienen reglas de negocio ni consultan repositorios directamente.
- Los servicios no exponen detalles HTTP como `ResponseEntity`.
- Las entidades JPA no se devuelven directamente desde la API.
- Los DTOs se validan en la frontera HTTP.
- Las migraciones de base de datos se crean en `src/main/resources/db/migration` mediante Flyway.

El frontend utiliza `core`, `shared`, `layout` y `features`; esta separación evita que elementos globales y funcionalidades concretas se mezclen.
