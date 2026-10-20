# Backend RutaSur

API de RutaSur construida con Spring Boot, PostgreSQL y Flyway.

## Ejecución local

1. Cree una base de datos PostgreSQL llamada `rutasur`.
2. Configure `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` (o sus valores equivalentes en `application.yaml`).
3. Ejecute `mvnw.cmd spring-boot:run` desde esta carpeta.

Flyway aplicará las migraciones versionadas al iniciar la aplicación. No cree las tablas manualmente.

## Administrador inicial académico

La migración `V6__seed_initial_administrator.sql` crea una cuenta local para poder acceder al panel administrativo después de una instalación nueva:

- Correo: `admin@rutasur.local`
- Contraseña: `RutaSurAdmin2026!`

La contraseña se guarda como hash BCrypt con factor de costo 12; no se almacena en texto plano. Esta cuenta existe únicamente para el contexto académico/local y debe sustituirse o cambiarse antes de un despliegue real.

## Documentación de API

- Swagger UI: `http://localhost:8090/swagger-ui/index.html`
- Scalar: `http://localhost:8090/scalar`

