# ADR-003 — Pago explícito posterior a la reserva

**Estado:** Aceptada · **Fecha:** 2026-10-31

## Decisión

Se conserva el flujo V1: crear reserva la deja pendiente; el cliente inicia el pago explícitamente después. `ReservaCreada` informa a Consultas y Notificaciones, pero Pagos no la consume ni genera una intención automática. Pagos publica su resultado y Reservas actualiza su estado local.

## Consecuencias

- La Saga aplica desde el comando de pago, no desde crear reserva.
- Pagos no consume `ReservaCreada`; sí consume `ReservaCancelada` desde su cola exclusiva `pagos-reserva` para devolver únicamente pagos ya aprobados.
- Se preserva la intención del cliente y el flujo funcional V1.
