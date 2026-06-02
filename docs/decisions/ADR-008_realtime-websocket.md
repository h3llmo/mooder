# ADR-008 — Real-time Messaging Protocol: WebSocket

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |

## Context

Phase 1 requires real-time delivery of messages between two users. The protocol must be supported by the Quarkus backend, the Next.js BFF, and the Swift iOS client.

## Decision

**WebSocket** is used for real-time bidirectional communication between clients and the backend.

- The backend exposes a WebSocket endpoint for chat sessions.
- The Next.js BFF maintains a server-side WebSocket connection to the backend and relays messages to the browser via its own WebSocket or SSE connection.
- The iOS app connects directly via WebSocket to the backend.
- Connection lifecycle (open, ping/pong keepalive, reconnect on drop) is managed client-side with exponential back-off.

## Consequences

- True bidirectional push — no polling overhead.
- Quarkus has native WebSocket support; Next.js and Swift both have mature WebSocket libraries.
- Nginx must be configured to upgrade HTTP connections to WebSocket (`proxy_pass` + `Upgrade` header).
- Connection state must be managed carefully at scale (sticky sessions or a shared connection registry).

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| SSE (Server-Sent Events) | Unidirectional — client cannot push; requires a separate HTTP channel for sending |
| HTTP long-polling | Higher latency, higher server load; not suitable for a chat product |
| gRPC streaming | Overkill for Phase 1; poor native browser support |
