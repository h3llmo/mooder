# ADR-010 — Database Migrations: Flyway

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |

## Context

The PostgreSQL schema will evolve with every feature. A migration tool is needed to version, apply, and track schema changes consistently across all environments.

## Decision

**Flyway** manages all database schema migrations.

- Migration scripts are SQL files stored in `src/backend/src/main/resources/db/migration/`.
- Naming convention: `V{version}__{description}.sql` (e.g. `V1__create_users_table.sql`).
- Flyway runs automatically on backend startup — the schema is always in sync with the deployed code.
- Migrations are **append-only** — existing scripts are never modified after they have been applied to any environment.
- Rollback scripts (`U{version}__description.sql`) are written alongside forward migrations for critical changes.

## Environment behaviour

| Environment | Flyway mode |
|---|---|
| Local | Auto-migrate on `docker compose up` |
| Staging | Auto-migrate on deployment |
| Production | Auto-migrate on deployment — migrations must be reviewed and tested in staging first |

## Consequences

- Schema history is version-controlled alongside application code — every deployment is reproducible.
- Flyway's checksum validation prevents silent drift between environments.
- Developers must never run manual SQL against shared environments.

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| Liquibase | Both are strong choices; Flyway is simpler, SQL-native, and integrates natively with Quarkus |
| Manual migrations | Not reproducible; error-prone across environments |
