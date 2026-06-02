# Mooder — Product Backlog

> This is the master list of all known work items.
> Items are refined progressively. Only `🟡 Ready` items are sprint-eligible.

---

## Epics

| ID | Title | Phase | Status |
|---|---|---|---|
| [EP-001](epics/EP-001_project-setup.md) | Project Setup & Infrastructure | All | 🟠 In Progress |
| [EP-002](epics/EP-002_phase1-chat.md) | Phase 1 — 1-on-1 Chat | Phase 1 | 🔵 Backlog |
| [EP-003](epics/EP-003_phase1-ai-insights.md) | Phase 1 — AI Insights Engine | Phase 1 | 🔵 Backlog |
| [EP-005](epics/EP-005_selective-visibility-draw-pattern.md) | Phase 1 — Selective Visibility (Draw Pattern System) | Phase 1 | 🔵 Backlog |

---

## Backlog items

### EP-001 — Setup & Infrastructure

| ID | Title | Priority | Status |
|---|---|---|---|
| US-001 | Define product vision & objectives | High | 🟢 Done |
| US-002 | Set up repository structure | High | 🟢 Done |
| US-003 | Define backend architecture baseline | High | 🔵 Backlog |
| US-004 | Define desktop frontend architecture | Medium | 🔵 Backlog |
| US-005 | Define iOS app architecture | Medium | 🔵 Backlog |
| US-006 | Set up Docker Compose for full local stack (Nginx + all services) | High | 🔵 Backlog |
| US-006-b | Configure Nginx as reverse proxy / LB (local + deployed) | High | 🔵 Backlog |
| US-006-c | Implement local mock data seeding at startup | High | 🔵 Backlog |
| US-007 | Set up GitHub Actions CI pipeline | High | 🔵 Backlog |
| US-008 | Set up Vercel project (desktop frontend) | High | 🔵 Backlog |
| US-009-b | Decide backend hosting platform (stg/prod) | High | 🔵 Backlog |
| US-010-b | Set up staging environment (backend + db) | High | 🔵 Backlog |
| US-011-b | Set up production environment (backend + db) | High | 🔵 Backlog |
| US-SEC-01 | Implement impersonation feature with env-based security controls | High | 🔵 Backlog |
| US-OBS-01 | Expose health probes on backend (liveness, readiness, startup) | High | 🔵 Backlog |
| US-OBS-02 | Expose Prometheus metrics endpoint on backend | High | 🔵 Backlog |
| US-OBS-03 | Instrument backend with OpenTelemetry (traces + structured logs) | High | 🔵 Backlog |
| US-OBS-04 | Add Prometheus + Grafana to local Docker Compose stack | Medium | 🔵 Backlog |

### EP-002 — Phase 1: 1-on-1 Chat

| ID | Title | Priority | Status |
|---|---|---|---|
| US-009 | Choose and set up OIDC identity provider (Auth0 stg/prod, Keycloak local) | High | 🟢 Done |
| US-010 | User registration & authentication via OIDC | High | 🔵 Backlog |
| US-011 | Create / join a 1-on-1 conversation | High | 🔵 Backlog |
| US-012 | Send and receive text messages in real time | High | 🔵 Backlog |
| US-013 | Message persistence (history & storage) | High | 🔵 Backlog |
| US-014 | Desktop chat UI | High | 🔵 Backlog |
| US-015 | iOS chat UI | High | 🔵 Backlog |
| US-016 | Push notifications — new message (iOS APNs + Web Push) | High | 🔵 Backlog |
| US-017 | Push notifications — AI insights ready + privacy alerts | Medium | 🔵 Backlog |
| US-018 | Notification preferences (per type + per conversation mute) | Medium | 🔵 Backlog |

### EP-003 — Phase 1: AI Insights Engine

| ID | Title | Priority | Status |
|---|---|---|---|
| US-020 | Define insight categories (tone, sentiment, patterns) | High | 🔵 Backlog |
| US-021 | Analyse a completed chat session and produce insights | High | 🔵 Backlog |
| US-022 | Present insights to the user post-session | High | 🔵 Backlog |
| US-023 | Consent flow before analysis is triggered | High | 🔵 Backlog |

### EP-004 — Phase 1: Privacy Controls

| ID | Title | Priority | Status |
|---|---|---|---|
| US-030 | User data retention settings (keep / delete / expire) | High | 🔵 Backlog |
| US-031 | Content sensitivity flags surfaced from AI analysis | Medium | 🔵 Backlog |
| US-032 | Export personal data (GDPR-style) | Medium | 🔵 Backlog |
| US-033 | Sharing restriction controls per conversation | Medium | 🔵 Backlog |

---

## Backlog refinement notes

- 2026-06-02: Vision validated. Phase 1 epics seeded from product brief. Stories are draft-level; estimation and acceptance criteria to be added in upcoming refinement sessions.

