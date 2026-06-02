# ADR-019 — UX Philosophy: Gamification over Dashboards

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |
| **Scope** | All client interfaces (desktop + iOS) |

## Context

Mooder delivers AI-powered insights on conversations. The UX challenge is to surface these insights in a way that feels natural and engaging rather than clinical or intrusive.

There is a risk that the product becomes a reporting tool: dashboards, graphs, analysis panels. That framing is cold and ultimately off-putting for a messaging companion. Users will disengage.

The alternative is to make the journey **playful**.

## Decision

> **Mooder's UX language is gamification. Not dashboards. Not reporting.**

### Core principle

Privacy controls and AI insights are not delivered as data. They are delivered as **moments** — small, playful interactions that feel native to a chat product and reward engagement rather than demanding attention.

The user must feel like they are **playing with their companion**, not reading a report about themselves.

### What this means in practice

| Instead of... | Mooder does... |
|---|---|
| A sentiment analysis dashboard | An emoji reaction that appears after a session |
| A "your data retention settings" form | A playful prompt: *"Keep this? 🔒 or let it go? 🌬️"* |
| A privacy alert panel | A gentle nudge with a single emoji and one action |
| A writing quality score | A whispered suggestion: *"Want to try saying it differently? ✨"* |
| An AI report PDF | A moment card that appears at the end of a session |

### The four awareness dimensions Mooder coaches

These are the questions the product wants users to ask — surfaced through play, not instruction:

| Dimension | Question the user discovers | UX vehicle |
|---|---|---|
| **Exposure** | What am I revealing about myself without realising it? | Post-session insight moment |
| **Sharing** | Should I have shared this? | Sensitivity nudge during / after conversation |
| **Intent vs output** | What did I want to say vs. what I actually said? | Writing reflection moment |
| **Alternatives** | What could I have written instead? | In-context suggestion (opt-in) |

### Gamification elements (design direction — not exhaustive)

- **Emoji as the primary feedback language** — lightweight, universal, non-judgmental.
- **Moment cards** — full-screen ephemeral cards (like Snapchat stories) that deliver a single insight after a session. Dismissible. No scrolling required.
- **Streaks and soft achievements** — optional layer: "3 sessions reviewed this week 🌱". Celebrates engagement without shaming inaction.
- **Choices, not forms** — privacy actions are presented as binary or ternary choices with personality, not settings pages.
- **Progressive revelation** — deeper insights and controls unlock as the user engages more. The product doesn't overwhelm on day one.

### What gamification is NOT in Mooder

- ❌ Points, leaderboards, or competitive mechanics — this is a private, personal product.
- ❌ Badges for sharing more data — gamification must never incentivise privacy degradation.
- ❌ Addictive dark patterns — the goal is healthy engagement, not compulsion.

### Push notification alignment

Per ADR-018, AI insight notifications include **an emoji only** as the teaser body. This is the gamification philosophy applied to the notification layer — intrigue without content exposure.

## Consequences

- UX/UI design must be driven by this philosophy from the first wireframe. Any design that looks like a dashboard should be challenged.
- The AI insights output format (EP-003) must produce content that can be rendered as moments and emoji-driven cards, not tables or reports.
- Privacy controls (EP-004) must be designed as conversational choices, not settings panels.
- This philosophy is a **design constraint** — engineering must preserve the space for it (e.g., full-screen moment card routing, ephemeral UI states).

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| Dashboard-first UX | Clinical, disengaging; positions Mooder as an analytics tool rather than a companion |
| Neutral / minimal UI (no gamification) | Misses the opportunity to make privacy controls feel rewarding and accessible |
| Heavy gamification (points, XP) | Risks feeling trivial for a product dealing with personal and sensitive data |
