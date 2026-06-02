# Mooder — Collaboration Guide

This guide explains **how the repository is organised**, why it is structured that way, and how the team works together day-to-day.

---

## 1. Project at a glance

Mooder is a multi-platform product built on three interconnected components:

- **Backend** — the authoritative data and business-logic backbone.
- **Frontend Desktop** — a desktop client consuming the backend.
- **iOS App** — a native Swift application consuming the backend.

Each component lives in its own top-level folder. They share nothing at the code level but are tied together through the backend API contract.

---

## 2. Folder skeleton explained

```
mooder/
│
├── README.md                    ← Entry point: what is Mooder?
├── CONTRIBUTING.md              ← How to branch, commit and submit PRs
├── CHANGELOG.md                 ← Version history of the whole project
│
├── src/
│   ├── backend/                 ← Java Quarkus service + PostgreSQL
│   │   └── README.md              Component-level readme
│   ├── frontend-desktop/        ← Desktop client application
│   │   └── README.md
│   └── ios-app/                 ← iOS Swift application
│       └── README.md
│
├── mock-data/                   ← Local seed data (JSON, auto-loaded at startup)
│   └── resources/
│
├── backlog/                     ← All product management artefacts
│   ├── PRODUCT_BACKLOG.md       ← Master list of all work items
│   ├── epics/                   ← One file per epic  (EP-NNN_name.md)
│   ├── user-stories/            ← One file per story (US-NNN_name.md)
│   └── sprints/                 ← One folder per sprint
│
└── docs/
    ├── collaboration/
    │   └── COLLABORATION_GUIDE.md   ← You are here
    ├── vision/
    │   └── VISION.md                ← Product objectives & context
    ├── environments/
    │   └── ENVIRONMENTS.md          ← Local / stg / prod definitions
    └── decisions/
        └── ADR_TEMPLATE.md          ← Architecture Decision Records template
```

### Why this layout?

| Principle | Implementation |
|---|---|
| **Separation of concerns** | Each component in its own folder; no cross-component source dependencies at root level |
| **Single source of truth for backlog** | All work items live under `backlog/`; no scattered to-do lists |
| **Discoverability** | Every folder has a `README.md` so newcomers can navigate without asking |
| **Lightweight governance** | Decisions are recorded in `docs/decisions/` as ADRs so context is never lost |

---

## 3. Backlog structure

The backlog is organised in three levels of granularity:

```
Epic  (EP-NNN)  — large theme, may span multiple sprints
  └── User Story (US-NNN) — deliverable slice of value, fits in one sprint
          └── Task        — technical sub-task tracked inside the story file
```

### File naming conventions

| Artefact | Folder | Name pattern | Example |
|---|---|---|---|
| Epic | `backlog/epics/` | `EP-NNN_short-name.md` | `EP-001_user-authentication.md` |
| User story | `backlog/user-stories/` | `US-NNN_short-name.md` | `US-007_login-page.md` |
| Sprint folder | `backlog/sprints/` | `sprint-NN/` | `sprint-02/` |
| Sprint plan | inside sprint folder | `SPRINT_NN_PLANNING.md` | `SPRINT_02_PLANNING.md` |

### Status labels used in backlog files

| Label | Meaning |
|---|---|
| `🔵 Backlog` | Identified, not yet refined |
| `🟡 Ready` | Refined, estimated, ready to pick up |
| `🟠 In Progress` | Actively being worked on |
| `🟢 Done` | Accepted by the team |
| `⛔ Blocked` | Cannot proceed — blocker documented |

---

## 4. Day-to-day workflow

1. **Sprint planning** — team selects stories from `PRODUCT_BACKLOG.md` (status `🟡 Ready`) and moves them into the current sprint folder.
2. **Starting work** — developer creates a `feature/<name>` branch, updates the story status to `🟠 In Progress`.
3. **Pull request** — PR references the story ID in its title (e.g. `[US-007] Implement login page`).
4. **Review & merge** — at least one reviewer approves before merge into `develop`.
5. **Sprint review** — mark completed stories `🟢 Done` and update `PRODUCT_BACKLOG.md`.

---

## 5. Documentation conventions

- **Decisions** that affect architecture or process are recorded as ADRs in `docs/decisions/`. Use `ADR_TEMPLATE.md` as a starting point.
- **Environment definitions** (local, staging, production) are documented in `docs/environments/ENVIRONMENTS.md`.
- **Component-level** technical notes go into the component's own `README.md`.
- **Project-wide** context lives under `docs/`.

---

## 6. Getting started as a new contributor

1. Read `README.md` — understand what Mooder does.
2. Read `docs/vision/VISION.md` — understand the product goals.
3. Read `CONTRIBUTING.md` — understand the branching and commit rules.
4. Browse `backlog/PRODUCT_BACKLOG.md` — see what is planned.
5. Pick up a `🟡 Ready` story from the current sprint, assign it to yourself, and get started!
