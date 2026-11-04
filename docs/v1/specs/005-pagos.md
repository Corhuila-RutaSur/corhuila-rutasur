# 005 — Pagos

## Objetivo

Registrar los pagos de una reserva y reflejar su resultado en el proceso de compra.

## Alcance

- Registrar intentos de pago, referencia, medio, valor, estado y respuesta externa.
- Permitir más de un intento de pago por reserva.
- Registrar reembolsos cuando corresponda.

## Fuera de alcance

- Selección de una pasarela de pagos o su integración técnica.
- Facturación electrónica.

## Reglas de negocio

1. Todo pago pertenece a una reserva.
2. La referencia de un pago, cuando exista, debe ser única.
3. Un pago aprobado confirma la reserva únicamente cuando cubre el total requerido; esta validación deberá considerar abonos si se habilitan.
4. Un pago rechazado no elimina la reserva y permite un nuevo intento.
5. Un reembolso debe quedar registrado como tal y conservar trazabilidad.

## Criterios de aceptación

```gherkin
Escenario: Pago aprobado
Dado que existe una reserva pendiente por pagar
Cuando se registra un pago aprobado por el valor total
Entonces el pago conserva su referencia y la reserva puede pasar a confirmada.

Escenario: Pago rechazado
Dado que existe una reserva pendiente
Cuando se registra un pago rechazado
Entonces la reserva permanece pendiente y el cliente puede realizar otro intento.
```

## Preguntas abiertas y supuestos

- V1 registra pagos, pero la pasarela concreta se define antes de implementar el módulo.
- La aceptación de pagos parciales requiere una decisión posterior.
