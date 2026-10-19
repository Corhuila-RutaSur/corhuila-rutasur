# 004 — Reservas configurables

## Objetivo

Permitir que un cliente compre varios cupos y configure una combinación de servicios de una misma salida.

## Alcance

- Crear una reserva para una salida abierta.
- Seleccionar uno o más servicios programados de esa salida.
- Registrar uno o varios participantes.
- Asignar los servicios adquiridos a cada participante.
- Conservar precios y subtotales al momento de reservar.
- Permitir actualizar observaciones mientras la reserva esté pendiente y cancelarla sin borrado físico.

## Fuera de alcance

- Integración con una pasarela de pago específica.
- Cambios de fecha o transferencias entre reservas después de confirmar el pago.

## Actores y permisos

| Actor | Puede hacer |
| --- | --- |
| Cliente | Crear y consultar sus propias reservas. |
| Administrador | Consultar y administrar reservas según reglas operativas. |

## Reglas de negocio

1. Una reserva pertenece a una sola salida.
2. Un cliente puede comprar varios cupos y registrar varios participantes.
3. Todos los servicios de una reserva deben estar programados para la misma salida.
4. La reserva selecciona un único paquete de servicios y todos los participantes reciben exactamente esos mismos servicios.
5. Cada detalle conserva el precio unitario y subtotal existentes al momento de la compra.
6. La cantidad de un detalle equivale al número de participantes del paquete y debe ser mayor que cero.
7. Solo puede existir un participante responsable por reserva.
8. El sistema no puede confirmar una reserva si no hay cupos suficientes.
9. Una reserva cancelada conserva su historial y restituye los cupos que había consumido.
10. Hasta implementar JWT, únicamente la creación de reservas se habilita de forma pública temporal.

## Flujos y casos alternativos

### Flujo exitoso

1. El cliente selecciona una salida abierta.
2. Elige los servicios que desea: por ejemplo, alojamiento y caminata, sin transporte.
3. Indica participantes y qué servicios tomará cada uno.
4. El sistema valida misma salida, cantidades, participantes y disponibilidad.
5. Se calcula el total y la reserva queda pendiente de pago.

### Errores y casos límite

- No se puede combinar un servicio de Tatacoa con uno de La Guajira.
- No se puede reservar una salida cancelada, cerrada o finalizada.
- No se puede asignar a un participante un servicio no comprado.

## Criterios de aceptación

```gherkin
Escenario: Compra sin transporte
Dado que una salida ofrece transporte, alojamiento, cena y caminata lunar
Cuando un cliente selecciona alojamiento, cena y caminata para dos participantes
Entonces la reserva se crea sin transporte y con los servicios asignados a esos participantes.

Escenario: Impedir mezcla de experiencias
Dado que el cliente tiene una reserva para una salida de Tatacoa
Cuando intenta agregar un servicio de una salida de La Guajira
Entonces el sistema rechaza la operación sin modificar la reserva.
```
