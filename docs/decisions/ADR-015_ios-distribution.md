# ADR-015 — iOS Distribution Pipeline

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted |
| **Deciders** | Product owner |
| **Scope** | MVP / Proof of Concept phase |

## Context

The iOS app needs a distribution path for testing and validation. Apple's App Store review process and enterprise distribution requirements are non-trivial and not appropriate for a proof-of-concept phase.

## Decision

### MVP / POC scope

| Environment | Distribution | Notes |
|---|---|---|
| Local | Xcode Simulator | Developer machine only — no device signing required |
| Staging | **TestFlight** | Internal testing group; up to 100 internal testers |
| Production | **Out of scope for MVP** | App Store submission deferred to post-POC |

App Store submission, Apple review compliance, and any production iOS distribution constraints are **explicitly not part of the MVP**. The goal of Phase 1 is to deliver a proof of concept and validate the product — not to publish to the App Store.

### TestFlight pipeline (staging)

```
GitHub Actions (macOS runner)
  → build & sign iOS app (Xcode + provisioning profile)
  → upload to App Store Connect via `altool` or `xcrun notarytool`
  → TestFlight automatically notifies internal testers
```

Signing credentials (provisioning profiles, certificates) are stored in GitHub Actions secrets — never committed to the repository.

### What is deferred

- App Store review submission
- App Store metadata (screenshots, descriptions, ratings)
- Age ratings and content policy compliance
- In-app purchase / subscription setup
- Apple's data privacy "nutrition label" (will be required at App Store submission)

## Consequences

- TestFlight provides a fast, friction-free distribution path for internal QA and stakeholder demos.
- The iOS build pipeline (Xcode + GitHub Actions macOS runner) must be set up before staging iOS testing can begin.
- App Store submission will require a separate ADR amendment covering Apple-specific compliance requirements when the time comes.

## Alternatives considered

| Alternative | Why not chosen |
|---|---|
| App Store from day one | Review process too slow for iterative POC; not needed until product is validated |
| Ad-hoc distribution (IPA sideloading) | Device UDID management is cumbersome; TestFlight is simpler |
| Expo / React Native cloud build | Not applicable — native Swift app |
