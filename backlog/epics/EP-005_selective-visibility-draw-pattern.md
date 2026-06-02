# EP-005 — Selective Content Visibility (Draw Pattern System)

| Field | Value |
|---|---|
| **Phase** | Phase 1 |
| **Status** | 🔵 Backlog |
| **Priority** | High |
| **Owner** | TBD |

## Description

The draw pattern system is Mooder's content privacy feature. It allows users to switch between a **full view** (master pattern — all content visible) and a **safe view** (safe pattern — only safe-flagged content visible) with no visual difference between the two states.

The mechanism is a **dot-connect drawn pattern** — identical in style on iOS (finger) and desktop (mouse). The interaction is familiar, brute-force resistant, and presents naturally as app security to the user.

See [ADR-020](../../docs/decisions/ADR-020_selective-visibility-draw-pattern.md) for the full design.

## Stories in this epic

| ID | Title | Status |
|---|---|---|
| US-040 | Data model: add `visibility` field to messages and conversations | 🔵 Backlog |
| US-041 | AI auto-categorisation of messages/conversations (safe / private / ai-flagged) | 🔵 Backlog |
| US-042 | User manual reclassification of message/conversation visibility | 🔵 Backlog |
| US-043 | Pattern setup flow — master pattern (framed as app security, suggested at first use, dismissible) | 🔵 Backlog |
| US-044 | Dot-connect pattern entry screen (finger on iOS, mouse on desktop — shared component) | 🔵 Backlog |
| US-045 | Safe pattern — silent registration of a second drawn pattern (not mentioned in onboarding) | 🔵 Backlog |
| US-046 | Safe view — server-side visibility-scoped queries | 🔵 Backlog |
| US-047 | Safe view — conversation list renders only safe content (no placeholder, no count) | 🔵 Backlog |
| US-048 | Safe view — incoming notifications suppressed for hidden conversations | 🔵 Backlog |
| US-049 | Pattern codes hashed server-side; no plaintext storage; silent failure on wrong pattern | 🔵 Backlog |

## Acceptance criteria

- [ ] In safe view, hidden conversations do not appear in the list — no placeholder, no count, no indicator.
- [ ] In safe view, hidden messages within a visible conversation do not appear — absence looks natural.
- [ ] The app is visually identical in full view and safe view to an outside observer.
- [ ] Network inspection in safe view yields no hidden content.
- [ ] A wrong pattern attempt produces no error message or visual feedback.
- [ ] The AI correctly categorises at least 80% of messages in test scenarios (to be refined with mock data).

## Dependencies

- EP-002 (chat must exist — messages and conversations must exist to categorise).
- EP-003 (AI categorisation feeds this epic — US-041 depends on the AI engine).

## Notes

- **The master pattern is presented as app security** — framed at onboarding as "protect your conversations with a pattern".
- **The safe pattern is not surfaced during onboarding.** It is available for users who discover and register a second pattern.
- **Not enabled by default** — suggested once at first launch, dismissible. Many users will only ever use the master pattern. That is a fully valid use case.
- US-044 (dot-connect component) should be built as a **shared UI component** reused on both iOS and desktop.
- Multi-device sync of visibility mode is an open question (see ADR-020).
