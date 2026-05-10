# 0004 Adopt Skill-First Multi-Agent Workflow

## Status

Accepted on 2026-05-10

## Date

2026-05-10

## Context

The repository already defines AI work around spec-driven development, scoped context loading, executable validation, review, and integration rules.
It also has workflow guidance for solo execution, sidecars, bounded workers, parallel slices, and full sidecar gates.

ADR 0003 records the concrete implementation decision for the multi-agent roster, starter skill catalog, durable state directories, and per-role read sets.
This ADR records the principle decision: adopt the skill-first, orchestrator-led operating model, then use ADR 0003 as the first implementation decision.
It also proposes clearer workflow-mode labels while preserving the existing `M0` through `M4` identifiers and mode semantics.

The repository needs a decision that separates:

- when to use multiple agents
- which responsibilities need independent agents
- which repeatable procedures belong in skills
- how the orchestrator integrates and validates results
- how to avoid coordination overhead on small tasks

This decision is about AI workflow architecture, not product runtime behavior.
It does not change application code, public APIs, release sequencing, or validation requirements by itself.

## Decision

Adopt a skill-first, orchestrator-led multi-agent workflow for non-trivial AI work when the change benefits from parallelism, independent judgment, or role-specific expertise.

Agents are accountable workers.
Skills are reusable operating procedures.
Durable workflow knowledge should live in skills, project guidance, specs, tests, and docs rather than being trapped in one agent prompt.

The orchestrator remains responsible for the task end to end.
It classifies the request, identifies governing specs and docs, decides whether multi-agent execution is useful, assigns bounded work, integrates outputs, performs final review, records validation, and handles the final commit or handoff.

Use the smallest effective number of agents.
Routine edits should remain solo when delegation adds more coordination cost than value.

Use these workflow-mode labels without removing the stable `M0` through `M4` identifiers:

| Mode | Meaning |
| --- | --- |
| `M0: direct` | One agent handles the work directly. |
| `M1: assisted` | A read-only reviewer, verifier, or specialist helps without editing. |
| `M2: delegated` | One worker owns one bounded write scope. |
| `M3: parallel` | Multiple disjoint slices run in parallel. |
| `M4: gated` | Independent review, verification, security, docs, release, or specialist gates are part of the workflow. |

The rename is vocabulary-only.
It does not change mode ordering, ownership rules, delegation limits, sidecar behavior, integration responsibility, or validation requirements.
Use `gated` rather than `agentic` for `M4` because every mode is agentic in some sense; `M4` is distinct because independent gates are required.

Recommended role model, using the six-role implementation vocabulary from ADR 0003:

| Role | Primary responsibility | Typical skills or guidance |
| --- | --- | --- |
| Coordinator | Own intake, routing, decomposition, shared files, integration, final validation evidence, and handoff | workflow, planning, review |
| Planner | Clarify scope, acceptance criteria, risks, dependencies, and spec gaps | planning |
| Worker | Implement one bounded code, test, docs, or support-file slice | coding, testing |
| Reviewer | Check correctness, spec alignment, maintainability, security risk, and hidden scope expansion | review, security-review |
| Verifier | Define and run validation, investigate failures, and report residual risk | testing, troubleshooting |
| Specialist | Own role-scoped gates such as architecture, documentation, security, release readiness, or other specialist checks | architecture, documentation, security-review, release |

Exploration, documentation review, and release readiness are Specialist activities unless a later accepted ADR creates separate top-level identities.
Release work remains inactive unless explicitly requested.

Recommended execution shape:

```text
User request
  -> Orchestrator intake
  -> Spec, planning, and artifact-routing check
  -> Task decomposition
  -> Parallel exploration when useful
  -> Bounded implementation by worker agents
  -> Dedicated verification pass
  -> Dedicated review and security review when needed
  -> Documentation and spec alignment
  -> Orchestrator integration
  -> Final validation
  -> Commit or handoff
```

Recommended skill catalog:

- `planning`: intake, scope, acceptance criteria, spec-first workflow, readiness review
- `coding`: repo conventions, edit boundaries, implementation rules, smallest-change expectations
- `testing`: validation commands by change type, evidence recording, benchmark and contract gates
- `troubleshooting`: failure diagnosis workflow and recovery decisions
- `review`: code review checklist, severity model, spec drift checks, hidden-scope checks
- `security-review`: auth, input handling, secrets, dependency, CI, release, and data-handling checks
- `documentation`: artifact ownership, cross-file alignment, and documentation-only validation
- `workflow`: branch, worktree, delegation, integration, sidecar, and handoff mechanics
- `release`: versioning, changelog, tagging, artifact verification, and publication sequencing

Execution rules:

- use multi-agent execution only when there is real parallelism, independent review value, or a bounded specialist activity
- give every delegated agent a concrete objective, read scope, write scope, stop condition, expected output, and validation target
- keep editing scopes disjoint across worker agents
- keep shared files under orchestrator ownership unless the plan explicitly assigns them
- do not let agents duplicate each other's work
- do not delegate an immediate critical-path blocker when local execution would move faster
- use sidecar agents for independent read-only review, verification, or specialist checks
- require each agent to report changed files, validation performed, blockers, risks, and integration readiness
- let the orchestrator, not an individual worker, declare the whole task complete
- move durable lessons into the owning skill, guide, spec, or documentation artifact

## Relationship With ADR 0003

This ADR and ADR 0003 agree on the core direction: multi-agent execution should be orchestrator-led, proportional to task complexity, and backed by reusable skills.
ADR 0004 is the principle decision.
ADR 0003 is the implementation decision.

| Question | ADR 0004: principle decision | ADR 0003: implementation decision |
| --- | --- | --- |
| Decision style | Principle-first operating model | Concrete implementation decision |
| Scope | Skill-first orchestration, proportional delegation, improved mode labels, and responsibility boundaries | Six-role roster, starter skill bundles, durable state directories, per-role read sets, Junie/Codex alignment |
| Role vocabulary | Reuses ADR 0003's Coordinator, Planner, Worker, Reviewer, Verifier, Specialist roster | Defines those six identities as the implementation vocabulary |
| Mode vocabulary | Renames labels to `M0: direct`, `M1: assisted`, `M2: delegated`, `M3: parallel`, and `M4: gated` | Uses the accepted labels when implemented in workflow guidance |
| Skill detail | Names activity categories such as `planning`, `coding`, `testing`, and `review` | Names concrete starter skills such as `repo-task-execute`, `run-validation`, `diff-review`, and `handoff-pack` |
| State model | Requires clear handoff and reporting discipline | Materializes `.agents/context/*` directories |
| Adoption path | Accept as the architectural direction | Implement as the first concrete rollout of that direction |

## Consequences

Benefits:

- planning, coding, review, testing, documentation, and release work can use role-specific procedures
- exploration, verification, and review can happen in parallel when the scopes are independent
- workers receive clearer ownership boundaries and output contracts
- independent review agents can challenge implementation choices without being biased by authorship
- skills make repeated workflows easier to reuse across agents and sessions
- durable process knowledge becomes easier to maintain and audit
- the orchestrator can scale from solo work to full sidecar workflows without changing the definition of done
- accepting the model before the implementation detail reduces the risk of premature workflow infrastructure

Costs and risks:

- multi-agent execution adds coordination overhead and should be skipped for small changes
- conflicting edits become more likely unless ownership boundaries are explicit
- a weak orchestrator can create false confidence by collecting agent assertions without integration review
- skills can drift from standing guidance if both are not maintained together
- too many specialized agents can slow simple work and obscure accountability
- this proposal may need a second implementation decision before concrete skill files and state directories are added

Required follow-up changes if accepted:

1. Use ADR 0003 as the implementation decision for the first rollout.
2. Update `.agents/references/workflow.md` to use the accepted mode labels while keeping the `M0` through `M4` identifiers and preserving existing mode semantics.
3. Add or formalize repo-local skills for the accepted activity set.
4. Update `.agents/references/documentation.md` if skill ownership or artifact-routing rules change.
5. Update `docs/WORKING_WITH_AI.md` if human-facing AI collaboration guidance should describe when to request or expect multi-agent workflows.
6. Update `ROADMAP.md` only if this proposal is selected for implementation work.
7. Keep routine single-agent maintenance explicitly allowed.

## Alternatives Considered

### Accept Only ADR 0003

ADR 0003 is more actionable and includes the concrete role roster, starter skills, durable state directories, and platform follow-up work.
It does not separate the operating principle from the first concrete implementation as clearly.
Keeping both ADRs lets future implementation details change without losing the principle decision.

### Keep Multi-Agent Work Ad Hoc

Ad hoc delegation can work for isolated tasks.
It does not give agents consistent role contracts, output formats, ownership boundaries, or validation expectations.
It also makes durable lessons harder to preserve.

### Make Every Task Multi-Agent

This maximizes role separation but creates unnecessary process overhead for routine edits, small documentation changes, and narrow fixes.
The repository should use the smallest effective workflow mode instead.

### Keep Existing Mode Labels

The current labels are precise, but several are implementation-heavy (`sidecar-readonly`, `parallel-sliced`, `full-sidecar`) and harder to explain in human-facing guidance.
Keeping the `M0` through `M4` identifiers while improving the labels preserves compatibility and makes the progression easier to scan.

### Use `M4: Agentic`

`Agentic` was considered for the highest workflow mode.
It was not chosen because all modes are agentic in some sense.
`Gated` better names what makes `M4` distinct: independent review, verification, security, documentation, release, or specialist gates.

### Use Dedicated Agents Without Dedicated Skills

Dedicated agents can separate responsibility, but the operating procedure remains prompt-local.
That makes consistency depend on each handoff and increases the chance that repeated validation, review, or documentation rules are omitted.

### Use Dedicated Skills Without Dedicated Agents

Skills alone improve consistency for solo work.
They do not provide parallel exploration, independent review, or bounded specialist execution when a larger change needs more than one accountable worker.

### Put All Workflow Knowledge In Standing Guides Only

Standing guides are appropriate for repository policy and artifact ownership.
Skills are better for repeatable task execution because they can package focused instructions, scripts, references, and templates for a specific activity.
The two should remain aligned rather than one replacing the other.

## Confirmation

This ADR was accepted by explicit user instruction on 2026-05-10.
Implementation must continue to confirm that:

- the workflow still follows spec-driven development and repository completion rules
- the orchestrator remains accountable for final integration, validation, and handoff
- multi-agent use is optional and proportional to task complexity
- skill instructions do not duplicate or contradict the owning `.agents/references/*.md` guides
- `.agents/references/workflow.md` keeps the `M0` through `M4` identifiers with the accepted labels and unchanged semantics
- validation requirements continue to come from `.agents/references/testing.md`
- review requirements continue to come from `.agents/references/reviews.md`
- release work remains out of scope unless explicitly requested

If accepted and implemented, confirmation should include:

1. A mapping from each supported agent role to its governing skill or guide.
2. Updated or created repo-local skills for the accepted activity set.
3. A documentation review showing that `docs/WORKING_WITH_AI.md`, `.agents/references/workflow.md`, `.agents/references/documentation.md`, and this ADR do not contradict each other.
4. A validation record for the documentation and support-file changes.

## Links

- ADR 0003: `docs/decisions/0003-adopt-multi-agent-roles-and-skill-catalog.md`
- ADR 0001: `docs/decisions/0001-adopt-pre-planning-artifacts.md`
- ADR 0002: `docs/decisions/0002-align-lifecycle-vocabulary-with-industry-practice.md`
- Workflow guide: `.agents/references/workflow.md`
- Documentation guide: `.agents/references/documentation.md`
- Testing guide: `.agents/references/testing.md`
- Review guide: `.agents/references/reviews.md`
- Human-facing AI guide: `docs/WORKING_WITH_AI.md`
