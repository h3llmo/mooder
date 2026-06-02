# EP-003 — Phase 1: AI Insights Engine

| Field | Value |
|---|---|
| **Phase** | Phase 1 |
| **Status** | 🔵 Backlog |
| **Priority** | High |
| **Owner** | TBD |

## Description

The AI Insights Engine is Mooder's primary value differentiator. After a chat session between two users, the backend analyses the conversation content and generates meaningful, actionable observations — tone, sentiment trends, communication patterns, recurring dynamics, etc.

Both participants must explicitly consent before any analysis is performed. Insights are **not delivered as reports or dashboards** — they surface as playful moment cards, emoji reactions, and gentle nudges, consistent with ADR-019 (gamification philosophy). The output format must support this: structured data that the frontend can render as moments, not text blocks.

## Stories in this epic

| ID | Title | Status |
|---|---|---|
| US-020 | Define insight categories (tone, sentiment, patterns) | 🔵 Backlog |
| US-021 | Analyse a completed chat session and produce insights | 🔵 Backlog |
| US-022 | Present insights to the user post-session | 🔵 Backlog |
| US-023 | Consent flow before analysis is triggered | 🔵 Backlog |

## Acceptance criteria

- [ ] Insight categories are defined and validated with at least one test dataset.
- [ ] Analysis is only triggered after explicit consent from both participants.
- [ ] Insights are returned by the backend and rendered on both desktop and iOS.
- [ ] Analysis results do not expose one participant's data to the other beyond what is agreed.

## Dependencies

- EP-002 (chat must exist to analyse).

## Notes

*AI provider / model choice to be decided as an ADR. Consider on-device vs server-side processing for privacy implications.*
