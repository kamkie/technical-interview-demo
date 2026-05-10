# Architecture Decision Records

This directory holds Architecture Decision Records (ADRs) for durable architecture, workflow, contract-policy, security, documentation-ownership, and repository-process decisions.

For the full human-facing documentation map, see [docs/README.md](../README.md).
For lifecycle and artifact-routing guidance (ADR vs PRD vs standalone spec vs plan), see [DEVELOPMENT_LIFECYCLE.md](../DEVELOPMENT_LIFECYCLE.md).

## When To Add An ADR

Add an ADR when a decision is durable and would otherwise be lost in chat, commit messages, or a single PR description. Typical triggers:

- A new architectural pattern, framework, or contract policy is adopted or retired.
- Repository workflow, ownership boundaries, or AI collaboration rules change in a way future contributors must respect.
- A previous ADR needs to be superseded; record the new decision and mark the old one accordingly.

For short-lived implementation choices use an execution plan under [.agents/plans/](../../.agents/plans/) instead.

## How To Add An ADR

1. Copy [ADR_TEMPLATE.md](ADR_TEMPLATE.md) to `NNNN-short-kebab-title.md`, where `NNNN` is the next zero-padded sequence number.
2. Fill in Status, Date, Provenance, Context, Decision, Consequences, Alternatives Considered, Confirmation, and Links.
3. If the ADR supersedes an earlier one, update the earlier ADR's Status to point at the new file and move the earlier file under [.agents/archive/](../../.agents/archive/) only when explicitly archived.
4. Update incoming references and add the new ADR to the index below.

## Accepted ADRs

| ADR | Title |
| --- | --- |
| [0001](0001-adopt-pre-planning-artifacts.md) | Adopt Pre-Planning Artifacts |
| [0002](0002-align-lifecycle-vocabulary-with-industry-practice.md) | Align Lifecycle Vocabulary With Industry Practice |
| [0003](0003-adopt-multi-agent-roles-and-skill-catalog.md) | Adopt Multi-Agent Roles And Skill Catalog |
| [0004](0004-adopt-skill-first-multi-agent-workflow.md) | Adopt Skill-First Multi-Agent Workflow |
| [0005](0005-adopt-operations-and-deployment-owner-guide.md) | Adopt Operations And Deployment Owner Guide |
| [0006](0006-split-human-documentation-and-ai-workflow-guides.md) | Split Human Documentation And AI Workflow Guides |

## Template

- [ADR_TEMPLATE.md](ADR_TEMPLATE.md)
