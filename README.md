# CNM Backend

API Spring Boot (Java 21) del Club Nicaragüense de Montañismo: catálogo de viajes,
reservas con comprobante, historial, notificaciones y panel administrativo.

## Puesta en marcha (out-of-box)

**Requisitos:** Java 21, PostgreSQL 14+ corriendo local (puerto 5432), y para el
frontend: Node 20+.

### 1. Base de datos

Crea la base `cnm` (una vez):

```bash
psql -U postgres -c "CREATE DATABASE cnm;"
```

> Si tu usuario local no es `postgres`, ajusta usuario/contraseña en el paso 2 y en
> los comandos `psql` (usa `-h localhost` en lugar de `-h /tmp` si aplica).

### 2. Variables de entorno

```bash
cp .env.example .env
```

Edita `.env`: contraseña de tu Postgres y un `JWT_SECRET` tuyo (`openssl rand -base64 32`).
El resto del ejemplo sirve para Postgres local tal cual.

### 3. Arrancar el backend

```bash
./mvnw spring-boot:run
```

- Puerto: `http://localhost:8080`.
- **Flyway migra el esquema automáticamente** al arrancar (`V1__create_schema.sql`,
  `V2__viaje_imagen_equipo.sql`).
- CORS: acepta cualquier origen por defecto (funciona con cualquier puerto de
  `next dev`). Para producción define `CORS_ORIGINS` en `.env`.

### 4. Datos de desarrollo (admin + usuario + viajes + reservas)

**Opcional y reproducible**: `seed-dev.sql` es idempotente (hace TRUNCATE + reinserta),
así que puedes ejecutarlo **cuando quieras y las veces que quieras**, en cualquier
laptop, sin importar el estado de la BD:

```bash
psql -U postgres -d cnm -f seed-dev.sql
```

Esto crea (entre otras cosas) las cuentas demo:

| Cuenta | Correo | Contraseña | Rol |
|---|---|---|---|
| Admin | `admin@cnmontanismo.com` | `Admin1234` | administrador |
| Cliente | `carlos.gonzalez@example.com` | `Cliente1234` | cliente |
| Clientes extra | `ana.perez` / `luis.martinez` / `maria.torres` `@example.com` | `Cliente1234` | cliente |

Además: 10 viajes (con imagen y equipo), 7 preguntas de formulario, 13 reservas en
todos los estados, acompañantes, respuestas y notificaciones — suficiente para probar
catálogo, inscripción, bandeja admin y dashboard.

Si prefieres un admin/cliente con otros datos, inserta tus propios usuarios con
`seed-dev.sql` como plantilla (la contraseña va hasheada con BCrypt; genera el hash
con `openssl` no sirve: usa cualquier generador BCrypt, p. ej.
`htpasswd -bnBC 10 "" 'tu-clave' | tr -d ':\n'`).

### 5. Frontend

Ver el README de `cnm-frontend` (no necesita `.env` en dev; el API default es
`http://localhost:8080`).

## Estructura

- `src/main/java/com/cnm/backend/controllers` — AuthController, ViajeController,
  ReservaController, UsuarioController, NotificacionController, EstadisticaController,
  CampoFormularioController.
- `src/main/resources/db/migration` — migraciones Flyway (esquema).
- `seed-dev.sql` — semilla de datos de desarrollo (no es migración).
- `.env.example` — plantilla de configuración (copiar a `.env`).

## Notas de seguridad del seed

`Admin1234` / `Cliente1234` son credenciales de desarrollo. Para cualquier entorno
compartido, cambia las contraseñas antes de exponer el servicio.
