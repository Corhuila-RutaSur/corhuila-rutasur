# 002 — Gestión de experiencias

## Objetivo

Permitir que un administrador construya y mantenga el catálogo de experiencias turísticas y sus servicios seleccionables.

## Alcance

- Gestionar tipos de experiencia.
- Crear, consultar, editar, activar e inactivar experiencias.
- Asociar imágenes a una experiencia y marcar una como principal.
- Crear servicios de tipo `TRANSPORTE`, `ALOJAMIENTO`, `ALIMENTACION` o `ACTIVIDAD`.

## Fuera de alcance

- Programar fechas, precios o cupos; se gestiona en la especificación 003.
- Ejecutar una experiencia o controlar asistencia.

## Actores y permisos

| Actor | Puede hacer |
| --- | --- |
| Administrador | Gestionar el catálogo completo. |
| Cliente | Consultar únicamente experiencias y servicios activos. |

## Reglas de negocio

1. Toda experiencia debe pertenecer a un tipo de experiencia activo.
2. Una experiencia puede tener uno o varios servicios seleccionables.
3. Una experiencia puede incluir servicios de cualquier tipo y el cliente podrá combinarlos libremente en una salida.
4. El nombre de un servicio no puede repetirse dentro de una misma experiencia.
5. Inactivar una experiencia o servicio impide ofrecerlo en nuevas reservas, sin borrar el historial existente.

## Criterios de aceptación

```gherkin
Escenario: Crear una experiencia de astroturismo
Dado que un administrador registró el tipo ASTROTURISMO
Cuando crea la experiencia "Noche de estrellas en la Tatacoa"
Entonces la experiencia queda disponible para programar salidas.

Escenario: Configurar servicios combinables
Dado que existe una experiencia activa
Cuando el administrador agrega transporte, alojamiento, alimentación y caminata lunar
Entonces cada servicio queda disponible para programarse de manera independiente.
```

## Preguntas abiertas y supuestos

- V1 no define límite de imágenes por experiencia.
- V1 maneja un único tipo principal por experiencia; la clasificación múltiple podrá evaluarse después.
