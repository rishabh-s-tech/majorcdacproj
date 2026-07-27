# --- Build stage ---
FROM node:22-alpine AS build
WORKDIR /app

# Install deps first so they're cached unless package*.json actually changes
COPY package.json package-lock.json ./
RUN npm ci

COPY . .

# Baked into the JS bundle at build time (Vite env vars are compile-time, not runtime).
# Override with --build-arg VITE_API_BASE_URL=... for each deployment target.
ARG VITE_API_BASE_URL=http://localhost:8080/api
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

RUN npm run build

# --- Runtime stage ---
FROM nginx:1.31-alpine AS runtime

# Force-patch OS packages at build time - don't just trust whatever was baked into
# the base image tag when it was last pushed upstream. This is what actually fixes
# CVEs like CVE-2026-31789 (OpenSSL/libcrypto3, CRITICAL) that Trivy's build gate
# catches: the base image tag itself lags behind Alpine's own patched package repo.
RUN apk update && apk upgrade --no-cache

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -qO- http://localhost:80/ >/dev/null 2>&1 || exit 1

CMD ["nginx", "-g", "daemon off;"]
