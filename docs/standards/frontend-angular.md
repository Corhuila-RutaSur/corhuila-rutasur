# Estándar de frontend Angular

## Propósito

Mantener una experiencia visual coherente, accesible y reutilizable en toda RutaSur. Este estándar se aplica a cualquier pantalla, componente, servicio, guard, formulario o estilo nuevo.

## Estructura y responsabilidad

```text
src/app/
├── core/                    # Elementos singleton y transversales
│   ├── config/
│   ├── guards/
│   ├── http/interceptors/
│   └── services/
├── shared/                  # UI y utilidades reutilizables, sin negocio
│   ├── components/
│   ├── directives/
│   ├── models/
│   ├── pipes/
│   └── utils/
├── layout/                  # Shell, navegación y composición visual global
└── features/                # Funcionalidades de negocio con lazy loading
    └── nombre-funcionalidad/
        ├── pages/
        ├── components/
        ├── data-access/
        └── models/
```

| Ubicación | Qué contiene | Qué no contiene |
| --- | --- | --- |
| `core/` | Configuración, sesión, autenticación, guards, interceptores, servicios singleton y notificaciones globales. | Componentes de negocio o servicios repetidos por funcionalidad. |
| `shared/` | Componentes visuales reutilizables, directivas, pipes, validadores y utilidades sin reglas de negocio. | Llamadas HTTP, estado de una funcionalidad o lógica de reservas. |
| `layout/` | Header, navegación, footer, menú y shell autenticado/público. | Casos de uso de una funcionalidad. |
| `features/` | Pantallas, componentes y acceso a datos de un dominio concreto. | Componentes genéricos que puedan usar otras funcionalidades. |

## Componentes reutilizables

El catalogo inicial y sus reglas de uso estan documentados en `docs/architecture/componentes-reutilizables-frontend.md`.

1. Todo elemento de interfaz reutilizable se crea en `shared/components/`.
2. Ejemplos: botones, campos de formulario, etiquetas, iconos, mascota, banners, modales, paneles, tablas reutilizables, estados vacíos, cargadores y componentes de notificación visual.
3. Los componentes de `shared/` reciben datos mediante `input` y emiten eventos mediante `output`; no llaman servicios HTTP ni conocen una funcionalidad de negocio.
4. Un componente específico de una pantalla permanece dentro de su `feature/`; no se mueve a `shared/` hasta que exista una segunda necesidad real de reutilización.
5. Los componentes de una funcionalidad usan los componentes compartidos para mantener el mismo aspecto. No se duplican botones, modales, banners ni mensajes de error con estilos propios.

## Formularios, inputs y validación

1. Los formularios nuevos usan Reactive Forms tipados.
2. Los validadores comunes y validadores personalizados puros viven en `shared/utils/validators/`.
3. Los mensajes de validación se centralizan por código de error en `shared/utils/validators/`; no se escriben mensajes dispersos en cada pantalla.
4. El componente reutilizable de campo o input recibe el control, etiqueta, tipo, ayuda y mensajes; muestra el error debajo del input con estilo consistente cuando el control está inválido y fue interactuado o enviado.
5. La validación del frontend mejora la experiencia, pero no reemplaza la validación del backend.
6. El frontend no interpreta ni traduce reglas de negocio complejas que el backend debe decidir.

## Diseño, estilos y accesibilidad

1. Los estilos globales se limitan a tokens, tema Angular Material, reset y reglas transversales en `src/styles.scss`.
2. Colores, espaciados, tipografía, elevación, radios y estados se consumen mediante tokens SCSS/CSS; no se repiten valores visuales arbitrarios en componentes.
3. Los estilos de una pantalla permanecen encapsulados en su componente y no usan `::ng-deep`.
4. No se modifica Angular Material con estilos globales agresivos o selectores que afecten componentes no relacionados.
5. Todo control interactivo debe tener texto o etiqueta accesible, foco visible, navegación por teclado y contraste suficiente.
6. Estados de carga, vacío, error y éxito se muestran mediante componentes reutilizables y mensajes claros.
7. Las imágenes e iconos decorativos usan texto alternativo apropiado o se marcan como decorativos.

## Servicios, HTTP y estado

1. Servicios singleton transversales viven en `core/services/`: sesión, autenticación, configuración, notificaciones y manejo global de errores.
2. Cada `feature` posee servicios de acceso a datos en `features/<nombre>/data-access/`; estos encapsulan llamadas HTTP y DTOs de esa funcionalidad.
3. Los componentes no construyen URLs ni consumen `HttpClient` directamente.
4. Las llamadas HTTP usan modelos/DTOs tipados; no se usa `any`.
5. Los componentes consumen flujos con `async` pipe o liberan suscripciones con `takeUntilDestroyed`.
6. Un interceptor central agrega autenticación a rutas protegidas y otro normaliza errores HTTP cuando corresponda.
7. Ningún token, refresh token o secreto se escribe en logs, plantillas, URLs o almacenamiento inseguro.

## Guards y rutas

1. Las funcionalidades se cargan de forma diferida cuando el flujo lo permita.
2. `authGuard` protege rutas autenticadas y conserva la URL de retorno para redirigir después del inicio de sesión.
3. `rolGuard` valida roles requeridos por la interfaz, por ejemplo administración u operación.
4. Los guards mejoran la navegación, pero no sustituyen autorización del backend.
5. Las rutas públicas de catálogo, experiencia y salida no requieren guard; reservas, perfil, pagos y operación sí.
6. Los guards redirigen a una página clara de inicio de sesión o acceso denegado; no dejan vistas parcialmente cargadas.

## Calidad y pruebas

1. Los validadores, pipes y utilidades se prueban como unidades puras con Vitest.
2. Los componentes compartidos deben probar sus entradas, salidas, accesibilidad básica y presentación de error.
3. Los servicios de acceso HTTP se prueban con las utilidades de pruebas de Angular, simulando respuestas exitosas y fallidas.
4. Los guards se prueban para usuario no autenticado, autenticado y sin rol cuando aplique.
5. Cada funcionalidad nueva cubre estado exitoso, carga, vacío, error y permiso denegado cuando corresponda.

## Nombres y documentación

- El código de negocio se escribe en español sin `ñ`, con nombres explícitos y sin abreviaturas ambiguas.
- Los componentes se nombran por intención: `BotonPrincipalComponent`, `CampoFormularioComponent`, `ModalConfirmacionComponent`.
- Las rutas usan minúsculas y guiones: `mis-reservas`, `detalle-experiencia`.
- Los métodos escritos manualmente incluyen JSDoc cuando su propósito, parámetros o efectos no sean evidentes.
- Se aplican las reglas de actualización documental definidas en `docs/specs/012-gobierno-documentacion.md`.
