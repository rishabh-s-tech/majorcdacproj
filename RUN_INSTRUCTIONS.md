# GreenNest Run Instructions

## Backend

1. Create a MySQL database named `greennest`.
2. From `back/greennest-backend-new`, run:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

### Environment variables

```text
DB_URL=jdbc:mysql://localhost:3306/greennest
DB_USERNAME=root
DB_PASSWORD=root
DDL_AUTO=update
SHOW_SQL=false
APP_CORS_ALLOWED_ORIGIN=http://localhost:5173

# Required in any real deployment - do not use the built-in default outside local dev.
JWT_SECRET=replace-with-a-long-random-string
JWT_EXPIRATION_MS=86400000

# A single admin account is auto-created on first startup if no admin exists yet.
# There is no public admin-signup endpoint; log in as this account and use
# POST /api/users/register/admin (while authenticated as an admin) to create more.
APP_ADMIN_EMAIL=admin@greennest.com
APP_ADMIN_PASSWORD=ChangeMe123!
```

## Frontend

From `front/greennest-frontend-new`, run:

```bash
npm install
npm run dev
```

For a production build:

```bash
npm run build
```

The frontend API URL can be changed with:

```text
VITE_API_BASE_URL=http://localhost:8080/api
```

## Verified in this revision

- Frontend lint: passed
- Frontend production build: passed
- Backend: reviewed and edited by hand; **could not be compiled in this environment**
  because Maven Central and JDK downloads are network-blocked here and there is no
  local JDK 17 / Maven install available. Please run `./mvnw clean verify` locally
  to confirm before deploying - see CHANGES.md for exactly what changed.

See `CHANGES.md` for a full list of fixes made in this pass.
