# Estructura del proyecto

RutaSur inicia como un monolito modular con dos aplicaciones independientes:

```text
apps/
├── api/                         # Spring Boot
│   └── src/main/
│       ├── java/co/edu/corhuila/rutasur/
│       │   ├── api/             # contratos HTTP y anotaciones OpenAPI
│       │   ├── controller/      # adaptadores HTTP REST
│       │   ├── service/         # lógica de negocio y casos de uso
│       │   ├── repository/      # acceso a datos con Spring Data JPA
│       │   ├── entity/          # mapeos JPA
│       │   ├── dto/             # contratos de entrada y salida
│       │   ├── mapper/          # conversiones entre DTO y entidad
│       │   ├── exception/       # errores y manejador global
│       │   └── config/          # configuración, incluida seguridad
│       └── resources/db/migration/ # migraciones Flyway
└── web/                         # Angular
    └── src/app/
        ├── core/                # singletons y configuración global
        ├── shared/              # elementos reutilizables y sin negocio
        ├── layout/              # composición visual de la aplicación
        └── features/            # funcionalidades de negocio con lazy loading
```

## Backend V1: arquitectura por capas

La primera versión usa capas globales y dependencias en una sola dirección:

```text
controller → service → repository → PostgreSQL
```

- `api/`: interfaces que definen rutas, contratos OpenAPI, respuestas y documentación de cada recurso REST.
- `controller/`: implementa una interfaz de `api/`, recibe HTTP y delega en servicios. No contiene reglas de negocio ni acceso directo a repositorios.
- `service/`: implementa los casos de uso y las reglas de negocio. Es la única capa que puede coordinar repositorios.
- `repository/`: interfaces Spring Data JPA para persistencia.
- `entity/`: entidades JPA que representan el modelo persistente.
- `dto/request` y `dto/response`: contratos HTTP. Nunca se exponen entidades JPA en la API.
- `mapper/`: transforma DTOs a entidades y entidades a DTOs mediante MapStruct.
- `exception/`: errores de negocio, `ManejadorGlobalExcepciones`, códigos de error y resolución internacionalizada de mensajes.
- `config/`: configuración transversal; `config/security` contendrá la seguridad de Spring.

No se usan interfaces de servicio en la V1 salvo que una necesidad concreta lo justifique. Los controladores inyectan las clases `@Service` directamente.

Las reglas detalladas de mapeo, contratos OpenAPI, consultas por proyección, `@Modifying`, transacciones, excepciones, complejidad y pruebas se definen en `docs/standards/backend-spring.md`.

## Frontend

- `core/`: servicios singleton, configuración, guards e interceptores HTTP.
- `shared/`: componentes, pipes, directivas, modelos y utilidades que no pertenecen a una funcionalidad concreta.
- `layout/`: shell de la aplicación, navegación, toolbar y sidebar.
- `features/`: una carpeta por funcionalidad; cada una puede tener `pages`, `components`, `data-access` y `models` propios.

No se crean funcionalidades de negocio vacías. Su nombre y límites nacen de una especificación aprobada.

Los componentes reutilizables, formularios, validadores, estilos, servicios, guards y pruebas Angular se rigen por `docs/standards/frontend-angular.md`.
