# Estructura del proyecto

RutaSur inicia como un monolito modular con dos aplicaciones independientes:

```text
apps/
├── api/                         # Spring Boot
│   └── src/main/
│       ├── java/co/edu/corhuila/rutasur/
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

- `controller/`: recibe HTTP, valida DTOs de entrada y genera respuestas HTTP. No contiene reglas de negocio ni acceso directo a repositorios.
- `service/`: implementa los casos de uso y las reglas de negocio. Es la única capa que puede coordinar repositorios.
- `repository/`: interfaces Spring Data JPA para persistencia.
- `entity/`: entidades JPA que representan el modelo persistente.
- `dto/request` y `dto/response`: contratos HTTP. Nunca se exponen entidades JPA en la API.
- `mapper/`: transforma DTOs a entidades y entidades a DTOs.
- `exception/`: errores de negocio y manejo uniforme de errores HTTP.
- `config/`: configuración transversal; `config/security` contendrá la seguridad de Spring.

No se usan interfaces de servicio en la V1 salvo que una necesidad concreta lo justifique. Los controladores inyectan las clases `@Service` directamente.

## Frontend

- `core/`: servicios singleton, configuración, guards e interceptores HTTP.
- `shared/`: componentes, pipes, directivas, modelos y utilidades que no pertenecen a una funcionalidad concreta.
- `layout/`: shell de la aplicación, navegación, toolbar y sidebar.
- `features/`: una carpeta por funcionalidad; cada una puede tener `pages`, `components`, `data-access` y `models` propios.

No se crean funcionalidades de negocio vacías. Su nombre y límites nacen de una especificación aprobada.
