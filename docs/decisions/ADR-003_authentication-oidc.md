# ADR-003 — Authentication Strategy: OIDC

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |

## Context

Mooder handles personal and potentially sensitive user data. A robust, standardised authentication mechanism is required across all three clients (backend, desktop, iOS). The solution must:

- Work consistently across the backend (Quarkus), Next.js desktop, and Swift iOS clients.
- Be secure by default and not require building auth logic from scratch.
- Support future extensibility (social login, MFA, enterprise SSO) without rearchitecting.
- Fit the phased roadmap — Phase 1 needs basic user authentication; later phases may need delegated access between components.

## Decision

Authentication is based on **OpenID Connect (OIDC)**.

- The **backend** acts as the resource server — it validates tokens on every protected request.
- An **OIDC-compliant identity provider (IdP)** handles user identity, login flows, and token issuance. The specific IdP (e.g. Keycloak self-hosted, Auth0, Okta) is a separate decision to be made before US-010 is refined.
- The **desktop (Next.js)** and **iOS (Swift)** clients are OIDC relying parties — they initiate the auth flow and receive tokens.
- All inter-component communication uses short-lived **JWT access tokens** issued by the IdP.

## Consequences

- Auth logic is centralised in the IdP, not duplicated across clients.
- Standard OIDC libraries exist for Quarkus, Next.js, and Swift — no custom auth code needed.
- MFA, social login, and SSO can be added at the IdP level without touching application code.
- A self-hosted IdP (e.g. Keycloak) adds an infrastructure component to manage; a managed IdP (e.g. Auth0) reduces ops burden but adds external dependency.
- Token refresh and session management must be handled correctly in both frontend clients.

## Open questions

- [x] Self-hosted IdP vs managed IdP? → **Resolved: Auth0 (stg/prod), Keycloak (local)** — see ADR-004.
- [ ] Token storage strategy in the iOS app (Keychain) and Next.js (httpOnly cookie vs memory)? → Addressed by BFF pattern — see ADR-004.

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| Custom username/password + JWT hand-rolled | Reinventing auth is a security risk; no extensibility path |
| OAuth 2.0 only (no OIDC) | OIDC adds the identity layer (ID token, userinfo) needed for a user-centric product like Mooder |
| Session-based auth (server sessions) | Does not scale across multiple stateless backend instances; incompatible with mobile clients |
