# EP-004 — Phase 1: Privacy Controls

| Field | Value |
|---|---|
| **Phase** | Phase 1 |
| **Status** | 🔵 Backlog |
| **Priority** | High |
| **Owner** | TBD |

## Description

Privacy controls are the deeper value proposition of Mooder (see VISION.md). The AI analysis (EP-003) surfaces what is in the data; the privacy controls give users the power to act on that knowledge.

Critically, these controls must **not feel like a settings page**. Per ADR-019 (gamification), every privacy action is a playful choice — a moment, a nudge, a binary decision with personality. The user should feel empowered and in control, not audited or overwhelmed.

## Stories in this epic

| ID | Title | Status |
|---|---|---|
| US-030 | User data retention settings (keep / delete / expire) | 🔵 Backlog |
| US-031 | Content sensitivity flags surfaced from AI analysis | 🔵 Backlog |
| US-032 | Export personal data (GDPR-style) | 🔵 Backlog |
| US-033 | Sharing restriction controls per conversation | 🔵 Backlog |

## Acceptance criteria

- [ ] A user can set retention rules for their messages (e.g., auto-delete after N days).
- [ ] The AI analysis can flag sensitive content and surface those flags in the privacy dashboard.
- [ ] A user can request a full export of their personal data.
- [ ] A user can restrict further sharing or forwarding of a conversation.

## Dependencies

- EP-002 (data must exist to control).
- EP-003 (AI flags feed into privacy recommendations — desirable but not strictly blocking).

## Notes

*GDPR / data-protection compliance requirements to be assessed as a separate work item before US-032 is refined.*
