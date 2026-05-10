# Working With AI

This is the human-facing guide for asking AI to help with this repository.
It is a navigation guide, not a second copy of the AI runbooks.

Use this file when you want AI help with planning, implementation, validation, review, or release preparation.
Use [AGENTS.md](../AGENTS.md) and the focused guides under [.agents/references/](../.agents/references/) for the rules the AI must follow.

## Start Here

Before asking AI to change the repository, identify:

- the behavior, documentation, or workflow you want changed
- the lifecycle phase from [Development Lifecycle](DEVELOPMENT_LIFECYCLE.md)
- the target artifact, plan, or file path
- the constraints and non-goals
- the validation or review evidence you expect before handoff

A useful request shape is:

```text
Task:
Goal:
Phase:
Target artifacts:
Constraints:
Definition of done:
```

Examples:

```text
Create Plan
topic: add candidate search filtering
```

```text
Implement Plan
plan_file: .agents/plans/PLAN_CANDIDATE_SEARCH.md
```

```text
Run Required Validation
plan_file: .agents/plans/PLAN_CANDIDATE_SEARCH.md
change: candidate search filtering API
```

If the target artifact, scope, or behavior is ambiguous, expect the AI to ask a targeted clarification question before editing.

## What To Load

Use the smallest owner set that can answer the request:

| Need | Start With |
| --- | --- |
| Project overview and implemented scope | [README.md](../README.md) |
| Environment setup, dev container, local shell, and `.env` | [SETUP.md](../SETUP.md) |
| Local development commands, CI reproduction, and local troubleshooting | [LOCAL_DEVELOPMENT.md](LOCAL_DEVELOPMENT.md) |
| Contributor workflow and PR expectations | [CONTRIBUTING.md](../CONTRIBUTING.md) |
| Lifecycle and artifact routing for humans | [Development Lifecycle](DEVELOPMENT_LIFECYCLE.md) |
| Deployment and runtime operations runbooks | [OPERATIONS.md](OPERATIONS.md) |
| Active and planned work | [ROADMAP.md](../ROADMAP.md) |
| Product intent and non-goals | [DESIGN.md](DESIGN.md) |
| Durable decisions | [docs/decisions/](decisions/) |
| Product requirements | [docs/requirements/](requirements/) |
| Standalone behavior specs | [docs/specs/](specs/) |
| Separate frontend AI contract | [FRONTEND_AI_CONTRACT.md](FRONTEND_AI_CONTRACT.md) |
| Repository-local AI rules | [AGENTS.md](../AGENTS.md) |
| AI planning, execution, workflow, validation, and review rules | [.agents/references/](../.agents/references/) |
| Reusable task prompts | [.agents/tasks/README.md](../.agents/tasks/README.md) |
| Repo-local workflow skills | [.agents/skills/README.md](../.agents/skills/README.md) |

Detailed task prompts, active plans, archived reports, and skill bodies are on-demand material.
Load them only when the task title, active plan, owner guide, or current problem calls for them.

## Working Model

This repository uses spec-driven development.
The normal AI-assisted loop is:

1. Frame the change in terms of behavior or documented responsibility.
2. Identify the governing spec, contract, plan, ADR, PRD, or guide.
3. Update the governing artifact before or alongside the implementation.
4. Make the smallest coherent change.
5. Run the validation that matches the change.
6. Review the diff for bugs, drift, missing validation, and security-sensitive impact.

Human responsibilities stay with the developer.
The developer owns scope, product intent, approval of tradeoffs, diff review, validation acceptance, and release decisions.

## Common Requests

For planning:

- ask AI to inspect the relevant owner documents
- ask it to name open questions and non-goals
- ask it to create or revise an execution plan under [.agents/plans/](../.agents/plans/) only when the work is large enough to need one

For implementation:

- point AI at the approved plan or the exact task
- keep the requested scope task-sized
- ask AI to record validation evidence and update active tracking artifacts
- ask for a commit only when the task is complete and the repository commit rules apply

For validation and review:

- ask AI to use [.agents/references/testing.md](../.agents/references/testing.md) for command choice
- ask AI to use [.agents/references/reviews.md](../.agents/references/reviews.md) for a bug-risk and drift review
- expect security review when auth, sessions, secrets, deployment-facing config, CI permissions, release paths, or externally exposed endpoints change

For release or operations:

- keep release preparation separate from implementation work
- use [OPERATIONS.md](OPERATIONS.md) for deployment and runtime runbooks
- use [.agents/references/releases.md](../.agents/references/releases.md) only after the implementation is integrated on `main`
- use [.agents/references/operations.md](../.agents/references/operations.md) for post-release deployment, rollback, hotfix, patch, backport, or deprecation routing

## Workflow Modes

Most work should use `M0: direct`: one agent, one working tree, clear task scope.

When you want delegation, sub-agents, worktrees, sidecars, or later integration of worker output, ask for that explicitly.
The AI workflow guide [workflow.md](../.agents/references/workflow.md) owns the details.

The current mode vocabulary is:

- `M0: direct`: one agent handles the work directly
- `M1: assisted`: read-only review, verification, or specialist help
- `M2: delegated`: one Worker owns one bounded write scope
- `M3: parallel`: multiple disjoint Worker scopes move in parallel
- `M4: gated`: independent review, verification, security, docs, release, or specialist gates are required

## Developer Habits

- ask AI to name the governing artifact before it edits
- tell AI what is in scope and out of scope
- prefer exact paths and task names over broad repository requests
- ask AI to report blockers explicitly
- keep release work separate from implementation work
- review the final diff, validation evidence, and contract impact yourself

## When To Slow Down

Stop and clarify before continuing when:

- the intended behavior is still ambiguous
- public API behavior is changing without clear contract updates
- an approved plan hides unresolved scope or validation questions
- multiple workers would need the same files
- requested release work is not integrated onto `main`

Speed is useful only when scope, ownership, and validation remain clear.
