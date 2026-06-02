# ADR-014 — Backend Hosting: Fly.io

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |
| **Resolves** | Open question in ADR-005 |
| **Supersedes** | Initial draft (DigitalOcean) |

## Context

The Quarkus backend and PostgreSQL database require a hosting platform for staging and production. The platform must support containerised Java workloads, be available in EU regions (GDPR compliance — ADR-012), and fit a small team's operational capacity.

## Decision

**Fly.io** hosts the backend infrastructure for staging and production.

| Component | Fly.io service |
|---|---|
| Quarkus backend | Fly App (container-based, Dockerfile deploy) |
| PostgreSQL | Fly Postgres (managed cluster) |
| Nginx | Deployed as a Fly App alongside the backend |

**EU region**: `cdg` (Paris) or `ams` (Amsterdam) — to be confirmed at setup time.

### Why Fly.io fits Quarkus well

- Fly.io deploys any Docker container — no platform-specific build required.
- Quarkus produces a fast-starting, low-memory JVM or native binary that fits Fly's resource-efficient container model well.
- Fly's anycast networking and edge routing reduce latency for EU users.
- Native support for persistent volumes, private networking between apps, and WebSocket connections (required by ADR-008).

### Deployment pipeline

```
GitHub Actions
  → build Docker image (backend + Nginx)
  → fly deploy --app mooder-backend-stg   (on merge to develop)
  → fly deploy --app mooder-backend-prod  (on release tag)
```

Fly.io CLI (`flyctl`) is used in GitHub Actions. API tokens stored as GitHub Actions secrets.

### Managed PostgreSQL

Fly Postgres provides:
- Automated snapshots
- Automatic failover (HA cluster for prod)
- Private networking — database is not publicly accessible
- Encryption at rest (satisfies ADR-013)

## Consequences

- Closes the blocking open question from ADR-005 — staging can now be set up.
- EU data residency satisfied (Paris / Amsterdam).
- Fly.io's scale-to-zero capability keeps staging costs minimal.
- WebSocket support is native — no special proxy config needed beyond ADR-008 Nginx setup.
- A DPA with Fly.io must be signed before storing real user data (GDPR — ADR-012).

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| DigitalOcean | Initially considered; Fly.io is better suited for containerised Quarkus workloads and has lower ops overhead |
| AWS ECS / GCP Cloud Run | More powerful but significantly more complex for a small team |
| Railway / Render | Good options; Fly.io preferred for Quarkus ecosystem fit and EU region availability |
