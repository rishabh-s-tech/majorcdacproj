# DevOps & Security Pipeline

This document describes the CI/CD and security tooling added on top of the GreenNest
application, and how to run/inspect each piece.

## 1. Local containers (Docker Compose)

```bash
cp .env.example .env      # fill in real values - especially JWT_SECRET and the admin password
docker compose up --build
```

- Frontend: http://localhost:5173
- Backend: http://localhost:8080/api
- MySQL: internal only (not exposed to the host) - the app talks to it over the
  compose-internal `greennest` network at `mysql:3306`.

Both `Dockerfile`s are multi-stage: a build stage (Maven / npm) that never ships in the
final image, and a slim runtime stage (`eclipse-temurin:17-jre-jammy` for the backend,
`nginx:1.27-alpine` for the frontend). The backend runs as a non-root user inside the
container. The frontend's Nginx config adds `X-Frame-Options`, `X-Content-Type-Options`,
and `Referrer-Policy` headers, and serves the SPA correctly (unknown routes fall back to
`index.html` for React Router).

`VITE_API_BASE_URL` is a **build-time** value (Vite bakes it into the JS bundle), so it's
passed as a Docker build arg, not a runtime env var - see `docker-compose.yml`.

## 2. Continuous Integration (`.github/workflows/ci.yml`)

Runs on every push/PR to `main` and `rishabh`:

- **backend** job: spins up a real MySQL 8 service container (needed because
  `GreennestBackendApplicationTests` uses `@SpringBootTest`, which boots the full Spring
  context including the datasource), runs `mvn test`, then packages the jar.
- **frontend** job: `npm ci`, `npm run lint`, `npm run build`.
- **docker-build-check** job: builds both Docker images (no push) to catch Dockerfile
  breakage early, only after both above jobs pass.

## 3. Security scanning

Four independent, free-tier GitHub-native tools, each uploading results to the repo's
**Security** tab (Code scanning alerts) where applicable:

| Tool | File | What it catches |
|---|---|---|
| Dependabot | `.github/dependabot.yml` | Vulnerable/outdated dependencies in `pom.xml`, `package.json`, Dockerfiles, and the GitHub Actions themselves. Opens PRs automatically. |
| CodeQL | `.github/workflows/codeql.yml` | Static analysis (SAST) across the Java and JS/JSX source for real code-level vulnerability patterns (injection, unsafe deserialization, etc). Also runs weekly on a schedule. |
| gitleaks | `.github/workflows/gitleaks.yml` | Scans the full commit history for accidentally committed secrets (API keys, passwords, private keys). |
| Trivy | `.github/workflows/trivy.yml` | Scans the built backend/frontend Docker images for known OS and library CVEs. Currently report-only (`exit-code: "0"`) so CI doesn't block on upstream base-image CVEs you can't immediately fix - flip to `"1"` once you're ready to gate on it. |

All results land in **GitHub → your repo → Security → Code scanning alerts**, in one place.

## 4. Secrets

Nothing sensitive is hardcoded. Locally, secrets live in a git-ignored `.env` (see
`.env.example` for the full list). In CI, the `backend` job uses throwaway values scoped
only to that ephemeral test run. For a real deployment, the equivalent values
(`JWT_SECRET`, `DB_PASSWORD`, `APP_ADMIN_PASSWORD`, etc.) should be stored as **GitHub
Actions secrets** (Settings → Secrets and variables → Actions) and injected at deploy
time - never committed.

## 5. Not done yet / next phases

- **CD (auto-deploy)**: no deployment target has been chosen yet - see chat for the
  options discussed (PaaS vs VM vs Kubernetes).
- **DAST** (e.g. OWASP ZAP baseline scan against a running instance) - deferred; can be
  added once there's a deployed environment to point it at.
- **Monitoring/observability** (Prometheus/Grafana) - optional stretch goal.
