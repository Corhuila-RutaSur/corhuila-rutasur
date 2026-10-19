# 001 — Glosario del dominio

## Objetivo

Establecer un lenguaje común para producto, diseño, frontend, backend, pruebas e inteligencia artificial.

## Términos

| Término | Definición |
| --- | --- |
| Experiencia | Producto turístico general ofrecido por RutaSur, por ejemplo: *Noche de estrellas en la Tatacoa*. |
| Servicio | Componente seleccionable de una experiencia: transporte, alojamiento, alimentación o actividad. |
| Salida | Realización de una experiencia en una fecha y hora concretas, con estado y cupos propios. |
| Programación de servicio | Disponibilidad, cupos y precio de un servicio dentro de una salida. |
| Reserva | Compra realizada por un cliente para una única salida; representa el paquete configurado por ese cliente. |
| Comprador | Usuario que crea y paga la reserva. No necesariamente es el único participante. |
| Participante | Persona que asistirá a uno o más servicios incluidos en una reserva. |
| Detalle de reserva | Servicio elegido, cantidad y precio conservado dentro de una reserva. |
| Asistencia | Registro individual de si un participante asistió a un servicio. |
| Guía | Persona que acompaña la ejecución general de una salida. |
| Conductor | Persona que opera el vehículo cuando un servicio requiere transporte. |
| Ejecución | Registro de lo ocurrido realmente en una salida o servicio: horas, estado y novedades. |

## Regla de uso

1. No se debe llamar “paquete” a una entidad fija: en V1 el paquete es la combinación de servicios de una `reserva`.
2. No se debe confundir una `experiencia` con una `salida`; la primera es catálogo y la segunda es una fecha programada.
3. No se debe confundir guía con conductor; pueden ser personas distintas.
