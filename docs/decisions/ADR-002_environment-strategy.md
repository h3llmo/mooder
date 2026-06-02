# ADR-002 — Environment Strategy

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |

## Context

Mooder spans three distinct components (backend, desktop, iOS) and will eventually serve real users with sensitive personal data. A clear environment strategy is needed from the outset to:

- Protect real user data from development activity.
- Provide a safe space for integration testing before any release.
- Define a repeatable, auditable promotion path from code to production.

## Decision

Three permanent environments are defined:

| Environment | Branch / trigger | Data policy |
|---|---|---|
| **Local** | Developer machine, on demand | Synthetic only |
| **Staging** | Auto-deploy on merge to `develop` | Anonymised / synthetic only |
| **Production** | Deploy on `git tag vX.Y.Z` from `main` | Real user data |

A fourth ephemeral environment (CI) is created per pull request for automated testing and destroyed afterwards.

Secrets are never stored in the repository. `.env.example` files document required variables; actual values are injected by the CI/CD secret store for stg/prod, and by the developer's local `.env` file for local.

## Consequences

- Developers can iterate freely locally without risk to shared data.
- Staging acts as a mandatory gate: nothing reaches production that hasn't been validated in stg first.
- The promotion flow (`local → stg → prod`) is simple and unambiguous.
- iOS builds add a slight complexity: simulator runs locally, but TestFlight / App Store distribution maps to the stg/prod pipeline — to be detailed when the iOS build pipeline is set up.

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| Two environments only (local + prod) | No safe integration testing space; too risky with real user data |
| Four environments (dev + stg + uat + prod) | Overhead not justified at current team size; can be revisited at scale |
| Feature-branch environments | Nice-to-have; can be added later as a CI enhancement |
