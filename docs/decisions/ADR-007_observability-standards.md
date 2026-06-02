# ADR-007 — Backend Observability Standards

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |
| **Applies to** | Backend (Quarkus) — extended to other runtimes as they are added |

---

## Context

The backend is the backbone of Mooder. From day one it must expose standard, machine-readable signals about its own health and behaviour so that:
- The infrastructure knows whether a container is alive, ready to serve traffic, and starting up correctly.
- Developers can diagnose issues locally without guesswork.
- Deployed environments can plug into any standard monitoring stack without custom adapters.

The guiding principle is **standards over custom** — no proprietary monitoring contracts. Every signal the backend emits must conform to an established open standard.

---

## Decision

The backend exposes four observable surfaces, each mapped to an industry standard:

### 1 — Health probes (MicroProfile Health / Kubernetes probe contract)

| Probe | Endpoint | Meaning |
|---|---|---|
| **Liveness** | `GET /q/health/live` | Is the process alive? If not → restart it |
| **Readiness** | `GET /q/health/ready` | Is the process ready to accept traffic? If not → remove from load balancer |
| **Startup** | `GET /q/health/started` | Has the process finished initialising? Used during slow starts |
| **Aggregate** | `GET /q/health` | All checks combined |

Response format follows the **MicroProfile Health 3.x** specification — a JSON payload with `status: UP | DOWN` and named check details.

Nginx (local) and the hosting platform (stg/prod) use these endpoints to gate traffic routing automatically.

### 2 — Metrics (OpenMetrics / Prometheus format)

| Endpoint | Format | Scraped by |
|---|---|---|
| `GET /q/metrics` | Prometheus text format (OpenMetrics) | Prometheus or any compatible scraper |

Exposed metric families (at minimum):

| Category | Examples |
|---|---|
| JVM runtime | heap usage, GC pauses, thread count |
| HTTP server | request count, error rate, response time (p50/p95/p99) |
| Database pool | active connections, wait time, pool exhaustion |
| Application | custom business metrics added per feature |

### 3 — Distributed tracing (OpenTelemetry)

All inbound requests and outbound calls (database, IdP, future services) are instrumented with **OpenTelemetry (OTEL)** traces.

| Signal | Standard | Export format |
|---|---|---|
| Traces | OpenTelemetry | OTLP (OpenTelemetry Protocol) |
| Metrics | OpenTelemetry | OTLP |
| Logs | OpenTelemetry | OTLP |

The OTEL collector endpoint is configurable via environment variable (`OTEL_EXPORTER_OTLP_ENDPOINT`) — the backend does not care what collector or backend receives the data (Jaeger, Tempo, Datadog, etc.).

### 4 — Structured logging

All log output is emitted as **structured JSON** to stdout. Each log entry includes at minimum:

| Field | Description |
|---|---|
| `timestamp` | ISO-8601 |
| `level` | INFO / WARN / ERROR / DEBUG |
| `logger` | Source class/module |
| `message` | Human-readable message |
| `traceId` | OTEL trace ID — correlates log to a trace span |
| `spanId` | OTEL span ID |
| `requestId` | HTTP request identifier |

JSON logs can be ingested by any log aggregation system (Loki, ELK, Datadog Logs, CloudWatch) without transformation.

---

## Local environment

The local Docker Compose stack includes a lightweight observability stack:

| Service | Container | Purpose |
|---|---|---|
| **Prometheus** | `mooder-prometheus` | Scrapes `/q/metrics` from the backend |
| **Grafana** | `mooder-grafana` | Dashboards — pre-seeded with a Mooder backend dashboard |

Local observability URLs (through Nginx or direct):

| Tool | URL |
|---|---|
| Health (aggregate) | `http://localhost/q/health` |
| Metrics (raw) | `http://localhost/q/metrics` |
| Grafana dashboard | `http://localhost:3001` |

OTEL traces in local are exported to the console (human-readable) by default. A Jaeger or Tempo sidecar can be added via `docker-compose.override.yml` if a developer needs trace UI locally.

---

## Deployed environments (stg / prod)

The backend emits the same signals in all environments. The collection stack is provided by the hosting platform or a connected service. The backend has no opinion on which — it only speaks standard protocols.

| Signal | Standard protocol | Consumed by (TBD at infra setup) |
|---|---|---|
| Health probes | HTTP GET | Load balancer / orchestrator |
| Metrics | Prometheus scrape | Monitoring platform |
| Traces & logs | OTLP | Observability platform |

---

## Consequences

- Any monitoring tool that understands Prometheus, OTEL, or MicroProfile Health can consume Mooder's backend signals with zero configuration on the backend side.
- Adding a new runtime (future service) means implementing the same four surfaces — consistency across the platform.
- Structured logging + trace correlation means a single `traceId` can link an HTTP request across logs, metrics, and traces end-to-end.
- The local Grafana dashboard is committed to the repo and auto-provisioned — developers can debug performance issues locally without setting up anything manually.

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| Custom health endpoint format | Non-standard; breaks compatibility with orchestrators and platforms |
| Logging to files instead of stdout | Stdout is the container-native approach; file logging requires volume management |
| Vendor-specific SDK (e.g. Datadog agent embedded) | Creates a vendor lock-in in the application layer; OTEL is vendor-neutral |
| Metrics only, no tracing | Tracing is essential for diagnosing latency in a multi-component system (backend + IdP + DB) |
