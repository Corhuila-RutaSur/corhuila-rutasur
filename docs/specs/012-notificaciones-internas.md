# 012 — Notificaciones internas

**Estado:** Implementación inicial V2  
**Servicio dueño:** Notificaciones  
**Origen:** Eventos de Reserva V2.

## Propósito

El servicio conserva un buzón interno por usuario. No envía correos, SMS ni WhatsApp en esta versión: su resultado es una notificación visible desde la aplicación, independiente de proveedores externos.

## Eventos consumidos

| Evento | Resultado para el usuario |
| --- | --- |
| `ReservaCreada` | Aviso de reserva pendiente de pago. |
| `ReservaConfirmada` | Aviso de reserva confirmada. |
| `ReservaCancelada` | Aviso de cancelación y liberación de cupos. |

El servicio no consume directamente `PagoRechazado` ni `PagoExpirado`: esos eventos no contienen el usuario propietario y Reservas publica posteriormente `ReservaCancelada`, que sí contiene el destinatario y confirma que la compensación fue correcta.

## Flujo implementado

1. SNS distribuye los eventos de `reservas.reserva` a la cola exclusiva `notificaciones-reserva`.
2. Notificaciones consume la cola, valida el tipo y genera un mensaje interno.
3. En una sola transacción persiste la notificación e Inbox; un reintento del mismo evento no crea un segundo aviso.
4. El frontend consulta el buzón por usuario y puede marcar un aviso como leído.

## API inicial

- `GET /api/v2/usuarios/{usuarioId}/notificaciones`
- `PATCH /api/v2/usuarios/{usuarioId}/notificaciones/{notificacionId}/leida`

La integración de `usuarioId` con el JWT/Gateway se aplicará cuando ese flujo de seguridad llegue al Gateway. El servicio no consulta la base de datos de Identidad ni la de Reservas.
