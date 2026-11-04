# 003 — Gestión de salidas

## Objetivo

Permitir programar una experiencia para una fecha concreta y definir qué servicios se ofrecerán, con su precio y disponibilidad.

## Alcance

- Crear y administrar salidas de una experiencia.
- Definir fecha, hora, cupos y estado de la salida.
- Habilitar servicios de la experiencia para una salida con precio y cupos propios.

## Fuera de alcance

- Cobro de la reserva.
- Asignación de guía, conductor y vehículo durante la ejecución.

## Actores y permisos

| Actor | Puede hacer |
| --- | --- |
| Administrador | Crear, abrir, cerrar, cancelar y consultar salidas; programar sus servicios. |
| Cliente | Consultar solo salidas abiertas y servicios activos con cupos. |

## Reglas de negocio

1. Una salida pertenece a una única experiencia.
2. Los servicios programados en una salida deben pertenecer a esa misma experiencia.
3. Los cupos totales deben ser mayores que cero y los disponibles no pueden ser negativos ni superar los totales.
4. El precio de un servicio programado no puede ser negativo.
5. Un mismo servicio solo puede programarse una vez por salida.
6. Una salida cerrada, cancelada o finalizada no admite nuevas reservas.

## Criterios de aceptación

```gherkin
Escenario: Programar una salida con servicios
Dado que existe la experiencia "Noche de estrellas en la Tatacoa"
Cuando el administrador programa una salida para el 15 de diciembre
Y habilita transporte, alojamiento y caminata lunar con precios propios
Entonces dichos servicios solo podrán comprarse dentro de esa salida.

Escenario: Rechazar un servicio de otra experiencia
Dado que una salida pertenece a la experiencia Tatacoa
Cuando el administrador intenta programar alojamiento de una experiencia de La Guajira
Entonces el sistema rechaza la operación.
```
