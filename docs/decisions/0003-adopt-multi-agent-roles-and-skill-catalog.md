# 0003 Adopt Multi-Agent Roles And Skill Catalog

## Status

Proposed on 2026-05-10

## Date

2026-05-10

## Context

Repository AI guidance already encodes a multi-agent execution model in `.agents/references/workflow.md`:

- Workflow modes `M0: direct`, `M1: assisted`, `M2: delegated`, `M3: parallel`, `M4: gated`.
- Named roles: `Coordinator`, `Worker`, `Reviewer`, `Verifier`, `Specialist`.
- Handoff packets and durable workflow state under `.agents/context/handoffs/`, `.agents/context/workers/`, `.agents/context/reviews/`, `.agents/context/verifications/`, and `.agents/context/specialists/`.
- `M3` and `M4` gates such as `Code Review`, `Verification`, `Security Review?`, `Docs Review?`, and `Release/Operations Gate?`.

Two `.agents/skills/` bundles exist today (`gh-fix-ci`, `gh-fix-security-quality`).
They show the intended skill shape: `SKILL.md` frontmatter with `name`, `description`, and `metadata.short-description`, an explicit `Read Set`, declared inputs, scripts, and a workflow with stop conditions.

In practice the policy is not yet matched by the implementation:

- One agent typically role-plays Coordinator, Planner, Worker, Reviewer, Verifier, and Specialist in the same context window.
- Skills cover only GitHub Actions failure triage today; planning, execution, validation, review, security, and integration loops are re-derived per session.
- `.agents/context/*` is referenced by guidance but not materialized in the repository tree, so agents have no place to write durable state.
- AI guidance does not declare a per-role read-set, so each agent loads the union of relevant references instead of the slice its role needs.

The repository runs on Windows with `./build.ps1` as the validation wrapper, uses spec-driven development, and treats `main` as the integration branch for completed work.
The current AI guidance set was authored primarily for Codex and GPT-5.x; Junie on Opus 4.7 reads `.junie/AGENTS.md` plus root `AGENTS.md`.
Both agent platforms benefit from narrower per-role context and from procedure-encoded skills that cut repeated reasoning.

ADR 0001 introduced pre-planning artifacts; ADR 0002 aligned lifecycle vocabulary.
This ADR implements the principle decision in ADR 0004 by defining *who acts* and *which procedures they invoke* during execution.

## Decision

Adopt a six-role agent roster, a starter skill catalog, and the supporting state and read-set rules below.

### Agent Roster

Make these six roles explicit identities with separate prompts, separate context, and separate tool whitelists.
Each role maps to an existing role in `.agents/references/workflow.md` so this ADR does not introduce new ownership terms.

| Role | Modes | Tool capability | Owning reference | Responsibility |
| --- | --- | --- | --- | --- |
| Coordinator | every mode | read + route; edits only Coordinator-owned shared files | `.agents/references/workflow.md` | Workflow shape, plan routing, shared files, integration order, conflict resolution, final validation, final reporting. |
| Planner | before `M2: delegated` or higher | read + plan-file edit | `.agents/references/planning.md`, `.agents/references/plan-template.md` | Author or revise `.agents/plans/PLAN_*.md`, decision log, readiness review. |
| Worker (Coder) | `M2: delegated`, `M3: parallel` | edit + run validation on its slice | `.agents/references/execution.md`, `.agents/references/code-style.md` | Implement one approved write scope, run smallest sufficient validation, commit, report. |
| Reviewer (Sidecar) | `M1: assisted`, `M4: gated` | read-only | `.agents/references/reviews.md`, `.agents/references/documentation.md` | Diff review, contract drift, gate decision. |
| Verifier (Sidecar) | `M1: assisted`, `M4: gated` | run-only, no file edits | `.agents/references/testing.md`, `.agents/references/troubleshooting.md` | Independent validation, benchmark, contract or compatibility evidence. |
| Specialist (Sidecar) | `M4: gated` | role-scoped | `.agents/references/reviews.md` (security), `.agents/references/releases.md`, `.agents/references/documentation.md` | Security review, docs review, release readiness, or other specialist gates. |

The Coordinator owns integration to `main`.
Reviewer, Verifier, and Specialist sidecars never edit Worker-owned files.
Two editing agents must not own the same write scope at the same time, in line with `workflow.md`.

### Starter Skill Catalog

Add the following skill bundles under `.agents/skills/`, each as a `SKILL.md` plus optional scripts, in priority order.
Existing `gh-fix-ci` and `gh-fix-security-quality` remain.

| Skill | Primary caller | Purpose |
| --- | --- | --- |
| `repo-task-execute` | Worker | Encode the `execution.md` loop: spec-first, smallest change, validate, review, commit. |
| `run-validation` | Worker, Verifier | Pick the smallest sufficient `./build.ps1` invocation per `testing.md` change-type rules. |
| `diff-review` | Reviewer (and Worker self-pre-check) | Read diff, check contract drift, name blocking issues, return a gate decision. |
| `handoff-pack` | Coordinator | Produce a complete handoff packet to `.agents/context/handoffs/` with objective, role, read scope, write scope, expected output, validation target, stop conditions, and reporting format. |
| `repo-plan-author` | Planner | Author a `PLAN_*.md` from `.agents/references/plan-template.md` and run readiness review. |
| `integrate-branch` | Coordinator | Merge-vs-cherry-pick decision, conflict resolution, post-merge validation. |
| `security-review` | Specialist | Threat model, dependency, and SAST review; gate decision. |
| `openapi-contract-check` | Reviewer or Verifier | Re-run OpenAPI compatibility, refresh approved baseline only on intentional contract change. |
| `triage-flaky-test` | Verifier | Re-run, isolate, decide quarantine versus fix. |
| `release-cut` | Specialist (release) | Wrap `releases.md` and `release-checklist.md` after integration on `main`. |
| `select-mode-and-skills` | Coordinator | Given a user request, output `(mode, agent roster, skill chain, handoff packet template)`. |

Skills must reference repository rules instead of inlining them.
Each `SKILL.md` declares a `Read Set` block that names the always-loaded guides and the conditional guides that load only when a stated trigger fires.

### Workflow Composition

Use the following default composition when a user request is accepted:

1. Coordinator classifies the request as bounded task, single plan task, whole plan, or release work.
2. If product intent or acceptance criteria are unclear, Coordinator invokes the Planner with `repo-plan-author`; the resulting plan is reviewed by a Reviewer sidecar before execution.
3. For bounded execution, Coordinator hands off to one Worker via `handoff-pack`. Worker uses `repo-task-execute` and `run-validation`. A Reviewer sidecar runs `diff-review`. A Verifier sidecar runs independent validation through `run-validation`.
4. For multi-slice execution (`M3: parallel`), Coordinator issues N independent handoff packets to N Workers on disjoint write scopes, with continuous Reviewer and Verifier sidecars (`M4: gated`) when gates are required.
5. Coordinator integrates accepted Worker branches one at a time using `integrate-branch`, runs final validation, and records terminal state in the plan or workflow state.
6. Release work runs only after integration on `main` and is owned by a release Specialist invoking `release-cut`, gated by Verifier.

### Mode Escalation Defaults

Pick the lowest mode that keeps gates meaningful:

- documentation-only or trivial fixes -> `M0: direct`
- a risky single change that benefits from independent diff review -> `M1: assisted`
- a self-contained bounded change -> `M2: delegated`
- multi-package or multi-slice work that is genuinely disjoint -> `M3: parallel`
- release candidates, cross-cutting refactors, or security-sensitive changes -> `M4: gated`

### Durable State

Materialize the workflow state directories declared in `workflow.md` as real, version-controlled paths:

- `.agents/context/handoffs/`
- `.agents/context/workers/`
- `.agents/context/reviews/`
- `.agents/context/verifications/`
- `.agents/context/specialists/`

Use a stable file name shape `<plan_stem_or_topic>__<role>.md`, as already required by `workflow.md`.
All cross-agent state lives in these files, not in conversation memory.

### Per-Role Read Set

Add a per-role read-set table to `.agents/references/workflow.md` so each agent loads only the references its role demands.
Example shape (authoritative content lives in `workflow.md` after acceptance):

| Role | Always loaded | Conditional |
| --- | --- | --- |
| Coordinator | `AGENTS.md`, `workflow.md`, `documentation.md` | active plan, `releases.md` for release work |
| Planner | `AGENTS.md`, `planning.md`, `plan-template.md` | `documentation.md`, `architecture.md`, ADR or PRD when referenced |
| Worker | `AGENTS.md`, `execution.md`, `code-style.md` | `testing.md` on validation, `documentation.md` on contract artifacts |
| Reviewer | `AGENTS.md`, `reviews.md`, `documentation.md` | `code-style.md`, contract artifacts |
| Verifier | `AGENTS.md`, `testing.md`, `troubleshooting.md` | `command-wrapper.md`, `gradle-task-graph.md` |
| Specialist | `AGENTS.md`, `reviews.md` | `releases.md`, `release-checklist.md`, `documentation.md` |

## Required Follow-Up Changes

If accepted, update these artifacts together:

1. `.agents/references/workflow.md`
   - Add a per-role read-set table.
   - Make the six-role roster explicit as named identities, not only as ownership labels.
   - Keep `M0` through `M4` identifiers, definitions, and integration rules unchanged while using the accepted labels.

2. `.agents/references/documentation.md`
   - Route skill-bundle ownership to `.agents/skills/<skill-name>/SKILL.md`.
   - Route durable workflow state ownership to `.agents/context/*` directories.
   - Reaffirm that skills must reference, not inline, repository rules.

3. `.agents/references/references-rules.md`
   - State that skills are tactics, not governance, and that reference files remain the single source of standing rules.

4. `.agents/skills/`
   - Add the starter skills listed above in the priority order shown.
   - Each new skill follows the existing `gh-fix-ci/SKILL.md` shape: frontmatter, `Read Set`, `Inputs`, `Workflow`, stop conditions, scripts where useful.

5. `.agents/context/`
   - Create the five state directories with a `README.md` describing the file-name shape and which role writes which directory.
   - Keep the directories non-empty in version control with a stub `README.md` so agents treat them as real.

6. `AGENTS.md`
   - Add a short cross-reference to the new agent roster and skill catalog without duplicating their content.
   - Update the `.agents/` description so it is not labeled as Codex-specific.

7. `.junie/AGENTS.md`
   - Add a brief mode-to-role mapping so Junie selects the right role bundle in `[CHAT]`, `[ADVANCED_CHAT]`, `[FAST_CODE]`, `[CODE]`, `[RUN_VERIFY]`, and `[SETUP]`.

8. `WORKING_WITH_AI.md`
   - Document the agent roles, the skill catalog, and the multi-agent workflow at developer-facing depth.
   - Reference but do not duplicate `.agents/references/workflow.md`.

9. `ROADMAP.md`
   - Track the rollout as one or more entries until the starter skills land and `.agents/context/*` is materialized.

10. Existing active plans
    - Update only those plans whose execution shape would change under the new roster; do not rewrite archived plans.

## Consequences

Benefits:

- independent judgment between author, reviewer, and verifier replaces single-agent self-attestation
- per-role context shrinks token usage and improves model quality, especially on long-context models such as Opus 4.7
- recurring procedures move from session reasoning into durable skills, which makes outputs repeatable across agents and sessions
- handoff packets and `.agents/context/*` state make multi-agent runs restartable instead of conversation-bound
- mode escalation defaults prevent over-using `M4: gated` on small work
- the repository stays platform-neutral: Codex, Junie, and other agents can adopt the same roster and skills

Costs:

- coordination overhead grows; handoff packets must actually be written
- skill catalog needs ongoing maintenance and should be linted and smoke-tested in CI
- multi-agent runs increase total tokens even while reducing wall-clock time
- write-scope discipline must be enforced or `Replan?` triggers will fire often
- some users may prefer a single-agent flow for short tasks; default to `M0` or `M2` to keep the cost honest

## Alternatives Considered

### Keep One Generalist Agent

Lower coordination overhead and no new infrastructure.
Loses independent review, mixes contexts, and makes parallel execution unsafe.
Does not unlock `M3: parallel` or `M4: gated` even though `workflow.md` already defines them.

### Add Roles Without A Skill Catalog

Roles alone improve independence but force every role to re-derive procedures (validation choice, diff review, handoff packet shape) per session.
Wastes tokens on Opus 4.7 and reduces output consistency across runs.

### Add Skills Without Explicit Roles

Skills alone help with repeatability but leave self-review and self-validation in place.
Most quality wins come from the Coder and Reviewer split, which requires distinct agents.

### Adopt An External Multi-Agent Framework Wholesale

Frameworks such as imported `multi-agent-execution.md` rules introduce vocabulary and tooling that overlap with `workflow.md`.
The repository already consolidated to one workflow owner in `PLAN_multi_agent_workflow_consolidation.md`.
Adopting a third vocabulary layer would re-introduce the duplication that consolidation removed.

### Make `.agents/context/*` Conversation-Only

Avoids new directories but breaks restartability and remote handoff.
Contradicts the durable-state expectation already declared in `workflow.md`.

## Confirmation

This ADR is proposed and not yet accepted.
Acceptance requires an explicit user decision recorded in `## Status`.
Implementation should be verified by manual documentation review that:

- `.agents/references/workflow.md` declares the six-role roster and the per-role read set
- `.agents/skills/` contains the starter skill bundles in `SKILL.md` shape with declared `Read Set` blocks
- `.agents/context/` directories exist with a `README.md` describing role-to-directory ownership
- `AGENTS.md` and `.junie/AGENTS.md` cross-reference the roster and catalog without duplicating their content
- `WORKING_WITH_AI.md` explains the multi-agent workflow at developer-facing depth
- `ROADMAP.md` tracks the rollout until the starter skills land

## Links

- Principle decision: `docs/decisions/0004-adopt-skill-first-multi-agent-workflow.md`
- `.agents/references/workflow.md`
- `.agents/references/planning.md`
- `.agents/references/execution.md`
- `.agents/references/plan-execution.md`
- `.agents/references/testing.md`
- `.agents/references/reviews.md`
- `.agents/references/documentation.md`
- `.agents/references/references-rules.md`
- `.agents/skills/gh-fix-ci/SKILL.md`
- `.agents/skills/gh-fix-security-quality/SKILL.md`
- `.agents/plans/PLAN_multi_agent_workflow_consolidation.md`
- ADR 0001: `docs/decisions/0001-adopt-pre-planning-artifacts.md`
- ADR 0002: `docs/decisions/0002-align-lifecycle-vocabulary-with-industry-practice.md`
- ADR overview: https://adr.github.io/
