# ADR-017 — Versioning Strategy

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |

## Context

Mooder has three independently deployable components (backend, frontend-desktop, ios-app). A versioning strategy is needed that conveys compatibility, allows independent evolution, and is easy to reason about.

## Decision

### Semantic versioning

All components use **Semantic Versioning**: `MAJOR.MINOR.PATCH`

| Digit | Incremented when |
|---|---|
| **MAJOR** | A non-backward-compatible (breaking) change is introduced |
| **MINOR** | New functionality added in a backward-compatible manner |
| **PATCH** | Backward-compatible bug fixes |

### Cross-component versioning rule

> **The MAJOR digit is shared across all components.**
> MINOR and PATCH digits are independent per component.

| Component | Version example | Meaning |
|---|---|---|
| Backend | `2.4.1` | Major 2, minor 4, patch 1 |
| Frontend Desktop | `2.1.3` | Major 2 (matches backend), minor 1, patch 3 |
| iOS App | `2.0.7` | Major 2 (matches backend), minor 0, patch 7 |

**A MAJOR increment on any component means ALL components must be updated to the new MAJOR before the release ships.** A major version bump signals a platform-wide breaking change that requires coordinated release.

MINOR and PATCH increments can be released independently per component.

### API versioning

The REST API URL prefix mirrors the backend MAJOR version: `/api/v1/`, `/api/v2/`, etc. (see ADR-009).

### Release tagging (git)

Git tags follow `{component}/v{version}`:

| Tag | Meaning |
|---|---|
| `backend/v2.4.1` | Backend release 2.4.1 |
| `desktop/v2.1.3` | Desktop release 2.1.3 |
| `ios/v2.0.7` | iOS release 2.0.7 |

A coordinated major release is tagged on all three simultaneously.

### Changelog

`CHANGELOG.md` at the repository root tracks releases per component under a shared version history. Each entry notes which components changed.

## Consequences

- The shared MAJOR digit makes compatibility immediately visible: if a client's MAJOR differs from the backend's, it is not compatible.
- Independent MINOR/PATCH allows the iOS app to ship a bug fix without forcing a desktop release.
- Coordinated major releases require release management discipline — the team must align before incrementing MAJOR.

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| Fully independent versioning per component | Difficult to reason about cross-component compatibility |
| Single version for all components | Forces lockstep releases for every patch; slows delivery |
| Date-based versioning (CalVer) | Does not convey compatibility information |
