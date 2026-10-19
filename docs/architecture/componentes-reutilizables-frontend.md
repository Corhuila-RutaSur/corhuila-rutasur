# Componentes reutilizables del frontend

## Objetivo

RutaSur construye su interfaz con componentes reutilizables para conservar una experiencia visual, accesible y funcionalmente consistente. Antes de crear un componente visual se debe revisar este catalogo y `docs/standards/frontend-angular.md`.

Un componente vive en `apps/web/src/app/shared/components/` cuando se reutiliza o se espera reutilizar en dos o mas funcionalidades. Si pertenece exclusivamente a una funcionalidad, permanece dentro de `features/<funcionalidad>/` hasta que exista una necesidad real de compartirlo.

## Catalogo inicial

| Componente | Selector | Uso |
| --- | --- | --- |
| Boton | `rs-boton` | Acciones de formularios, confirmaciones y navegacion. Variantes: `primario`, `secundario`, `texto` y `peligro`. |
| Campo de texto | `rs-campo-texto` | Campos basados en `FormControl`; muestra etiqueta y el primer error bajo el campo. |
| Panel | `rs-panel` | Agrupa contenido relacionado con un titulo opcional. |
| Banner | `rs-banner` | Muestra estados, advertencias, errores o mensajes destacados de una seccion. |
| Modal | `rs-modal` | Contenido para dialogos de confirmacion; su apertura se centralizara en un servicio al requerirse. |
| Icono o mascota | `rs-icono-mascota` | Representacion temporal y centralizada de iconografia o mascota hasta definir recursos oficiales. |

## Formularios y validacion

Los formularios usan Reactive Forms tipados. Los validadores propios se ubican en `shared/utils/validators/`; los mensajes se centralizan en `mensajes-validacion.ts`. Un campo no debe repetir textos de error en cada pantalla: `rs-campo-texto` los obtiene y los expone debajo del input cuando el usuario lo ha tocado o modificado.

## Notificaciones

`NotificacionService`, ubicado en `core/services/`, centraliza los toastr temporales. Los componentes y servicios de funcionalidad lo invocan, pero no crean mensajes flotantes por su cuenta. Ejemplo: `notificacionService.mostrar('Reserva creada correctamente.', 'exito')`.

## Estilos

Los tokens globales de RutaSur viven en `src/styles.scss`. Un componente debe encapsular sus estilos y consumir los tokens existentes. No se permite `::ng-deep`, estilos globales por funcionalidad, ni modificar clases internas de Angular Material desde una pantalla.

## Criterio de evolucion

Cuando una funcionalidad requiera una variante nueva, se debe ampliar el componente compartido si el comportamiento es generico. Si cambia una regla de negocio, la logica debe permanecer en la funcionalidad, no dentro de `shared`.

## Pruebas

Cada componente compartido debe tener pruebas cuando incorpora comportamiento. Como minimo se validan los mensajes de formularios, los eventos de salida, los estados deshabilitados y los elementos de accesibilidad relevantes. Los validadores se prueban de forma unitaria y no requieren levantar una pantalla completa.
