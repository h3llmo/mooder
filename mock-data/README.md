# Mooder — Mock Data

This folder contains the **seed data** loaded automatically when the local environment starts (`docker compose up`).

---

## Purpose

- Provide a consistent, realistic dataset for local development and testing.
- Eliminate the need for developers to manually create test data.
- Act as a shared fixture library: when a new test scenario is needed, a new resource file is added here and committed.

## Structure

```
mock-data/
└── resources/
    ├── users/          ← One JSON file per user
    │   ├── user01.json
    │   ├── user02.json
    │   └── user03.json
    └── roles/          ← Role definitions
        └── roles.json
```

## Format

All files are plain **JSON**. The schema is intentionally flat and target-agnostic for now — some fields will feed the IdP (Keycloak locally, Auth0 in deployed environments), others will feed the PostgreSQL database. The exact provisioning split will be defined in a future ADR once component schemas are finalised.

## Provisioning rule

> **On every `docker compose up`, the local stack is seeded from these files.**

If the data already exists (e.g. on a restart without volume wipe), the seed operation is idempotent — it does not duplicate records.
To reset to a clean state: `docker compose down -v && docker compose up`.

## Adding new mock data

1. Create or update a JSON file under the appropriate `resources/` subfolder.
2. Follow the existing naming convention (`user04.json`, etc.).
3. Commit the file — mock data is version-controlled so all developers share the same fixtures.
4. If a new resource type is needed (e.g. `conversations/`), create a new subfolder and document it here.

## Sensitive data policy

Mock data contains **fictional personal data only**. Never commit real names, real email addresses, real phone numbers, or any data derived from real individuals.
