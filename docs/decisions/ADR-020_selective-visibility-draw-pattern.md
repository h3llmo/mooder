# ADR-020 — Selective Content Visibility: Draw Pattern System

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |
| **Security level** | 🔴 High — core product differentiator |

## Context

Users of messaging apps have a legitimate need for app-level content privacy — controlling what is visible on their device at any given moment. Standard app locks protect the whole app or nothing. There is no existing, frictionless way to show a subset of conversations to someone looking at your screen while remaining fully functional.

Mooder's answer is a **selective content visibility system** built on drawn patterns — a familiar gesture-based mechanism that doubles as both app security and content filtering.

## Decision

### Concept

The user controls what is visible through **drawn patterns** — a finger or mouse drawn across a dot-connect grid. Each unique drawing maps to a visibility mode.

Two visibility modes exist:

| Mode | Activated by | Shows |
|---|---|---|
| **Full view** | Master drawn pattern | All content — every conversation, every message |
| **Safe view** | Safe drawn pattern(s) | Only content flagged as safe |

**Critical UX invariant**: the app looks and feels **identical** in both modes. There is no badge, no lock icon, no visual indicator that a filter is active.

### Input: dot-connect style, all platforms

A **dot-connect grid** (the user draws a path connecting dots) is used on all platforms:

| Platform | Input method |
|---|---|
| iOS | Finger touch |
| Desktop (Next.js) | Mouse drag |

The interaction model is identical — the same dot grid, the same connect-the-dots gesture. This ensures design consistency across platforms and keeps the desktop implementation straightforward. Desktop is primarily a portability and design integrity proof; touch-screen desktop use is also supported naturally.

### Pattern types

| Pattern | Role | Quantity |
|---|---|---|
| **Master pattern** | Unlocks full view | 1 per user |
| **Safe pattern** | Activates safe view | 1 or more per user |

The user chooses their own patterns. The safe pattern(s) exist silently — they are never mentioned in the UI alongside the master pattern. Setting up a second pattern is something the user discovers and chooses, not something Mooder instructs.

### Activation: opt-in, suggested at first use

> **This feature is not enabled by default.**

At first launch, Mooder **suggests** setting up a drawn pattern — framed purely as **app security** ("protect your conversations with a pattern, like a PIN"). This is:

1. **Normal and expected** — a pattern lock on a messaging app raises no suspicion. It is no different from setting a PIN on a phone.
2. **Non-revealing** — enabling a master pattern does not imply the existence of a safe pattern. Many users will use only the master pattern, purely for security. That is a completely valid and expected use case.
3. **Non-coercive** — the suggestion can be dismissed. The feature remains off until the user opts in.

The selective visibility capability — drawing a *different* pattern to see filtered content — is **not explained during onboarding**. It is available for users who want it, discoverable through use.

### Content categorisation

Every message and conversation is assigned a **visibility category**:

| Category | Meaning | Visible in safe view? |
|---|---|---|
| `safe` | Content the user is comfortable showing | ✅ Yes |
| `private` | Content the user wants hidden by default | ❌ No |
| `ai-flagged` | AI analysis suggested this may be sensitive | ❌ No (user can reclassify) |

Categorisation happens through two channels:
1. **AI automatic** — the analysis engine (EP-003) flags messages and conversations based on tone, content sensitivity, and context.
2. **User manual** — the user can reclassify any message or conversation at any time.

### Safe view appearance

When in safe view:
- Hidden conversations do not appear in the conversation list.
- Hidden messages within a visible conversation do not appear — the conversation looks naturally sparse, not truncated.
- The app functions normally — the user can send messages, receive notifications, and use all features. Incoming messages on hidden conversations are suppressed from previews and notifications in safe view.
- **No placeholder, no "X hidden items", no ellipsis.** The absence of content must be natural.

### Security constraints

- Pattern codes are **never stored in plaintext** server-side. They are hashed and salted.
- The visibility filter is applied **server-side at query time** — the hidden content is never sent to the client in safe view. A network inspection in safe view yields no hidden data.
- Failed pattern attempts are silent — no error message, no "wrong pattern" feedback.
- There is no "forgot my pattern" recovery flow — by design.

### Relationship to AI insights

The AI categorisation layer feeds directly into the draw pattern system:
- After a session analysis, the AI proposes visibility categories per message/conversation.
- The user reviews the proposals through the gamified moment-card UX (ADR-019) — not a settings page.
- Accepting a proposal feels like a swipe or a tap, not a form submission.

## Consequences

- This feature is architecturally significant: the backend must support visibility-scoped queries for every content endpoint.
- The data model must carry a `visibility` field on both messages and conversations from day one — retrofitting this is expensive.
- The absence of a "wrong pattern" signal means UX must be designed so the user is confident they entered the correct pattern without explicit feedback.
- This feature must be treated with the same security rigour as authentication — a bypass would be a critical vulnerability.
- **This feature is presented as app security** — the master pattern is the entry point. The safe view is a secondary capability.

## Open questions

- [x] Pattern entry mechanism — **Resolved: dot-connect drawn gesture, all platforms (finger on iOS, mouse on desktop).**
- [ ] Multi-device sync of visibility state — if a user has both desktop and iOS, does the visibility mode sync or stay independent per device?
- [ ] What happens to incoming notifications in safe view? (Currently: suppressed from previews — to be validated in ADR-018 follow-up.)

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| Selectable icon / emoji grid | Less resistant to brute-force attempts; dot-connect provides a larger combination space |
| Separate hidden folder / vault | Creates a visible secondary structure that signals hidden content |
| Second app / account | Too much friction; not a seamless user experience |
| PIN-based lock on the whole app | Locks everything or nothing; no content-level granularity |
| Client-side filter only | Network inspection would reveal hidden content — insufficient |
