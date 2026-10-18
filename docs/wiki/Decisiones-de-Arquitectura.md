# Decisiones de arquitectura

Las decisiones arquitectónicas se registran como ADR (Architecture Decision Record) en `docs/adr/` dentro del repositorio principal.

| ADR | Decisión | Estado |
| --- | --- | --- |
| ADR-001 | Usar arquitectura por capas en la V1. | Aceptada |

## ADR-001: arquitectura por capas

La V1 emplea `controller → service → repository → PostgreSQL`. La arquitectura hexagonal queda como alternativa de evolución, no como requisito inicial.

La decisión busca mantener el proyecto comprensible y acorde con el alcance de la primera entrega, sin perder la posibilidad de evolucionar si el dominio lo justifica.
