# 007 — Calificaciones

## Objetivo

Permitir que el comprador evalúe de forma general una reserva después de que la experiencia haya terminado.

## Alcance

- Registrar puntuación de 1 a 5.
- Registrar comentario opcional.
- Conservar fecha de calificación.

## Fuera de alcance

- Calificar individualmente a guía, conductor, alojamiento o actividad.
- Respuestas públicas de RutaSur a comentarios.

## Reglas de negocio

1. Una reserva admite máximo una calificación en V1.
2. Solo el comprador de la reserva puede calificarla.
3. La puntuación debe estar entre 1 y 5.
4. La reserva debe estar finalizada para poder ser calificada.

## Criterios de aceptación

```gherkin
Escenario: Calificar una reserva finalizada
Dado que el cliente es comprador de una reserva finalizada
Cuando registra una puntuación de 5 y un comentario
Entonces el sistema guarda una única calificación para esa reserva.

Escenario: Impedir calificación antes de la experiencia
Dado que una reserva está confirmada pero no finalizada
Cuando el comprador intenta calificarla
Entonces el sistema rechaza la solicitud.
```
