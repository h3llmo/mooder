# ADR-001 — Frontend Technology Choices

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |

## Context

Mooder requires two client applications:
1. A **desktop/web** client for users on computers.
2. A **native mobile** client for iPhone and iPad users.

Technology choices needed to be made before scaffolding can begin.

## Decision

| Component | Technology chosen |
|---|---|
| `frontend-desktop` | **Next.js** (React framework) |
| `ios-app` | **Swift** (native iOS) |

## Consequences

**Next.js (desktop):**
- Server-side rendering and static generation available out of the box.
- Large ecosystem; straightforward API integration via `fetch` / React Query.
- Can be deployed as a web app and accessed from any desktop browser, removing the need for a native desktop installer for Phase 1.

**Swift (iOS):**
- Full access to iOS platform capabilities (notifications, biometrics, etc.).
- Best-in-class performance and UX on iPhone/iPad.
- Requires macOS + Xcode for development; separate build pipeline from the web frontend.

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| React Native (cross-platform) | Native Swift preferred for tighter iOS platform integration and better long-term control |
| Electron (desktop) | Next.js is lighter, deployable as a web app, and avoids the overhead of bundling a browser |
| Flutter | Team expertise and ecosystem maturity favour Next.js + Swift for this combination |
