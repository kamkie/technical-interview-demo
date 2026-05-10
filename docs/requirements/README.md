# Product Requirements Documents

This directory holds Product Requirements Documents (PRDs) for broad or ambiguous user-facing product intent that needs goals, requirements, non-goals, and acceptance criteria before implementation.

For the full human-facing documentation map, see [docs/README.md](../README.md).
For lifecycle and artifact-routing guidance (ADR vs PRD vs standalone spec vs plan), see [DEVELOPMENT_LIFECYCLE.md](../DEVELOPMENT_LIFECYCLE.md).

## When To Add A PRD

Add a PRD when a user-facing change is broad, ambiguous, or affects multiple surfaces and the team needs alignment on intent before scoping implementation. Typical triggers:

- A new product capability is proposed and the problem, users, and success criteria are not yet shared.
- An existing capability needs a significant scope change with new non-goals and acceptance criteria.
- The change spans multiple modules or contracts and requires explicit requirements before specs or plans.

If behavior is narrow and contract-shaped, prefer a standalone spec in [docs/specs/](../specs/).
If the decision is architectural or process-level rather than product intent, use an ADR in [docs/decisions/](../decisions/).
For task-sized implementation checkpoints, use an execution plan under [.agents/plans/](../../.agents/plans/).

## How To Add A PRD

1. Copy [PRD_TEMPLATE.md](PRD_TEMPLATE.md) to a descriptive kebab-case filename in this directory.
2. Fill in the template sections (goals, users, requirements, non-goals, acceptance criteria, links).
3. Link the new PRD from any related ADR, standalone spec, execution plan, or [ROADMAP.md](../../ROADMAP.md) entry.
4. Update the index below.

## Accepted PRDs

_None yet._

## Template

- [PRD_TEMPLATE.md](PRD_TEMPLATE.md)
