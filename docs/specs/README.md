# Especificaciones funcionales V2

Los specs son la fuente de verdad funcional para el desarrollo de V2. Definen qué problema se resuelve, quién puede usarlo, reglas, límites, escenarios y criterios de aceptación; no reemplazan el código, OpenAPI, migraciones ni ADR. Consulta también la [trazabilidad V1 → V2](trazabilidad-v1-v2.md).

## Ciclo de trabajo

1. Crear un spec a partir de [la plantilla](plantilla-spec.md).
2. Relacionarlo con el ADR, diagramas, datos, endpoints y eventos que afecte.
3. Revisar y acordar reglas y criterios de aceptación antes de implementar.
4. Implementar y probar contra esos criterios.
5. Actualizar el spec si cambia el comportamiento; no dejar la documentación para después.

## Backlog vigente y dueño

| Estado | Spec propuesto | Servicio dueño |
| --- | --- | --- |
| Implementado parcialmente | 001 — Lenguaje ubicuo y límites de contexto | Todos |
| Implementado parcialmente | [002 — Identidad, autorización y sesiones](002-identidad-autorizacion-y-sesiones.md) | Identidad |
| Implementado parcialmente | [003 — Catálogo de experiencias](003-catalogo-de-experiencias.md) | Catálogo |
| Implementado parcialmente | 004 — Salidas y disponibilidad | Reservas |
| Implementado parcialmente | [005 — Reservas, cupos y Saga](005-reservas-cupos-y-saga.md) | Reservas |
| Implementado parcialmente | [006 — Pagos, reembolsos y Saga](006-pagos-reembolsos-y-saga.md) | Pagos |
| Pendiente | 007 — Operación y asistencia | Operación |
| Pendiente | 008 — Calificaciones | Reservas / Consultas |
| En implementación | [009 — Proyecciones de consulta](009-proyecciones-de-consulta.md) | Consultas |
| Implementado parcialmente | 010 — Estados e invariantes | Todos |
| Implementado parcialmente | 011 — Gobierno y trazabilidad | Todos |
| Pendiente crítico | [013 — Seguridad de borde y autorización distribuida](013-seguridad-de-borde-y-autorizacion-distribuida.md) | Gateway / Identidad / todos |

La especificación de Identidad es el punto de partida de la extracción. Su primera migración aún no fue aplicada, por lo que puede alinearse con este documento antes del primer arranque del servicio.
