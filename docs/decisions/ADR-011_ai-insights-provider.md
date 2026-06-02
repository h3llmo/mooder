# ADR-011 — AI Insights Provider: TBD with Mock Interface

| Field | Value |
|---|---|
| **Date** | 2026-06-02 |
| **Status** | Accepted (provider TBD) |
| **Deciders** | Product owner |

## Context

The AI insights engine (EP-003) requires an LLM or NLP provider to analyse chat content. The specific provider is not yet chosen. Development must not be blocked by this decision — features must be buildable and testable before the provider is selected.

## Decision

### Provider

**TBD.** The AI provider decision is deferred. Candidates include OpenAI, Anthropic, Google Gemini, Mistral, and open-source self-hosted models. An ADR amendment will capture the final choice.

### Interface contract

An **AI interface abstraction layer** is defined in the backend before any provider is wired in. All feature code calls this interface — never the provider SDK directly.

```
AIInsightsService (interface)
    └── FakeAIInsightsService   ← used locally and in tests
    └── <ProviderAIInsightsService>   ← wired in when provider is chosen
```

### Mock implementation: Faker

Until the real provider is chosen, a **Faker-based mock** (`FakeAIInsightsService`) is the default implementation:
- Returns deterministic, realistic-looking insight objects seeded from the input.
- Enables full end-to-end feature development and UI work without a real AI call.
- Activated by `AI_PROVIDER=fake` (default on local and in CI).
- The mock data format must exactly match the real provider's expected output format so switching providers requires zero changes to calling code.

### Environment config

| Environment | Default provider |
|---|---|
| Local | `fake` (Faker mock) |
| CI | `fake` |
| Staging | `fake` until real provider is chosen, then real |
| Production | Real provider only |

## Consequences

- Development of EP-003 features can begin immediately without a provider contract.
- The abstraction layer ensures the provider can be swapped or A/B tested with no impact on feature code.
- The Faker mock must be maintained to stay in sync with the interface contract as it evolves.

## Open questions

- [ ] Which AI provider? → To be decided before staging go-live. Criteria: cost, privacy (is conversation data sent off-platform?), latency, EU data residency.
- [ ] On-device vs server-side processing? → Privacy implication: on-device keeps data off any third-party server.
