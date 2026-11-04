# 004 — Salidas y disponibilidad

**Estado:** Borrador  
**Servicio dueño:** Reservas  
**Origen V1:** Spec 003.

## Alcance y reglas

- Crear, actualizar, cancelar y consultar salidas programadas asociadas por referencia a una experiencia de Catálogo.
- Configurar servicios y actividades disponibles, su precio y cupos por salida.
- La disponibilidad comercial es propiedad de Reservas y se actualiza mediante comandos ACID locales.
- Una salida no se reserva si está cancelada, finalizada o no ofrece los servicios o actividades solicitados.
- No existe un plan comercial: una reserva selecciona directamente actividades y servicios habilitados en su salida; esa selección es común a todos sus participantes.
- Catálogo entrega información de presentación; no conserva ni decide cupos.

## Eventos

Consume `ExperienciaActualizada` e `ExperienciaInactivada` para validar/publicar referencias. Publica `SalidaProgramada`, `SalidaActualizada`, `DisponibilidadActualizada` y `SalidaCancelada`.

## Criterio de aceptación

```gherkin
Escenario: Crear una salida disponible
Dado que existe una experiencia activa conocida por Reservas
Cuando un administrador configura fecha, cupos, servicios y actividades
Entonces Reservas registra la salida y la disponibilidad inicial en su base propia
Y publica SalidaProgramada mediante Outbox.
```

## Modelo persistente inicial

`salida` conserva el snapshot de la experiencia, su fecha y los cupos. Las tablas
`programacion_servicio` y `programacion_actividad` habilitan elementos para una salida
y fijan su precio. Al crear una reserva, `detalle_reserva` y
`detalle_reserva_actividad` guardan el snapshot seleccionado; no se crean detalles
por participante, porque todos reciben la misma configuración.
# Consulta pública de disponibilidad de una salida

Consultas expone `GET /api/v2/public/experiencias/salidas/{salidaId}/disponibilidad`. El contrato devuelve la salida programada, fecha, estado y cupos disponibles; cada servicio programado incluye además sus propios cupos disponibles, precio y estado. Las actividades programadas incluyen precio y estado.

Reservas conserva la fuente de verdad de cupos y programación. Cada creación de salida, programación de servicio, programación de actividad y cambio de cupos por reserva publica un evento transaccional Outbox (`SalidaActualizada`, `ProgramacionServicioActualizada` o `ProgramacionActividadActualizada`). Consultas consume esos eventos de forma idempotente y reconstruye sus proyecciones propias; no consulta la base de datos de Reservas.

La selección enviada al comando de reserva usa los `programacionId` retornados por este endpoint. Antes de comprometer cupos, Reservas vuelve a validar que esas programaciones sigan activas: la consulta pública informa disponibilidad, pero la transacción ACID de Reservas es la autoridad final.

