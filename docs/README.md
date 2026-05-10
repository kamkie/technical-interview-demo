# Documentation

This index is the human-facing map for repository documentation.
Use [README.md](../README.md) for the shortest project overview and supported-scope summary.

## Start Here

| Need | Document |
| --- | --- |
| Project overview and supported scope | [README.md](../README.md) |
| Environment setup, dev container, local shell, and `.env` | [SETUP.md](../SETUP.md) |
| Local development commands, CI reproduction, and local troubleshooting | [LOCAL_DEVELOPMENT.md](LOCAL_DEVELOPMENT.md) |
| Contributor workflow and PR expectations | [CONTRIBUTING.md](../CONTRIBUTING.md) |
| Development lifecycle and artifact routing | [DEVELOPMENT_LIFECYCLE.md](DEVELOPMENT_LIFECYCLE.md) |
| AI collaboration guide | [WORKING_WITH_AI.md](WORKING_WITH_AI.md) |
| Deployment and runtime operations | [OPERATIONS.md](OPERATIONS.md) |
| Product intent, non-goals, and contract direction | [DESIGN.md](DESIGN.md) |
| Active and planned work | [ROADMAP.md](../ROADMAP.md) |
| Released history | [CHANGELOG.md](../CHANGELOG.md) |
| Durable lessons | [LEARNINGS.md](LEARNINGS.md) |

## Planning And Decisions

| Artifact Type | Location | Use When |
| --- | --- | --- |
| ADRs | [decisions/](decisions/) | Durable architecture, workflow, contract-policy, security, documentation-ownership, or repository-process decisions |
| PRDs | [requirements/](requirements/) | Broad or ambiguous user-facing product intent needs goals, requirements, non-goals, and acceptance criteria |
| Standalone specs | [specs/](specs/) | Behavior or contract truth is not already clear in executable specs or published docs |
| Execution plans | [.agents/plans/](../.agents/plans/) | Selected work needs task-sized implementation checkpoints |

## Contracts And Integration

- [FRONTEND_AI_CONTRACT.md](FRONTEND_AI_CONTRACT.md): generated import-ready backend contract for a separate first-party frontend repository's AI agents
- [src/docs/asciidoc/](../src/docs/asciidoc/): REST Docs source pages for the packaged `/docs` output
- [src/test/resources/openapi/approved-openapi.json](../src/test/resources/openapi/approved-openapi.json): approved machine-readable public API contract
- [src/manualTests/http/examples/](../src/manualTests/http/examples/): reviewer-focused HTTP examples
- [src/manualTests/http/suites/](../src/manualTests/http/suites/): semi-automated IntelliJ HTTP Client manual-regression suites

## Operations

Use [OPERATIONS.md](OPERATIONS.md) for deployment contract, release-artifact verification, Docker image operation, container smoke, post-deploy smoke, healthy runtime expectations, upgrade and rollback, Kubernetes, Helm, monitoring, OAuth runtime setup, and deployment troubleshooting.
