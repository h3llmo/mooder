# ADR-013 — Encryption Strategy

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |
| **Security level** | 🔴 High — review required before any implementation change |

## Context

Mooder processes private conversations between individuals. Encryption is both a technical requirement and a core product promise — users must be able to trust that their messages cannot be read by anyone other than the intended participants.

The reference model is **Meta Messenger's encryption approach** (Signal Protocol-based end-to-end encryption), adapted to a scalable architecture that can be strengthened over time.

## Decision

### Encryption layers

| Layer | Mechanism | Scope |
|---|---|---|
| **In transit** | TLS 1.3 (minimum) on all connections | All client ↔ backend, backend ↔ DB, backend ↔ IdP |
| **At rest** | Database-level encryption + encrypted storage volumes | All data at rest on DigitalOcean |
| **Message content (target)** | End-to-end encryption (E2EE) based on Signal Protocol | Message payloads — readable only by sender and recipient |

### Phase 1 implementation approach

Full Signal Protocol E2EE is the **target state**. Given the complexity of key management at scale, Phase 1 delivers a **progressive encryption model**:

| Phase | Encryption level | Notes |
|---|---|---|
| **Phase 1 MVP** | TLS in transit + encryption at rest | Messages are encrypted on the wire and on disk; the backend can read content for AI analysis (with consent) |
| **Phase 1+** | E2EE for stored messages | Backend stores only ciphertext; AI analysis runs on client-decrypted content or via a privacy-preserving compute model |
| **Future** | Full Signal Protocol (double ratchet, forward secrecy) | Matches Messenger-level guarantees |

This progressive model ensures the product ships with strong baseline encryption while the E2EE upgrade path is architecturally reserved.

### Key management principles

- Encryption keys are **never stored alongside the data they protect**.
- Key material is managed by a dedicated secrets/KMS service (provided by the hosting platform or Docker Desktop locally).
- Key rotation is supported from day one — the schema must accommodate multiple key versions.

### AI analysis and E2EE

When E2EE is active, the backend cannot read message content. AI analysis must either:
- Run on the client (on-device model), or
- Use a privacy-preserving computation approach.

This constraint is a key input to the AI provider decision (ADR-011).

## Consequences

- Phase 1 MVP ships with TLS + at-rest encryption — strong baseline, honest about limitations.
- The architecture must not close the door on E2EE — data models and key management must be designed with E2EE upgrade in mind from day one.
- Any change that weakens the encryption posture requires an explicit ADR update.

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| TLS only (no at-rest encryption) | Insufficient — a compromised database exposes all content in plaintext |
| Immediate full E2EE | High implementation complexity; key distribution and recovery are non-trivial; deferred to Phase 1+ |
| Proprietary encryption scheme | Signal Protocol is open, audited, and battle-tested; custom crypto is a security anti-pattern |
