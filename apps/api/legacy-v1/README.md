# Backend RutaSur

API de RutaSur construida con Spring Boot, PostgreSQL y Flyway.

## Ejecución local

1. Cree una base de datos PostgreSQL llamada `rutasur`.
2. Configure `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` (o sus valores equivalentes en `application.yaml`).
3. Ejecute `mvn spring-boot:run` desde esta carpeta.

Flyway aplicará las migraciones versionadas al iniciar la aplicación. No cree las tablas manualmente.

## Administrador inicial académico

La migración `V6__seed_initial_administrator.sql` crea una cuenta local para poder acceder al panel administrativo
después de una instalación nueva:

- Correo: `admin@rutasur.local`
- Contraseña: `RutaSurAdmin2026!`

La contraseña se guarda como hash BCrypt con factor de costo 12; no se almacena en texto plano. Esta cuenta existe
únicamente para el contexto académico/local y debe sustituirse o cambiarse antes de un despliegue real.

## Documentación de API

- Swagger UI: `http://localhost:8090/swagger-ui/index.html`
- Scalar: `http://localhost:8090/scalar`

## Carga manual de una experiencia para pruebas

Inicie el legado desde este directorio con:

```powershell
mvn spring-boot:run
```

Luego abra Swagger UI en `http://localhost:8090/swagger-ui/index.html`. Use primero el endpoint `POST /api/v1/public/autenticacion/login` con la cuenta académica y copie `data.accessToken` de la respuesta. En **Authorize**, pegue el token como `Bearer <token>`.

En una base V1 nueva, las semillas crean `ASTROTURISMO` con id `1` y el administrador con id `1`. Si la base ya contiene datos, consulte esos ids en PostgreSQL antes de continuar:

```sql
select id, nombre from tipo_experiencia order by id;
select id, correo from usuario order by id;
```

### 1. Crear experiencia

Ejecute `POST /api/v1/experiencias`:

```json
{
  "tipoExperienciaId": 1,
  "creadoPorUsuarioId": 1,
  "nombre": "Noche de estrellas en la Tatacoa",
  "descripcion": "Observación astronómica y caminata nocturna guiada.",
  "destino": "Villavieja, Huila",
  "duracionHoras": 5,
  "recomendaciones": "Usar ropa cómoda, hidratación y linterna.",
  "imagenes": []
}
```

Guarde el valor `data.id` como `EXPERIENCIA_ID`.

### 2. Adjuntar imagen física opcional

Use `POST /api/v1/experiencias/{EXPERIENCIA_ID}/imagenes` como `multipart/form-data`. En Swagger cargue el archivo en el campo `archivo`, marque `principal=true` y asigne `orden=1`. El backend guarda el archivo en la carpeta configurada por `RUTASUR_UPLOADS_DIR` o, por defecto, en `RutaSurUploads` dentro del perfil del usuario.

### 3. Crear servicios configurables

Ejecute `POST /api/v1/experiencias/{EXPERIENCIA_ID}/servicios` una vez por cada servicio. Valores permitidos para `tipo`: `TRANSPORTE`, `ALOJAMIENTO`, `ACTIVIDAD` y `ALIMENTACION`.

```json
{
  "tipo": "TRANSPORTE",
  "nombre": "Transporte ida y regreso",
  "descripcion": "Traslado desde Neiva hasta el desierto de la Tatacoa."
}
```

```json
{
  "tipo": "ACTIVIDAD",
  "nombre": "Caminata y observación astronómica",
  "descripcion": "Actividad nocturna acompañada por guía."
}
```

Guarde los valores `data.id` como `SERVICIO_TRANSPORTE_ID` y `SERVICIO_ACTIVIDAD_ID`.

### 4. Crear una salida disponible

Ejecute `POST /api/v1/experiencias/{EXPERIENCIA_ID}/salidas`. Para reservar, el estado debe ser `ABIERTA`.

```json
{
  "fechaHoraInicio": "2026-12-15T18:00:00-05:00",
  "fechaHoraFin": "2026-12-15T23:00:00-05:00",
  "cuposTotales": 20,
  "estado": "ABIERTA",
  "observaciones": "Punto de encuentro: terminal de Neiva."
}
```

Guarde `data.id` como `SALIDA_ID`.

### 5. Habilitar los servicios de esa salida

Ejecute `POST /api/v1/salidas/{SALIDA_ID}/programaciones` por cada servicio. Esta programación define precio y cupos de cada opción para **esa salida**, no para todo el catálogo.

```json
{
  "servicioExperienciaId": 1,
  "precioUnitario": 80000.00,
  "cuposTotales": 20
}
```

Reemplace `servicioExperienciaId` por el identificador real recibido en el paso 3. Guarde cada `data.id` de programación: esos identificadores son los que una reserva selecciona.

### Verificación pública

Sin token, pruebe estos endpoints:

```text
GET /api/v1/public/experiencias
GET /api/v1/public/experiencias/{EXPERIENCIA_ID}
GET /api/v1/public/experiencias/{EXPERIENCIA_ID}/servicios
GET /api/v1/public/experiencias/{EXPERIENCIA_ID}/salidas
GET /api/v1/public/salidas/{SALIDA_ID}/programaciones
```

Una reserva siempre selecciona identificadores de **programaciones**, no ids de servicios directos. Todos los participantes de una reserva reciben las mismas programaciones seleccionadas.

