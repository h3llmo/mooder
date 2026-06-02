# ADR-012 — Data Retention & GDPR Compliance

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |
| **Regulation** | EU GDPR (Regulation 2016/679) |

## Context

Mooder processes personal data — messages, AI-derived insights, and user profile information. As a product targeting EU users, full GDPR compliance is a non-negotiable requirement, not an afterthought.

## Decision

### Principles (GDPR Articles 5 & 25)

| Principle | Implementation |
|---|---|
| **Purpose limitation** | Data collected only for the stated purpose (relation companion features). No secondary use without explicit consent. |
| **Data minimisation** | Only data necessary for the feature is stored. No speculative collection. |
| **Storage limitation** | Data is not kept longer than necessary. Retention periods are defined per data type. |
| **Privacy by design** | Privacy controls are built into the architecture from day one, not bolted on. |
| **Right to erasure (Art. 17)** | A user can request full deletion of their data. The system must honour this within the legally required timeframe. |
| **Data portability (Art. 20)** | Users can export all their personal data in a machine-readable format (JSON). |
| **Consent** | AI analysis is only triggered with explicit, informed consent from all participants (see EP-003). |

### Retention periods (draft — to be validated with legal counsel)

| Data type | Retention period | Trigger for deletion |
|---|---|---|
| Chat messages | User-defined (default: indefinite) | User deletes conversation or account |
| AI insights | 12 months | Insight age or user deletion request |
| User profile | Duration of account | Account deletion |
| Audit logs (impersonation, consent) | 3 years | Automatic expiry |
| Authentication events | 90 days | Automatic expiry |

### Data residency

All personal data of EU users must be stored on infrastructure located within the EU. This constraint applies to the choice of backend hosting (DigitalOcean EU region — see ADR-014), database hosting, and any third-party AI provider (see ADR-011 open question).

### Right to erasure implementation

Deletion is **hard delete** by default for user-generated content. Derived data (AI insights) is deleted alongside source data. Anonymised aggregates (if any) may be retained.

## Consequences

- A Data Processing Agreement (DPA) is required with every third-party sub-processor (Auth0, DigitalOcean, AI provider).
- A Privacy Policy and Terms of Service must be written and presented to users before account creation.
- The right-to-erasure flow (US-032) must cascade across all data stores: PostgreSQL, IdP (Auth0), and any AI provider logs.
- Legal counsel review of retention periods is required before production launch.

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| Soft delete only | Soft-deleted data is still stored and still personal data under GDPR — does not satisfy right to erasure |
| US-only compliance (CCPA) | Product targets EU users; GDPR is stricter and supersedes |
