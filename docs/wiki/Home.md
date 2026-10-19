# RutaSur

RutaSur es una aplicación web para la gestión de procesos asociados a rutas. El proyecto se compone de un frontend Angular y un backend Spring Boot.

## Estado actual

- Backend inicializado con Spring Boot, JPA, Flyway, PostgreSQL, Spring Security y Actuator.
- Frontend inicializado con Angular y Angular Material.
- Arquitectura V1 definida por capas.
- Especificaciones funcionales como fuente de verdad antes de implementar.

## Arquitectura resumida

```text
Angular + Angular Material
           ↓ HTTP / JSON
Spring Boot REST API
           ↓
PostgreSQL
```

La primera versión utiliza una arquitectura por capas:

```text
Controller → Service → Repository → PostgreSQL
```

Consulta [Arquitectura V1](Arquitectura-V1) para conocer las responsabilidades de cada capa y [Guía de desarrollo](Guia-de-Desarrollo) para el flujo de trabajo.

El comportamiento del producto se define en las [Especificaciones V1](Especificaciones-V1), mientras que el modelo persistente se explica en [Modelo de datos V1](Modelo-de-Datos-V1).
