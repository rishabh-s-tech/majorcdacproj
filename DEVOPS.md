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

Five independent, free-tier GitHub-native tools. Between them they cover the full
spectrum: dependency risk, source-level bugs, secrets, container CVEs, and - the only
"shift-right" check in the list - the live running app itself.

| Tool | Type | File | What it catches |
|---|---|---|---|
| Dependabot | SCA | `.github/dependabot.yml` | Vulnerable/outdated dependencies in `pom.xml`, `package.json`, Dockerfiles, and the GitHub Actions themselves. Opens PRs automatically. |
| CodeQL | SAST | `.github/workflows/codeql.yml` | Static analysis across the Java and JS/JSX source for real code-level vulnerability patterns (injection, unsafe deserialization, etc). Also runs weekly on a schedule. |
| gitleaks | Secrets | `.github/workflows/gitleaks.yml` | Scans the full commit history for accidentally committed secrets (API keys, passwords, private keys). |
| Trivy | Container | `.github/workflows/trivy.yml` | Scans the built backend/frontend Docker images for known OS and library CVEs. Runs twice: once to report everything (CRITICAL+HIGH, always uploaded to the Security tab) and once as a **gate** that fails the build only on CRITICAL findings with an available fix (`ignore-unfixed: true` - you can't block on a CVE nobody has patched yet). |
| OWASP ZAP | DAST | `.github/workflows/dast-zap.yml` | Runs a baseline scan against the **live deployed frontend**, probing the running app for real vulnerabilities (missing security headers, cookie flags, reflected XSS patterns) the way an external attacker would. Currently report-only (`fail_action: false`); files/updates a GitHub issue with findings. Runs weekly plus on manual trigger. |

Requires one repo variable to be set for ZAP to know what to scan: **Settings → Secrets
and variables → Actions → Variables tab** → add `DAST_TARGET_URL` =
`https://independent-dedication-production.up.railway.app` (the live frontend URL).

All static findings (Dependabot, CodeQL, gitleaks, Trivy) land in **GitHub → your repo →
Security → Code scanning alerts**. ZAP's findings land as a GitHub issue instead, since
that's what the action supports.

## 4. Secrets

Nothing sensitive is hardcoded. Locally, secrets live in a git-ignored `.env` (see
`.env.example` for the full list). In CI, the `backend` job uses throwaway values scoped
only to that ephemeral test run. For a real deployment, the equivalent values
(`JWT_SECRET`, `DB_PASSWORD`, `APP_ADMIN_PASSWORD`, etc.) should be stored as **GitHub
Actions secrets** (Settings → Secrets and variables → Actions) and injected at deploy
time - never committed.

## 5. Deployment (Railway)

The app is deployed on [Railway](https://railway.com) as three services inside one project:

| Service | Role | URL |
|---|---|---|
| Backend | Spring Boot API (Docker) | https://majorcdacproj-production.up.railway.app |
| Frontend | React SPA served by Nginx (Docker) | https://independent-dedication-production.up.railway.app |
| MySQL | Managed database plugin | internal only, not publicly exposed |

Each service builds from the same GitHub repo (`rishabh-s-tech/majorcdacproj`) using its
own **Root Directory** setting so Railway picks the right Dockerfile:

- Backend service → Root Directory `back/greennest-backend-new`
- Frontend service → Root Directory `front/greennest-frontend-new`, with
  `VITE_API_BASE_URL` set as a build-time variable (Railway passes matching service
  Variables into Docker `ARG`s automatically) pointing at the backend's public URL
  plus `/api`.

Railway's managed MySQL exposes its own auto-generated credentials as variables
(`MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD`). The backend
service references these directly rather than duplicating the values, e.g.:

```
DB_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}
DB_USERNAME=${{MySQL.MYSQLUSER}}
DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}
```

`JWT_SECRET` and `APP_ADMIN_PASSWORD` are separate, real values set directly on the
backend service (distinct from the ones used locally/in CI) and are never committed to
the repo. `APP_CORS_ALLOWED_ORIGIN` on the backend is set to the frontend's public
Railway URL so the browser's CORS check passes when the deployed frontend calls the
deployed backend.

Both services deploy automatically on every push to the connected branch (Railway
watches the GitHub repo directly), separate from and in addition to the CI checks in
section 2, which still gate correctness/security on every push/PR.

## 6. Branch protection

To make the CI/security gates actually mean something, `main` should require them to
pass before a merge is even possible - otherwise all this tooling is advisory only.
Set this up once via **GitHub → repo → Settings → Branches → Add branch protection
rule**:

- Branch name pattern: `main`
- Enable **Require a pull request before merging** (blocks direct pushes)
- Enable **Require status checks to pass before merging**, and select the `backend`,
  `frontend`, and `docker-build-check` jobs from `ci.yml`, plus the Trivy gate job from
  `trivy.yml`
- Optionally enable **Require approvals** (1) if you want a review step in the story too

After this, a PR with a failing test, a lint error, or a CRITICAL CVE with a known fix
physically cannot be merged to `main` - the pipeline becomes a real gate, not just a
report.

## 7. Not done yet / next phases

- **Monitoring/observability** (Prometheus/Grafana, or Railway's built-in metrics) -
  optional stretch goal.
- **Custom domain** - Railway currently serves both services on `*.up.railway.app`
  subdomains; a custom domain can be attached later via Settings → Networking.
- **Infrastructure as code** - the Railway project is currently configured by hand
  through its dashboard; a `railway.toml`/Terraform setup would make the infra
  reproducible from a file instead of manual clicks.
