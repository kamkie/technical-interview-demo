# Execution Plans

This directory holds active execution plans (`PLAN_*.md`) that break selected work into task-sized implementation checkpoints.
`PLAN_TEMPLATE.md` is the reusable plan skeleton and is not an active plan.

For the full human-facing documentation map, see [docs/README.md](../../docs/README.md).
For lifecycle and artifact-routing guidance (ADR vs PRD vs standalone spec vs plan), see [docs/DEVELOPMENT_LIFECYCLE.md](../../docs/DEVELOPMENT_LIFECYCLE.md).
For the detailed AI planning and plan-execution workflow, see [.agents/references/planning.md](../references/planning.md) and [.agents/references/plan-execution.md](../references/plan-execution.md).

## When To Add A Plan

Add an execution plan when selected work needs an explicit, ordered set of implementation checkpoints before or during execution. Typical triggers:

- A change spans multiple files, modules, or steps that benefit from a sequenced checklist.
- An ADR, PRD, or standalone spec has been accepted and now needs a concrete implementation order with validation checkpoints.
- A refactor, migration, or documentation move must land in stages so each step can be reviewed and validated independently.

For durable architecture or process decisions, use an ADR in [docs/decisions/](../../docs/decisions/) instead.
For broad or ambiguous user-facing intent, use a PRD in [docs/requirements/](../../docs/requirements/).
For canonical behavior or contract truth, use a standalone spec in [docs/specs/](../../docs/specs/).

## How To Add A Plan

1. Create a new file named `PLAN_<short_snake_case_title>.md` in this directory.
2. Start from [PLAN_TEMPLATE.md](PLAN_TEMPLATE.md) and follow the structure described in [.agents/references/planning.md](../references/planning.md): provenance, context, scope, ordered tasks with validation, links to governing specs.
3. Link the new plan from any related ADR, PRD, standalone spec, or [ROADMAP.md](../../ROADMAP.md) entry.
4. Execute the plan using the loop in [.agents/references/plan-execution.md](../references/plan-execution.md); when the plan is complete, move it under [.agents/archive/](../archive/).

## Active Plans

Do not list [PLAN_TEMPLATE.md](PLAN_TEMPLATE.md) here.

| Plan | Purpose |
| --- | --- |
| [PLAN_human_documentation_split.md](PLAN_human_documentation_split.md) | Human documentation split (ADR 0006 follow-up) |

## Archived Plans

Completed or superseded plans live under [.agents/archive/](../archive/).
