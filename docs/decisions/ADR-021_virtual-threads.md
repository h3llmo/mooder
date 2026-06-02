# ADR-021 — Concurrency Model: Virtual Threads (Project Loom)

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Tech lead, Product owner |
| **Replaces** | N/A — first explicit concurrency decision |

## Context

Quarkus 3 on Java 21 offers two concurrency models for building a scalable, non-blocking backend:

### Option A — Reactive (Mutiny + Vert.x reactive pg client)

- All service methods return `Uni<T>` or `Multi<T>` (reactive streams)
- Database calls use `quarkus-reactive-pg-client` — a Vert.x-based non-blocking PostgreSQL driver
- Truly non-blocking end-to-end: no OS thread is held during I/O
- **Cost:** every layer of the codebase must speak Mutiny. Error handling, transactions, and testing become significantly more complex. The reactive mental model is a steep learning curve and a major source of bugs.

### Option B — Virtual Threads (`@RunOnVirtualThread`, Project Loom)

- All service and resource methods are written as **normal blocking Java** — plain `return`, `try/catch`, `for` loops
- Each request runs on a **Java 21 virtual thread** — a lightweight user-space thread managed by the JVM (Project Loom)
- When a virtual thread blocks on I/O (database query, network call), the OS thread is **released** and reused by another virtual thread. Blocking is structurally non-blocking.
- Database: standard `quarkus-jdbc-postgresql` (JDBC) + Hibernate ORM Panache — unchanged
- Quarkus annotation: `@RunOnVirtualThread` on REST resources and service beans

## Decision

**We use virtual threads (`@RunOnVirtualThread`) as the concurrency model for all Mooder backend runtimes.**

### What this means in practice

```java
@Path("/api/v1/chat")
@RunOnVirtualThread   // <-- this is all it takes
public class ChatResource {
    // Write plain blocking code here. Scalability is handled by the JVM.
    public List<MessageDto> listMessages(...) {
        return chatService.listMessages(...); // blocks a virtual thread, not an OS thread
    }
}
```

### Why not the Vert.x reactive pg client?

The reactive pg client (`quarkus-reactive-pg-client`) is the correct choice **only** for the Mutiny reactive model. With virtual threads and JDBC, the JDBC driver calls block the virtual thread — not the OS thread. The scalability outcome is equivalent; the code complexity is vastly lower.

> We are not losing performance by using JDBC on virtual threads. We are trading reactive complexity for readable, testable, blocking-style code.

### Database transactions

Hibernate ORM handles transactions in the normal `@Transactional` way. Virtual threads release the underlying OS thread during JDBC I/O automatically via Loom. No reactive transaction API is needed.

### WebSocket (ADR-008)

Quarkus WebSockets Next (`quarkus-websockets-next`) is natively virtual-thread compatible. WebSocket handlers are annotated with `@RunOnVirtualThread` too.

### Thread pool

Virtual threads are created on demand by the JVM — there is no fixed pool to tune. This eliminates thread starvation as a class of scaling problem.

## Consequences

- **All REST resources** must carry `@RunOnVirtualThread` (or inherit it from a class-level annotation).
- **No Mutiny types** (`Uni`, `Multi`) in service interfaces or implementations — if a future contributor adds them, it is a code review rejection.
- Service interfaces in `mooder-api` remain plain Java — no reactive types. This is enforced by the module having no Mutiny dependency.
- `quarkus-reactive-pg-client` is **not** added to the project. `quarkus-jdbc-postgresql` is the only PostgreSQL driver.
- Java 21 is a hard runtime requirement (already set in parent pom).

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| Mutiny reactive + reactive pg client | Correct for reactive pipelines; overkill for this team size and scope. Code complexity is very high. |
| Classic Quarkus (platform threads, blocking executor pool) | Works but does not scale under high concurrency — thread pool exhaustion under load. |
| Kotlin coroutines | Requires Kotlin; we are a Java project. |
