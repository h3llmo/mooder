# ADR-004 — Identity Provider per Environment & BFF Pattern

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |
| **Relates to** | ADR-003 (OIDC authentication strategy) |

---

## Part 1 — Identity Provider per environment

### Context

ADR-003 established OIDC as the authentication standard. The choice of IdP differs between local development and shared environments to balance developer autonomy, cost, and production parity.

### Decision

| Environment | Identity Provider |
|---|---|
| **Local** | **Keycloak** (self-hosted, runs as a Docker container) |
| **Staging** | **Auth0** (managed SaaS — dedicated stg tenant) |
| **Production** | **Auth0** (managed SaaS — dedicated prod tenant) |

Staging and production use **separate Auth0 tenants** — credentials and user bases are never shared between them.

### Consequences

- Developers can work fully offline with a local Keycloak instance. No Auth0 account or internet connection required for local dev.
- Keycloak and Auth0 are both OIDC-compliant: application code does not change between environments; only IdP configuration (issuer URL, client ID, client secret) differs and is injected via environment variables.
- The `.env.example` files must document `OIDC_ISSUER`, `OIDC_CLIENT_ID`, and `OIDC_CLIENT_SECRET` for each component.
- Keycloak configuration (realms, clients, roles) must be kept as exportable config files in the repository so any developer can spin up a consistent local IdP.

---

## Part 2 — Backend For Frontend (BFF) Pattern

### Context

With OIDC in place, a decision is needed on how frontend clients interact with the Quarkus backend and how tokens are managed in each client.

A naive approach (clients hold tokens directly and call the backend) exposes tokens in the browser or requires complex token management in the iOS app. The BFF pattern provides a cleaner alternative.

### Decision

The **Backend For Frontend (BFF)** pattern is adopted between frontend clients and the Quarkus backend.

```
                    ┌─────────────────────────────────┐
                    │          Auth0 / Keycloak        │
                    │         (OIDC Identity Provider) │
                    └────────────┬────────────────┬────┘
                                 │                │
                    OIDC flow    │                │  OIDC flow
                                 ▼                ▼
┌──────────────┐    ┌────────────────┐    ┌───────────────┐
│   Browser    │◄──►│  Next.js BFF   │    │   iOS App     │
│ (no tokens)  │    │ (server-side)  │    │  (BFF or      │
└──────────────┘    │  holds tokens  │    │  direct PKCE) │
                    └───────┬────────┘    └───────┬───────┘
                            │                     │
                     API calls (server-to-server) │
                            │                     │
                            ▼                     ▼
                    ┌─────────────────────────────────┐
                    │       Quarkus Backend API        │
                    │      (validates JWT tokens)      │
                    └─────────────────────────────────┘
```

#### Desktop (Next.js)

- The Next.js server layer acts as the BFF.
- The OIDC Authorization Code Flow is handled **server-side**; tokens are never exposed to the browser.
- The browser communicates only with the Next.js BFF via session cookies (httpOnly, secure).
- The BFF forwards requests to the Quarkus backend attaching the access token server-to-server.

#### iOS (Swift)

- The iOS app uses the **Authorization Code Flow with PKCE** directly against the IdP.
- Tokens are stored securely in the **iOS Keychain**.
- The app calls the Quarkus backend directly with the access token in the `Authorization` header.
- A dedicated BFF for iOS may be introduced in a later phase if complexity warrants it.

### Consequences

- **Browser security improved**: no tokens in `localStorage` or JavaScript-accessible cookies; XSS attacks cannot steal tokens.
- **Next.js API routes** become a mandatory part of the desktop architecture — all backend calls are proxied through them.
- **iOS** has a simpler, more direct path; PKCE ensures security without a server-side intermediary.
- Token refresh is the BFF's responsibility for desktop; the iOS app manages its own refresh via the Keychain.
- The Quarkus backend is always called **server-to-server** from Next.js — CORS policy can be restricted accordingly.

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| Tokens stored in browser localStorage | Vulnerable to XSS; not acceptable for a privacy-first product |
| Single IdP for all environments (Auth0 only) | Forces internet dependency for local dev; slows developer iteration |
| Single IdP for all environments (Keycloak only) | Operational burden of self-hosting in production; managed IdP preferred for prod |
