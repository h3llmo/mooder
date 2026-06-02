# Contributing to Mooder

Thank you for contributing! Please read this guide before submitting changes.

## Branching strategy

| Branch | Purpose |
|---|---|
| `main` | Stable, production-ready code |
| `develop` | Integration branch — all features merge here first |
| `feature/<name>` | New feature or user story |
| `fix/<name>` | Bug fix |
| `chore/<name>` | Non-functional improvement (tooling, docs, refactor) |

## Commit messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <short description>

Examples:
feat(backend): add user authentication endpoint
fix(ios-app): correct navigation bar color
docs(backlog): add EP-002 epic definition
```

## Pull requests

1. Branch off `develop`.
2. Link the PR to the relevant user story or backlog item.
3. At least one peer review is required before merge.
4. Squash commits when merging into `develop`.

## Backlog & issue tracking

- All work items live in [`backlog/`](backlog/).
- Pick up a story from the current sprint before starting work.
- Update the story status when you start and when you finish.

## Questions?

Open a discussion or reach the team via the agreed communication channel.
