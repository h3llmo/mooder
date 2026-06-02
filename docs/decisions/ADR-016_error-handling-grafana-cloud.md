# ADR-016 — Error Handling & Observability: Grafana Cloud

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |
| **Relates to** | ADR-007 (observability standards) |

## Context

Errors must be captured, structured, and searchable across all environments. Two concerns are addressed: the **error response format** exposed to clients, and the **observability platform** used by the team for logs, traces, and alerting.

Splunk was initially named but is enterprise-scale and expensive — not appropriate for a startup-phase product. The team has no Splunk expertise.

## Decision

### 1 — Standard error response format (API)

All REST API errors return a consistent JSON structure:

```json
{
  "timestamp": "2026-06-02T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "MOODER_ERR_001",
  "message": "Human-readable description safe to display",
  "path": "/api/v1/messages",
  "traceId": "abc123def456"
}
```

| Field | Purpose |
|---|---|
| `status` | HTTP status code |
| `error` | Standard HTTP reason phrase |
| `code` | Mooder-specific error code — stable identifier for client-side handling (see `docs/error-codes.md`) |
| `message` | Safe, non-sensitive description — no stack traces, no internal paths |
| `traceId` | OTEL trace ID — links this error directly to the full trace in Grafana |

Internal error details are **never exposed in API responses**. They are logged server-side only.

### 2 — Observability platform: Grafana Cloud

**Grafana Cloud** is the observability platform for staging and production.

#### Why Grafana Cloud

- The local stack already runs Grafana + Prometheus (ADR-007). Grafana Cloud is the hosted version of the exact same tooling — zero new tools to learn.
- It is **OTEL-native**: logs, traces, and metrics emitted by the backend (ADR-007) are ingested directly via OTLP with no transformation.
- The free tier covers a startup comfortably (50 GB logs/month, 50 GB traces, generous metrics retention).
- No infrastructure to manage — it is a fully managed SaaS.

#### Grafana Cloud stack

| Signal | Grafana Cloud service | Local equivalent |
|---|---|---|
| Logs | **Loki** | Console / Grafana local |
| Traces | **Tempo** | Console (OTEL) |
| Metrics | **Mimir** (Prometheus-compatible) | Prometheus container |
| Dashboards & alerts | **Grafana** | Grafana container |

#### Integration

The backend ships logs, traces, and metrics to Grafana Cloud via the **OTEL Collector** configured with the Grafana Cloud OTLP endpoint. A single environment variable change (`OTEL_EXPORTER_OTLP_ENDPOINT`) points the backend at Grafana Cloud in stg/prod and at the local collector locally.

#### Alerting

Grafana alerting rules notify the team (email / Slack) on:
- Error rate exceeding a threshold
- Health probe failures
- Latency p95 exceeding SLO

## Consequences

- Local and production use identical tooling — dashboards built locally work in Grafana Cloud.
- The `traceId` in every error response lets a developer jump from a bug report directly to the full trace in Grafana Tempo.
- Free tier is sufficient for Phase 1; paid tiers scale transparently if needed.
- No Splunk expertise or budget required.

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| Splunk | Enterprise pricing and complexity; no team expertise; overkill for this stage |
| Datadog | Excellent product but expensive at scale; Grafana Cloud covers the same needs at lower cost |
| Sentry | Strong for frontend/error tracking specifically; Grafana Cloud covers the full backend observability surface |
| ELK stack (self-hosted) | Operational overhead; managed Grafana Cloud preferred |


| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |
| **Relates to** | ADR-007 (observability standards) |

## Context

Errors must be captured, structured, and observable across all environments. Two concerns are addressed here: the **error response format** exposed to clients, and the **error aggregation platform** used by the team.

## Decision

### 1 — Standard error response format (API)

All REST API errors follow a consistent JSON structure:

```json
{
  "timestamp": "2026-06-02T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "MOODER_ERR_001",
  "message": "Human-readable description safe to display",
  "path": "/api/v1/messages",
  "traceId": "abc123def456"
}
```

| Field | Purpose |
|---|---|
| `status` | HTTP status code (mirrors the HTTP response status) |
| `error` | Standard HTTP reason phrase |
| `code` | Mooder-specific error code — stable identifier for client-side handling |
| `message` | Safe, non-sensitive description (no stack traces, no internal paths) |
| `traceId` | OTEL trace ID — links this error to the full trace in Splunk |

Internal error details (stack traces, SQL errors) are **never exposed in API responses**. They are logged server-side only.

### 2 — Error aggregation: Splunk

**Splunk** is the error aggregation and log analysis platform for staging and production.

- The backend's structured JSON logs (ADR-007) are shipped to Splunk.
- Splunk indexes on `traceId`, `code`, `status`, and `level` — enabling instant cross-environment search.
- Error dashboards and alerts are configured in Splunk (threshold-based alerting for error rate spikes).
- Locally, errors go to the console (Grafana handles local observability per ADR-007).

### Error code registry

Mooder error codes follow the pattern `MOODER_ERR_{NNN}`. A registry file (`docs/error-codes.md`) is maintained alongside this ADR and updated as new error codes are introduced.

## Consequences

- Every client (Next.js, iOS) can handle errors programmatically via the `code` field without parsing message strings.
- The `traceId` in the error response lets a developer jump directly to the full trace in Splunk from a bug report.
- Splunk requires configuration as part of the staging/production setup (log shipper agent or direct HTTPS event collector).

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| Sentry | Strong for frontend error tracking; Splunk preferred for server-side log analysis and existing team familiarity |
| ELK stack (self-hosted) | Operational overhead; managed Splunk preferred |
| No standard error format | Inconsistent client error handling; ruled out |
