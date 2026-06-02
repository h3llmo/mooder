# ADR-005 — Local Environment & Deployment Infrastructure

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |
| **Relates to** | ADR-002 (environment strategy), ADR-004 (IdP per environment) |

---

## Part 1 — Local environment: Docker Compose

### Context

Developer autonomy and fast onboarding are top priorities. Every developer must be able to run the full Mooder stack locally with zero environment-specific setup beyond installing Docker Desktop.

### Decision

> **The only prerequisite for local development is Docker Desktop.**

A single `docker compose up` at the repository root starts the complete local stack:

| Service | Container | Role |
|---|---|---|
| **Nginx** | `mooder-nginx` | Web server · reverse proxy · load balancer — single entry point |
| Backend (Quarkus) | `mooder-backend` | API service (internal network only) |
| PostgreSQL | `mooder-db` | Data store (internal network only) |
| Frontend Desktop (Next.js) | `mooder-desktop` | BFF + UI (internal network only) |
| Identity Provider (Keycloak) | `mooder-keycloak` | OIDC IdP (internal network only) |

**Nginx is the single entry point.** All traffic is routed through it; all other containers are on an internal Docker network, unreachable directly from the host.

**Secrets, certificates, and infrastructure credentials** are provided by Docker Desktop on the fly via `docker compose secrets` — no manual generation, no files to copy, no certificates to install. Developers get a fully working HTTPS-capable stack without touching any secret material.

A `docker-compose.override.yml` (git-ignored) is available for personal local tweaks.

Default local routes (all through Nginx on `localhost`):

| Route | Proxied to |
|---|---|
| `http://localhost/` | Frontend Desktop (Next.js) |
| `http://localhost/api/` | Backend (Quarkus) |
| `http://localhost/auth/` | Keycloak |

### Consequences

- Any developer with Docker Desktop installed is fully productive from day one — no runtimes, no DB installs, no certificate management.
- Nginx config is committed to the repo — routing rules are version-controlled and identical across all developers.
- Secrets never appear in committed files; Docker Desktop injects them at container startup.
- The iOS app (Xcode/Simulator) is the only exception — it needs macOS + Xcode but points at the local Docker stack via `http://localhost/api/`.
- Hot-reload for backend and Next.js must be enabled via Docker volume mounts.
- Keycloak realm config is committed as importable JSON and auto-loaded on first start.

---

## Part 2 — Deployment infrastructure

### Context

For staging and production environments, a lightweight, low-ops deployment stack is needed that fits a small team and scales as the product grows.

### Decision

| Concern | Tool |
|---|---|
| Source control & code review | **GitHub** |
| CI/CD pipelines | **GitHub Actions** |
| Frontend hosting (Next.js) | **Vercel** |
| Web server · reverse proxy · load balancer | **Nginx** (mirrors local setup) |
| Secrets & certificates (deployed) | Platform secret store (GitHub Actions secrets / Vercel env vars) |
| Backend hosting | ⚠️ To be decided (see open questions) |
| iOS distribution | App Store / TestFlight (standard Apple pipeline) |

#### Nginx in deployed environments
Nginx plays the same role in staging and production as it does locally — single entry point, reverse proxy, and load balancer in front of the Quarkus backend. This ensures the routing layer is consistent across all environments and the Nginx config is fully tested locally before reaching deployed environments.

TLS termination and certificate management in deployed environments is handled at the Nginx layer, provisioned by the hosting platform.

#### GitHub Actions responsibilities
- Run automated tests on every pull request.
- Build and push Docker images (backend + Nginx) on merge to `develop` (→ staging) and on release tag (→ production).
- Trigger Vercel deployments for the frontend (Vercel's GitHub integration handles this natively).
- Inject secrets from the GitHub Actions secret store into deployment pipelines.

#### Vercel responsibilities
- Host and serve the Next.js frontend.
- Preview deployments are automatically created for every pull request — useful for frontend review without a shared staging server.
- Environment variables (OIDC config, backend API URL) are managed in the Vercel project settings per environment.

### Open questions

- [ ] **Backend hosting platform** — options to evaluate: Railway, Render, Fly.io, AWS ECS, GCP Cloud Run. To be decided before US-010-b (set up staging) is started.
- [ ] **Database hosting** — managed PostgreSQL for stg/prod (e.g. Supabase, Railway, AWS RDS, Neon). To be decided alongside backend hosting.

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| Docker Compose for stg/prod | Adds ops overhead; managed platforms preferred above local |
| Self-hosted CI (Jenkins, GitLab) | GitHub Actions is sufficient and has zero infra to maintain |
| Netlify instead of Vercel | Vercel has first-class Next.js support (same team); preferred |
| Requiring local installs (Java, Node, Postgres) | Violates the "Docker Desktop only" autonomy principle |
| Different reverse proxy (Traefik, Caddy) | Nginx is widely understood, battle-tested, and consistent across local and deployed |
