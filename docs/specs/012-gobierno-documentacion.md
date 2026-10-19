# 012 — Gobierno de la documentación

## Objetivo

Mantener la documentación como parte del producto: actual, verificable y útil para personas, herramientas y agentes de inteligencia artificial.

## Alcance

- Definir qué documentos deben cambiar junto con un cambio funcional o técnico.
- Establecer el `README.md` como primera fuente de orientación del proyecto.
- Mantener alineadas las especificaciones, modelo de datos, migraciones, API y Wiki.

## Reglas de negocio documental

1. Toda modificación de flujo, regla de negocio, estado, actor, permiso o criterio de aceptación debe actualizar su especificación en `docs/specs/` dentro del mismo cambio.
2. Todo cambio en tablas, columnas, relaciones, restricciones o datos semilla debe actualizar la migración correspondiente y el modelo/documentación de datos.
3. Todo endpoint nuevo o cambio de contrato debe reflejarse en OpenAPI y, cuando afecte el uso del sistema, en la especificación funcional.
4. El `README.md` debe reflejar el propósito, estado, arquitectura resumida, estructura, documentación principal, requisitos y forma vigente de ejecutar el proyecto.
5. Si un cambio modifica cualquiera de esos elementos del README, se debe actualizar en el mismo pull request.
6. La Wiki replica información de consulta; los archivos en `docs/` son la fuente versionada de referencia.
7. Ninguna documentación debe contener secretos, contraseñas, JWT, refresh tokens ni datos personales reales.

## Criterios de aceptación

```gherkin
Escenario: Cambio de flujo de reserva
Dado que se modifica una regla o paso del flujo de reserva
Cuando se prepara el cambio para revisión
Entonces la especificación 004 y los documentos relacionados quedan actualizados en el mismo pull request.

Escenario: Cambio visible en el proyecto
Dado que cambia la forma de ejecutar el proyecto, su arquitectura o estado general
Cuando se prepara el cambio para revisión
Entonces el README refleja esa nueva información antes de integrar el cambio.

Escenario: Cambio de base de datos
Dado que se agrega o modifica una estructura persistente
Cuando se crea la migración
Entonces sus tablas y columnas incluyen documentación PostgreSQL y el MER se mantiene alineado.
```

## Fuera de alcance

- Exigir que cambios puramente internos, sin efecto en comportamiento ni operación, alteren el README.
- Publicar automáticamente la Wiki; la publicación sigue el flujo de Git del repositorio de Wiki.
