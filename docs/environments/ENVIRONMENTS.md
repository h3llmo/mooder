# Mooder — Environment Definitions

Mooder operates across three environments. Each has a distinct purpose, audience, data policy, and deployment trigger.

---

## Overview

| | Local | Staging (stg) | Production (prod) |
|---|---|---|---|
| **Purpose** | Feature development & debugging | Integration, QA & pre-release validation | Live service for end users |
| **Audience** | Individual developer | Dev team + QA | Real users |
| **Data** | Synthetic / personal dev data | Anonymised or synthetic only | Real user data |
| **Uptime expectation** | None — on demand | Best effort | High availability |
| **Deployment trigger** | Manual (developer) | Merge to `develop` | Tagged release from `main` |
| **Access** | Developer's machine | Team members only | Public (authenticated users) |
| **Secrets** | Local `.env` file (never committed) | CI/CD secret store | CI/CD secret store |
| **Identity Provider** | Keycloak (Docker, local) | Auth0 (stg tenant) | Auth0 (prod tenant) |
| **Impersonation** | ✅ On by default | ⚠️ Opt-in, dual-condition gate | 🚫 Hard-disabled (build + runtime + CI) |

---

## Local environment

### Purpose
The local environment is where all day-to-day development happens. **Developer autonomy is a top priority** — the only prerequisite is Docker.

### Single command startup

```bash
docker compose up
```

This starts the complete stack. **Nginx is the single entry point** — all other containers are on an internal Docker network:

| Service | Container | Default route |
|---|---|---|
| **Nginx** | `mooder-nginx` | `http://localhost` (entry point for everything) |
| Backend (Quarkus) | `mooder-backend` | `http://localhost/api/` · `/q/health` · `/q/metrics` |
| PostgreSQL | `mooder-db` | internal only |
| Frontend Desktop (Next.js) | `mooder-desktop` | `http://localhost/` |
| Identity Provider (Keycloak) | `mooder-keycloak` | `http://localhost/auth/` |
| **Prometheus** | `mooder-prometheus` | internal — scrapes `/q/metrics` |
| **Grafana** | `mooder-grafana` | `http://localhost:3001` (dashboards, pre-seeded) |

**Secrets, certificates, and credentials** are provided by Docker Desktop on the fly — no manual setup required. Developers never handle raw secret files.

A `docker-compose.override.yml` (git-ignored) is available for personal local tweaks.
The iOS app (Xcode Simulator) is the only component that still requires macOS + Xcode; it points at `http://localhost/api/`.

### Rules
- **Never point local config at staging or production databases.**
- Use only synthetic or personal test data locally.
- Local environment variables live in `.env` files at each component root — **git-ignored**.
- A `.env.example` file (committed) documents every required variable with a placeholder value.

### Mock data
The local stack is **automatically seeded at startup** from `mock-data/resources/`. No manual data entry needed. See [`mock-data/README.md`](../../mock-data/README.md) for conventions.
To reset to a clean state: `docker compose down -v && docker compose up`.

---

## Staging environment (stg)

### Purpose
Staging is a shared, persistent environment that mirrors the production topology. It is used to:
- Validate features before they reach real users.
- Run integration and end-to-end tests.
- Demo new functionality to stakeholders.

### Hosting
| Component | Platform |
|---|---|
| Frontend (Next.js) | Vercel — stg deployment |
| Backend (Quarkus) | Fly.io — stg app |
| PostgreSQL | Fly Postgres — stg cluster |
| Identity Provider | Auth0 — dedicated stg tenant |

### Rules
- **No real user data.** Only anonymised or purpose-built synthetic datasets.
- All team members may access staging for testing purposes.
- Staging is deployed automatically when a branch is merged into `develop`.
- Staging can be reset / reseeded at any time without notice.
- Credentials and API keys are separate from production — never share them.

### Deployment trigger
```
merge to develop  →  GitHub Actions  →  deploy to stg
                                     →  Vercel preview auto-deploy (frontend)
```

---

## Production environment (prod)

### Purpose
Production is the live environment serving real users. Stability, security, and data integrity are the top priorities.

### Hosting
| Component | Platform |
|---|---|
| Frontend (Next.js) | Vercel — prod deployment |
| Backend (Quarkus) | Fly.io — prod app |
| PostgreSQL | Fly Postgres — prod HA cluster |
| Identity Provider | Auth0 — dedicated prod tenant |

### Rules
- **Only tagged releases are deployed to production.** No hotfix exceptions without a review.
- Real user data is present — data privacy and security requirements apply in full.
- Access is restricted: only authorised personnel may connect directly to production infrastructure.
- All changes must have passed in staging before promotion.
- Database migrations must be reviewed and tested in staging before running in production.

### Deployment trigger
```
git tag vX.Y.Z on main  →  GitHub Actions  →  deploy to prod
                                            →  Vercel prod deploy (frontend)
```

---

## CI environment (automated tests)

The CI pipeline (triggered on every pull request) runs in an ephemeral environment:
- A fresh database is spun up for each run and destroyed afterwards.
- No persistent data.
- Validates that the build passes and all automated tests are green before a PR can be merged.

---

## Environment variable conventions

Every component follows the same pattern:

| File | Purpose | Committed? |
|---|---|---|
| `.env.example` | Documents all required variables with placeholder values | ✅ Yes |
| `.env.local` | Developer's actual local values | ❌ No (git-ignored) |
| `.env.stg` | Staging values (injected by CI/CD) | ❌ No |
| `.env.prod` | Production values (injected by CI/CD) | ❌ No |

> **Rule:** secrets never touch the repository. When in doubt, add the variable name to `.env.example` and the actual value to the CI/CD secret store.

---

## Promotion flow

```
Local  ──(PR merged)──►  Staging  ──(release tagged)──►  Production
         develop branch                   main branch
```

*See [CONTRIBUTING.md](../../CONTRIBUTING.md) for branching rules and [ADR-002](../decisions/ADR-002_environment-strategy.md) for the rationale behind this strategy.*
