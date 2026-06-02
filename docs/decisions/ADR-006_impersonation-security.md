# ADR-006 — Impersonation Feature & Security Controls

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |
| **Security level** | 🔴 High — review required before any implementation change |

---

## Context

Impersonation allows a privileged operator to act on behalf of another user — useful for debugging, support, and QA. Because it grants access to another user's private data and can generate actions in their name, it is one of the most sensitive features in the platform.

Three strict requirements drive this design:
1. Local development needs it available by default with zero friction.
2. Staging needs it available but controlled and fully auditable.
3. **Production must make it technically impossible — not merely discouraged.**

---

## Decision

### Environment matrix

| | Local | Staging | Production |
|---|---|---|---|
| **Impersonation enabled** | ✅ On by default | ⚠️ Opt-in, gated | 🚫 Hard-disabled |
| **Who can impersonate** | Any `admin` user | `admin` + explicit approval token | Nobody — ever |
| **Audit log** | Console output | Persistent, tamper-evident log | N/A |
| **Session time limit** | None | 30 minutes max, non-renewable | N/A |
| **Activation mechanism** | Docker env flag | Two conditions must both be true | Refused at startup |

---

### Layer 1 — Runtime environment guard (backend)

The backend reads `MOODER_ENV` at startup. The logic is:

```
if MOODER_ENV == "prod":
    → impersonation routes are NEVER registered
    → if IMPERSONATE_ENABLED=true is also set → application refuses to start with a fatal error
    → this check cannot be bypassed by any other config value
```

This is **not a feature flag** — it is a startup invariant. The application will not run in production with impersonation attempted to be enabled.

### Layer 2 — Build-time separation

The production Docker image is built with the `prod` profile, which excludes the impersonation HTTP routes entirely. Even if someone were to flip a config value, the endpoints do not exist in the production binary.

### Layer 3 — CI/CD pipeline guard

The GitHub Actions production deployment workflow:
- Explicitly sets `MOODER_ENV=prod`.
- **Never** sets `IMPERSONATE_ENABLED`.
- The workflow file is protected by GitHub branch protection rules — only authorised maintainers can modify the production deployment workflow.
- Any PR touching the production workflow triggers a mandatory security review.

### Layer 4 — Staging controls (opt-in, dual condition)

For staging, **both** of the following must be true simultaneously to activate impersonation:

| Condition | Mechanism |
|---|---|
| `IMPERSONATE_ENABLED=true` | Set explicitly in the staging environment config |
| A valid `IMPERSONATE_APPROVAL_TOKEN` | A short-lived token (TTL: 1 hour) generated out-of-band by a team lead and injected into the request header |

Neither condition alone is sufficient. This prevents accidental activation and requires a conscious human decision each time.

**Additional staging constraints:**
- Only users with the `admin` role can initiate an impersonation session.
- Every impersonation session is bounded to **30 minutes maximum** — the token cannot be refreshed.
- Every session is written to a persistent, append-only audit log: `who impersonated whom`, `start time`, `end time`, `actions performed`.
- The impersonated user's UI shows a visible banner: *"This session is being monitored."* (future consideration — requires user notification design).

### Layer 5 — Local controls

On local (`docker compose up`), impersonation is on by default via the Docker Compose environment:
```
MOODER_ENV=local
IMPERSONATE_ENABLED=true
```
No approval token required. Audit log goes to console. No time limit. This is intentional — developer friction must be minimal locally.

---

## Audit log format (staging)

Each impersonation event produces a structured log entry:

```json
{
  "event": "impersonation_start | impersonation_end | impersonation_action",
  "impersonator": { "id": "...", "username": "..." },
  "target": { "id": "...", "username": "..." },
  "sessionId": "uuid",
  "timestamp": "ISO-8601",
  "expiresAt": "ISO-8601",
  "action": "optional — describes the action taken during the session"
}
```

---

## What "hard-disabled in production" means in practice

To be explicit: there is **no configuration, no environment variable, no API call, and no admin UI action** that can enable impersonation in a production-built, production-deployed instance of Mooder. The only way to introduce impersonation in production would be to:

1. Modify the application source code, AND
2. Pass a code review, AND
3. Merge to `main`, AND
4. Produce a new release — at which point the ADR would need to be revisited.

This is a deliberate architectural choice. Privacy is a core product promise.

---

## Consequences

- Developers have zero friction locally.
- Staging impersonation requires deliberate, human-coordinated action — accidental activation is not possible.
- Production impersonation is architecturally prevented at three independent layers (runtime, build, CI/CD).
- Any future attempt to weaken these controls requires an explicit ADR update with a documented rationale.

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| Single `IMPERSONATE_ENABLED` flag for all envs | A misconfigured prod env var would silently enable a catastrophic security hole |
| Impersonation disabled in prod by convention only | Conventions are broken; technical enforcement is required for a privacy-first product |
| Impersonation available in prod for support use cases | Out of scope for Phase 1; if ever needed, requires a separate hardened design (break-glass access with legal/compliance review) |
