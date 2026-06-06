# Reto Tecnico Lite Thinking — Inventory (Full Stack)

Aplicacion para gestion de **empresas, productos (multimoneda) e inventario**, con autenticacion
**JWT**, roles **ADMIN / EXTERNO**, generacion de **PDF** del inventario y **envio por correo**.

**Stack:** Java 21 · Spring Boot 3 · Hibernate/JPA · PostgreSQL · Bean Validation · **Quasar + Vue 3** · Pinia · Axios.

```
reto-lite-thinking/
├── backend/            API REST (Spring Boot, arquitectura hexagonal)
├── frontend/           SPA (Quasar + Vue 3 + Pinia)
├── db/schema.sql       Esquema de referencia
└── docker-compose.yml  PostgreSQL + backend
```

---

## 1. Arquitectura

### Backend — Hexagonal (Ports & Adapters)

```
domain/          Entidades de negocio + excepciones (sin dependencias de framework)
application/     Casos de uso (services), DTOs, mappers y PUERTOS de salida (interfaces)
infrastructure/  ADAPTADORES: persistencia (JPA), web (REST), security (JWT), pdf, mail,
                 validation (validadores custom) y config
```

El `application` depende solo de **puertos** (`port/out/*`), nunca de Spring Data ni de detalles de
infraestructura. Los adaptadores (`*RepositoryAdapter`, `OpenPdfGeneratorAdapter`,
`SmtpEmailSenderAdapter`, `JwtService`) implementan esos puertos: se puede cambiar BD, motor de PDF o
proveedor de correo sin tocar la logica de negocio.

### Frontend — Quasar + Vue 3

```
src/boot/axios.js     Instancia Axios + interceptor que inyecta el JWT y maneja 401
src/router/           Rutas + guard global (autenticacion y autorizacion por rol)
src/stores/           Pinia: auth, empresas, productos, inventario
src/pages/            Login, Empresas, Productos, Inventario, 404
src/components/       Dialogos de formulario (Empresa / Producto multimoneda)
```

### Modelo de datos

`Empresa (1:N) Producto`, `Producto (N:M) Categoria`, `Producto (1:N) ProductoPrecio` (multimoneda),
`Cliente (1:N) Orden`, `Orden (N:M) Producto` (via `OrdenDetalle`). `Usuario` gestiona la autenticacion.

---

## 2. Requisitos

- Java 21 y Maven 3.9+
- Node 18+ (frontend)
- PostgreSQL 16 (local o vía Docker)
- Docker (opcional, para levantar todo en contenedores)

---

## 3. Configuracion de variables de entorno

El backend se configura por **variables de entorno**, con valores por defecto en `application.yml`
usando la sintaxis `${VARIABLE:valor-por-defecto}` (lee la variable; si no existe, usa el default).

- **En local / IDE:** las variables se cargan desde `backend/.env` mediante **spring-dotenv**
  (dependencia ya incluida en el `pom.xml`). El archivo `.env` debe estar en la raiz del backend
  (junto al `pom.xml`), **no** en `src/main/resources/`.
- **En Docker:** las inyecta `docker compose` (lee el `.env` de la raiz del proyecto).

> El `.env` **no se versiona** (esta en `.gitignore`). El repositorio incluye `backend/.env.example`
> como plantilla. Para empezar:
>
> ```bash
> cd backend
> cp .env.example .env     # luego edita .env con tus valores reales
> ```

Variables soportadas: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION_MS`,
`CORS_ALLOWED_ORIGINS`, `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`.

Para generar un `JWT_SECRET` propio (minimo 32 bytes): `openssl rand -hex 32`.

---

## 4. Ejecucion

### 4.1 Base de datos

Con Docker (solo Postgres):
```bash
docker compose up -d postgres
```

O con un PostgreSQL local, crea la base y el usuario que el backend espera por defecto:
```sql
CREATE USER inventory WITH PASSWORD 'inventory';
CREATE DATABASE inventory OWNER inventory;
GRANT ALL PRIVILEGES ON DATABASE inventory TO inventory;
```

### 4.2 Backend

```bash
cd backend
cp .env.example .env        # si aun no lo has hecho
mvn spring-boot:run         # API en http://localhost:8080
```

Las variables del `.env` se cargan automaticamente (spring-dotenv). Al arrancar, el `DataSeeder`
crea los usuarios y datos de ejemplo si la BD esta vacia.

### 4.3 Frontend

```bash
cd frontend
npm install
npm run dev                 # SPA en http://localhost:9000
```

El backend ya habilita CORS para `http://localhost:9000`. Si el backend no esta en
`http://localhost:8080`, define `API_URL` antes de `npm run dev` (ver `frontend/.env.example`).

### 4.4 Alternativa: todo con Docker

```bash
docker compose up --build   # PostgreSQL + API
```

---

## 5. Credenciales (seed automatico)

Al arrancar, si la BD esta vacia, se crean estos usuarios (contrasena con **hash BCrypt**):

| Rol      | Email                        | Contrasena    |
|----------|------------------------------|---------------|
| ADMIN    | admin@litethinking.com       | `Admin123*`   |
| EXTERNO  | externo@litethinking.com     | `Externo123*` |

La pantalla de login incluye botones para autocompletar estas credenciales.
Tambien se cargan empresas, categorias, productos (precios COP/USD/EUR) y una orden de ejemplo.

### Permisos por rol

| Funcion                         | ADMIN | EXTERNO |
|---------------------------------|:-----:|:-------:|
| Ver empresas                    |  Si   |   Si    |
| Crear/editar/eliminar empresas  |  Si   |   No    |
| Productos (CRUD)                |  Si   |   No    |
| Inventario + PDF + correo       |  Si   |   No    |

El control se aplica en **dos capas**: guard del router (frontend) y `@PreAuthorize` (backend).

---

## 6. API

Swagger UI: `http://localhost:8080/swagger-ui.html` (boton **Authorize** para el JWT).

| Metodo | Ruta                      | Rol             | Descripcion                          |
|--------|---------------------------|-----------------|--------------------------------------|
| POST   | `/api/auth/login`         | publico         | Login, retorna JWT                   |
| GET    | `/api/empresas`           | ADMIN, EXTERNO  | Lista empresas                       |
| POST   | `/api/empresas`           | ADMIN           | Crea empresa                         |
| PUT    | `/api/empresas/{nit}`     | ADMIN           | Actualiza empresa                    |
| DELETE | `/api/empresas/{nit}`     | ADMIN           | Elimina empresa                      |
| GET    | `/api/productos`          | ADMIN, EXTERNO  | Lista productos (`?empresaNit=`)     |
| POST   | `/api/productos`          | ADMIN           | Crea producto (precios multimoneda)  |
| PUT    | `/api/productos/{id}`     | ADMIN           | Actualiza producto                   |
| DELETE | `/api/productos/{id}`     | ADMIN           | Elimina producto                     |
| GET    | `/api/categorias`         | ADMIN, EXTERNO  | Lista categorias                     |
| POST   | `/api/categorias`         | ADMIN           | Crea categoria                       |
| GET    | `/api/inventario`         | ADMIN           | Inventario (productos por empresa)   |
| GET    | `/api/inventario/pdf`     | ADMIN           | Descarga el inventario en PDF        |
| POST   | `/api/inventario/enviar`  | ADMIN           | Envia el PDF a un correo             |

### Validaciones de entrada

Las peticiones se validan con Bean Validation. Destacado: el campo `moneda` de cada precio se valida
con una anotacion **`@ISO4217`** propia (`infrastructure/validation`), que comprueba contra
`java.util.Currency` que el codigo sea una moneda ISO 4217 valida (COP, USD, EUR…). Los errores de
validacion devuelven **400** con el detalle por campo.

---

## 7. Pruebas (backend)

```bash
cd backend && mvn test
```

- **Unitarias (Mockito):** `EmpresaServiceTest`, `ProductoServiceTest`, `AuthServiceTest`, `JwtServiceTest`.
- **Integracion (Testcontainers + PostgreSQL real):** `InventoryIntegrationTest`
  (se **omite automaticamente** si Docker no esta disponible).

---

## 8. Decisiones tecnicas

- **Multimoneda** como tabla `producto_precio` (1:N) en vez de columnas fijas → escalable a N monedas.
- **Validacion de moneda** con anotacion custom `@ISO4217` → reglas de dominio expresadas de forma
  declarativa y reutilizable.
- **Inventario** = vista derivada de productos por empresa (no se duplica estado en otra tabla).
- **PDF** con OpenPDF (sin dependencias nativas). **Correo** con Spring Mail (SMTP) via endpoint REST.
- **Seguridad** JWT *stateless* + BCrypt; autorizacion por rol con `@PreAuthorize` (`@EnableMethodSecurity`)
  en el backend y guards de router en el frontend.
- **Config por entorno:** `application.yml` parametrizado con `${VAR:default}`; los valores reales
  viven en `.env` (fuera de Git), cargados por spring-dotenv en local o inyectados por Docker/servidor.
- **Frontend** desacoplado: Axios centralizado con interceptor JWT, estado en Pinia, UI por componentes.
- `ddl-auto: update` para agilizar el reto; en produccion se recomienda **Flyway/Liquibase**
  (`db/schema.sql` queda como referencia).

---

## 9. Configuracion SMTP (envio del inventario)

Define `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM` en `backend/.env`.

- **Mailtrap (recomendado para pruebas):** sandbox que atrapa los correos sin enviarlos a bandejas
  reales. Toma los datos de *Email Testing → tu Inbox → SMTP Settings*:
  ```env
  MAIL_HOST=sandbox.smtp.mailtrap.io
  MAIL_PORT=587
  MAIL_USERNAME=tu-usuario-mailtrap
  MAIL_PASSWORD=tu-clave-mailtrap
  MAIL_FROM=no-reply@litethinking.com
  ```
- **Gmail (envio real):** requiere *App Password* (no la clave normal) y `MAIL_FROM` igual a la cuenta
  que autentica.

---

## 10. Pendiente / mejoras a futuro

- Migraciones versionadas con Flyway/Liquibase para produccion.
- Refresh tokens y paginacion en los listados.
- Mas cobertura de tests (controladores con MockMvc).
- Dockerizar el frontend (Nginx) para una demo 100% en contenedores.
