# EP-002 — Phase 1: 1-on-1 Chat

| Field | Value |
|---|---|
| **Phase** | Phase 1 |
| **Status** | 🔵 Backlog |
| **Priority** | High |
| **Owner** | TBD |

## Description

Deliver the core messaging experience: two users can open a conversation, exchange text messages in real time, and have their history persisted securely. This is the foundation on which AI insights (EP-003) and privacy controls (EP-004) are layered.

The chat experience should feel familiar — comparable to Instagram DMs, Messenger, or Snapchat — while being backed by a scalable, privacy-first infrastructure.

## Stories in this epic

| ID | Title | Status |
|---|---|---|
| US-009 | Set up Auth0 tenants (stg + prod) and local Keycloak | 🔵 Backlog |
| US-010 | User registration & authentication via OIDC (BFF for desktop, PKCE for iOS) | 🔵 Backlog |
| US-011 | Create / join a 1-on-1 conversation | 🔵 Backlog |
| US-012 | Send and receive text messages in real time | 🔵 Backlog |
| US-013 | Message persistence (history & storage) | 🔵 Backlog |
| US-014 | Desktop chat UI | 🔵 Backlog |
| US-015 | iOS chat UI | 🔵 Backlog |

## Acceptance criteria

- [ ] A registered user can start a conversation with another registered user.
- [ ] Messages are delivered in real time on both desktop and iOS clients.
- [ ] Conversation history is persisted and retrievable after app restart.
- [ ] All message data is stored securely server-side.

## Dependencies

- EP-001 (infrastructure & architecture baseline) must be completed first.

## Notes

*Real-time delivery mechanism (WebSocket, SSE, polling) to be decided as an ADR.*
