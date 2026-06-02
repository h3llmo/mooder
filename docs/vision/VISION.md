# Mooder — Product Vision

> *Status: Draft — validated 2026-06-02*

---

## North star

> **Mooder is a smart messaging companion.**
> It delivers AI-powered insights on your conversations and gives you meaningful control over your digital communication.

---

## What Mooder is

A messaging application where AI works quietly in the background — analysing your conversations after a session and returning insights in a playful, non-intrusive way.

**The experience is a companion, not a tool.** There are no dashboards, no analytics panels, no reports. Insights arrive as emoji, moment cards, and gentle nudges that feel native to a chat product.

### What Mooder delivers

1. **A polished chat experience** — fast, familiar 1-on-1 messaging. The entry point.
2. **AI insights** — after a session, playful moment-based insights about tone, patterns, and communication. Delivered as moments, not metrics.
3. **Content privacy controls** — app-level security using a drawn pattern (dot-connect style). Users control what is visible in the app at any time.
4. **Communication awareness** — users gain a clearer picture of what they share, how they come across, and what they might say differently.

---

## Problem statement

Digital communication is fast, often reactive, and rarely reflected upon. People send messages without considering tone, what they reveal about themselves, or whether they'd phrase things differently with a moment's thought.

Mooder creates that moment — playfully, not intrusively.

In parallel, the people who use messaging apps most have a legitimate need to keep some conversations private at the app level. A drawn pattern lock (familiar from Android pattern unlock) lets users protect their content simply and without friction.

---

## Phase 1 — Chat companion

### What it is

A messaging application for 1-on-1 conversations with an AI analysis layer and a content visibility control system.

### Target users

| Persona | Need |
|---|---|
| The reflective communicator | Wants to understand and improve how they communicate |
| The privacy-conscious user | Wants app-level control over what is visible on their device |
| The self-aware adult | Values insight into their own communication patterns |

### Phase 1 success criteria

- [ ] A user can open a 1-on-1 chat session via the desktop app or iOS app.
- [ ] Messages are stored securely in the backend.
- [ ] AI analysis flags and categorises messages and conversations after a session (with consent).
- [ ] The draw pattern system allows the user to switch between full view and filtered view seamlessly.
- [ ] Users can apply privacy settings influenced by the analysis results.
- [ ] The system handles the expected user load without degradation.

---

## Phased roadmap

Mooder is intentionally **incremental**. Each phase reaches a self-contained milestone of value while laying the infrastructure for the next.

| Phase | Focus | Core value delivered |
|---|---|---|
| **Phase 1** *(current)* | 1-on-1 chat + AI insights + content privacy | Smart messaging with AI awareness |
| **Phase 2+** | Additional interaction surfaces | Same philosophy & infrastructure, new data modalities |

> Phases beyond Phase 1 are not yet scoped. The backend architecture must anticipate them from day one.

---

## Architectural philosophy

> **The backend is the backbone.**

All intelligence, data storage, and business logic live in the backend. Frontend clients (desktop, iOS, and future ones) are thin consumers of the backend API. This ensures:

- A new client or interaction surface can be added without redesigning the core.
- AI capabilities are centralised and reusable across phases.
- Data privacy and security policies are enforced at a single layer.

---

## Out of scope — Phase 1

- Group chats (more than 2 participants)
- Voice or video messaging
- Social graph / friend discovery features
- Monetisation / subscription model (infrastructure to be prepared, not activated)
- App Store submission (TestFlight POC first — see ADR-015)

---

*Last updated: 2026-06-02*

