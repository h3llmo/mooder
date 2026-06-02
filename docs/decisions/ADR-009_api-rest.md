# ADR-009 — API Design: REST

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |

## Context

All three clients (Next.js BFF, iOS app) communicate with the Quarkus backend over HTTP. An API style must be chosen and applied consistently.

## Decision

The backend exposes a **REST** API over HTTP/HTTPS.

- Resources are named with nouns, actions with HTTP verbs (GET, POST, PUT, PATCH, DELETE).
- Responses use standard HTTP status codes.
- Payloads are JSON.
- API is versioned via URL prefix: `/api/v1/`, `/api/v2/`, etc. — version increment aligns with the major version digit (see ADR-017).
- OpenAPI 3.x specification is generated from the backend code and committed to the repository as the canonical contract.

## Consequences

- Universal tooling support — every HTTP client in every language can consume the API.
- The OpenAPI spec serves as the source of truth for client generation and documentation.
- REST is stateless — scales horizontally without session affinity for HTTP calls (WebSocket handled separately per ADR-008).

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| GraphQL | Adds complexity (schema, resolvers, N+1 problem) not justified for Phase 1; REST is sufficient and better understood across the team |
| gRPC | Better for internal service-to-service; poor native browser support; iOS setup is heavier |
