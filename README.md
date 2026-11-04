# RutaSur

RutaSur es una plataforma web para descubrir, configurar, reservar y operar experiencias turísticas. Una experiencia puede ofrecer transporte, alojamiento, alimentación y actividades; cada cliente construye su propia combinación para una fecha de salida específica.

> El catálogo es público. Para crear, pagar, consultar o calificar una reserva, el usuario debe iniciar sesión.

## Estado del proyecto

La V1 ejecutable está congelada en la etiqueta `v1`. El proyecto inicia la definición de la V2: microservicios, arquitectura hexagonal interna, CQRS selectivo y comunicación por eventos. La documentación vigente está en [`docs/`](docs/README.md); la evidencia histórica de la V1 se conserva en [`docs/v1/`](docs/v1/README.md).

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

Consulta el [modelo entidad–relación histórico](docs/v1/architecture/modelo-entidad-relacion-v1.md), el [diccionario V1](docs/v1/database/modelo-de-datos-v1.md) y el [modelo de datos V2](docs/architecture/modelo-datos-v2.md) para el detalle.

## Estructura del repositorio

```text
apps/
  api/                 Backend Spring Boot
  web/                 Frontend Angular
docs/
  specs/               Fuente de verdad funcional V2 y criterios de aceptación
  architecture/        Arquitectura objetivo y diagramas
  database/            Modelos y estrategia de migraciones por servicio
  api/                 Convenciones de contratos HTTP
  events/              Contratos de eventos e integración asíncrona
  standards/           Reglas transversales y definición de terminado
  adr/                 Decisiones arquitectónicas
  v1/                  Evidencia histórica del MVP etiquetado como v1
```

## Documentación principal

| Documento | Para qué sirve |
| --- | --- |
| [Índice de documentación](docs/README.md) | Punto de entrada para la V1 histórica y la V2 vigente. |
| [ADR de migración V2](docs/adr/002-migracion-a-microservicios-v2.md) | Decisión de microservicios, hexagonal, CQRS y eventos. |
| [Ejecución local V2](docs/operations/ejecucion-local-v2.md) | Guía reproducible: bases, Floci, colas, variables y orden de inicio. |
| [Modelo de datos V2](docs/architecture/modelo-datos-v2.md) | Propiedad de datos, dominios y proyecciones CQRS. |
| [Flujo de reserva V2](docs/architecture/flujo-creacion-reserva-v2.md) | Bloqueo temporal, pago, confirmación y liberación de cupos. |
| [Documentación histórica V1](docs/v1/README.md) | Especificaciones, diagramas, estándares y modelo de datos preservados. |

La documentación fuente y versionada se mantiene en `docs/`. La [Wiki publicada](https://github.com/Corhuila-RutaSur/corhuila-rutasur/wiki) replica las páginas de consulta general y debe sincronizarse cuando cambie información funcional o técnica.

## Inicio local V2

La V2 requiere preparar PostgreSQL local, Floci y los recursos SNS/SQS antes de levantar los microservicios. Sigue la [guía de ejecución local V2](docs/operations/ejecucion-local-v2.md) en el orden indicado; contiene comandos reproducibles, variables, puertos, verificación y diagnóstico.

La V1 se puede consultar desde la etiqueta `v1` y su documentación histórica; no debe iniciarse como si fuera parte de la V2.

## Contrato de respuestas y autenticación

Todas las respuestas de la API usan `RespuestaGeneral<T>`: `codigoHttp`, `codigo`, `mensaje` y `data`. Los campos nulos se omiten. Un access token vencido devuelve `401` con `autenticacion.token.expirado`, señal para que el frontend intente renovar la sesión; un token inválido, revocado o malformado devuelve `autenticacion.token.invalido`.

## Reglas de contribución y documentación

1. Antes de modificar V1, consulta su especificación histórica en `docs/v1/specs/`. Para V2, crea o actualiza el spec, contrato y documentación vigente en `docs/` dentro del mismo cambio.
2. Todo cambio de comportamiento, flujo, estado, permiso, contrato HTTP, modelo de datos o configuración relevante debe actualizar la documentación afectada en el mismo cambio.
3. Cuando el cambio altere la visión general, arquitectura, arranque local, enlaces, estado del proyecto o forma de usarlo, también se debe actualizar este `README.md`.
4. Una migración Flyway aplicada no se modifica: se crea una nueva versión.
5. No se incluyen secretos, contraseñas ni tokens reales en el repositorio o en la documentación.

La norma histórica está en [012 — Gobierno de la documentación](docs/v1/specs/012-gobierno-documentacion.md); la evolución V2 se rige por el [ADR-002](docs/adr/002-migracion-a-microservicios-v2.md).
