# ADR-018 — Notification Strategy

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |

## Context

Mooder is a chat product. Users must be reachable even when the app is closed. Three categories of events are worth notifying about: incoming messages, AI insights becoming available, and privacy alerts surfaced by the analysis engine.

## Decision

### Notification events

| Event | Trigger | Recipient |
|---|---|---|
| **New message** | A message is delivered to a conversation | The recipient of the message |
| **AI insights ready** | Analysis of a session is complete | Both participants (consent already given) |
| **Privacy alert** | AI analysis flags sensitive content requiring user action | The affected user |

### Delivery model

**True push notifications** — delivered even when the app is closed or in the background.

| Client | Push technology |
|---|---|
| iOS | Apple Push Notification service (APNs) |
| Desktop (Next.js) | Web Push API (via VAPID) + Service Worker |

The backend is responsible for triggering push events. It sends notifications to a **notification service** layer that dispatches to APNs or Web Push depending on the registered device/client type. A user may have multiple registered devices — a notification is fanned out to all active devices for that user.

### User preferences (granular control)

Users can control notifications at two levels:

| Level | Example |
|---|---|
| **Per notification type** | Disable "AI insights ready" notifications globally |
| **Per conversation** | Mute all notifications from a specific conversation |

Preferences are stored in the backend and respected at dispatch time — a muted conversation never triggers a push, regardless of event type.

Default state on account creation: **all notifications enabled**.

### Notification payload

Push payloads are **minimal by design** — they must not include message content in the notification body (privacy principle). The notification wakes the app; the app fetches the content securely.

```json
{
  "type": "new_message | insights_ready | privacy_alert",
  "conversationId": "uuid",
  "title": "New message",
  "body": "You have a new message."
}
```

Message text is **never included** in the push payload.

### Local environment

Push notifications cannot be tested end-to-end locally (APNs requires Apple infrastructure; Web Push requires HTTPS + a registered service worker). Local testing uses:
- In-app notification toasts (no push) as the local simulation.
- A mock notification service activated when `PUSH_PROVIDER=mock`.

## Consequences

- A notification service component must be added to the backend (or as a thin sidecar) to handle device registration, fan-out, and preference filtering.
- APNs credentials (certificate or key) must be managed in GitHub Actions secrets for the iOS build pipeline (ADR-015).
- Web Push VAPID keys must be generated and stored as secrets for the Next.js frontend.
- iOS push requires a paid Apple Developer account and APNs configuration in the TestFlight pipeline.
- Message content is never exposed in push payloads — consistent with the privacy promise and encryption roadmap (ADR-013).

## Open questions

- [x] Should AI insight notifications include a brief summary teaser? → **Resolved: emoji only** (ADR-019 — gamification philosophy).

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| In-app only notifications | Breaks the core chat experience — users expect to be notified when the app is closed |
| Third-party push service (Firebase FCM) | FCM routes iOS notifications through Google infrastructure — privacy concern for a privacy-first product; direct APNs preferred |
| No granular preferences | Poor UX; notification fatigue causes users to disable everything |
