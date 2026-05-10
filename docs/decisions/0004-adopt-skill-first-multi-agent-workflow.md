# 0004 Adopt Skill-First Multi-Agent Workflow

## Status

Proposed on 2026-05-10

## Date

2026-05-10

## Context

The repository already defines AI work around spec-driven development, scoped context loading, executable validation, review, and integration rules.
It also has workflow guidance for solo execution, sidecars, bounded workers, parallel slices, and full sidecar gates.

ADR 0003 proposes a concrete multi-agent roster, starter skill catalog, durable state directories, and per-role read sets.
This ADR records a lighter competing proposal: adopt the skill-first, orchestrator-led operating model first, then implement concrete roles, skills, and state only after the model is accepted.

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

Recommended role model:

| Role | Primary responsibility | Typical skills or guidance |
| --- | --- | --- |
| Orchestrator | Own intake, routing, decomposition, integration, validation evidence, and handoff | workflow, planning, review |
| Planner | Clarify scope, acceptance criteria, risks, dependencies, and spec gaps | planning |
| Explorer | Answer narrow codebase, test, docs, or ownership questions in parallel | architecture, documentation, testing |
| Worker | Implement one bounded code, test, docs, or support-file slice | coding, testing |
| Tester / Verifier | Define and run validation, investigate failures, and report residual risk | testing, troubleshooting |
| Reviewer | Check correctness, spec alignment, maintainability, security risk, and hidden scope expansion | review, security-review |
| Documentation Agent | Keep user-facing docs, AI guidance, decision records, plans, and generated references aligned when applicable | documentation |
| Release Agent | Perform versioning, changelog, artifact, tag, and publication checks only when release work is explicitly requested | release |

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

## Comparison With ADR 0003

This ADR and ADR 0003 agree on the core direction: multi-agent execution should be orchestrator-led, proportional to task complexity, and backed by reusable skills.
They differ in how much implementation detail should be accepted now.

| Question | ADR 0003: roles and catalog | ADR 0004: skill-first workflow |
| --- | --- | --- |
| Decision style | Concrete implementation proposal | Principle-first operating model |
| Scope | Six-role roster, starter skill bundles, durable state directories, per-role read sets, Junie/Codex alignment | Orchestrator-led workflow, agent responsibilities, skill categories, delegation rules |
| Best fit | The team is ready to build the supporting skill and state infrastructure now | The team wants to agree on operating principles before committing to exact files and role roster |
| Strength | More actionable; follow-up work can start immediately | Lower commitment; easier to accept without over-specifying implementation |
| Risk | May overfit the first implementation and create more upfront work | May leave too many implementation choices undecided |
| Skill detail | Names concrete starter skills such as `repo-task-execute`, `run-validation`, `diff-review`, and `handoff-pack` | Names activity categories such as `planning`, `coding`, `testing`, and `review` |
| Agent detail | Defines Coordinator, Planner, Worker, Reviewer, Verifier, and Specialist as explicit identities | Defines broader roles including Orchestrator, Planner, Explorer, Worker, Tester, Reviewer, Documentation Agent, and Release Agent |
| State model | Materializes `.agents/context/*` directories | Requires clear handoff/reporting but defers exact state storage |
| Cross-platform scope | Explicitly includes Codex and Junie follow-up | Stays platform-neutral unless follow-up work chooses platform-specific guidance |
| Adoption path | Accept, then implement a concrete skill catalog and state layout | Accept, then create a plan to choose exact roles, skills, and state layout |

Decision guidance:

- choose ADR 0003 if the desired next step is implementation of a concrete multi-agent infrastructure
- choose ADR 0004 if the desired next step is agreement on direction before designing the concrete infrastructure
- combine them by accepting ADR 0004 as the principle decision and using ADR 0003 as the first implementation plan input

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

1. Decide whether ADR 0003 becomes the implementation plan input, is revised, or is rejected.
2. Update `.agents/references/workflow.md` if its existing `M0` through `M4` modes need to reference this ADR or the skill-first operating model.
3. Add or formalize repo-local skills for the accepted activity set.
4. Update `.agents/references/documentation.md` if skill ownership or artifact-routing rules change.
5. Update `WORKING_WITH_AI.md` if human-facing AI collaboration guidance should describe when to request or expect multi-agent workflows.
6. Update `ROADMAP.md` only if this proposal is selected for implementation work.
7. Keep routine single-agent maintenance explicitly allowed.

## Alternatives Considered

### Accept ADR 0003 Directly

ADR 0003 is more actionable and includes the concrete role roster, starter skills, durable state directories, and platform follow-up work.
It is the better choice if the next step should be immediate implementation.
This ADR keeps those details as a follow-up decision so the principle can be accepted without committing to the exact implementation.

### Keep Multi-Agent Work Ad Hoc

Ad hoc delegation can work for isolated tasks.
It does not give agents consistent role contracts, output formats, ownership boundaries, or validation expectations.
It also makes durable lessons harder to preserve.

### Make Every Task Multi-Agent

This maximizes role separation but creates unnecessary process overhead for routine edits, small documentation changes, and narrow fixes.
The repository should use the smallest effective workflow mode instead.

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

This ADR remains proposed until explicitly accepted.
Before implementation, confirm that:

- the workflow still follows spec-driven development and repository completion rules
- the orchestrator remains accountable for final integration, validation, and handoff
- multi-agent use is optional and proportional to task complexity
- skill instructions do not duplicate or contradict the owning `.agents/references/*.md` guides
- validation requirements continue to come from `.agents/references/testing.md`
- review requirements continue to come from `.agents/references/reviews.md`
- release work remains out of scope unless explicitly requested

If accepted and implemented, confirmation should include:

1. A mapping from each supported agent role to its governing skill or guide.
2. Updated or created repo-local skills for the accepted activity set.
3. A documentation review showing that `WORKING_WITH_AI.md`, `.agents/references/workflow.md`, `.agents/references/documentation.md`, and this ADR do not contradict each other.
4. A validation record for the documentation and support-file changes.

## Links

- ADR 0003: `docs/decisions/0003-adopt-multi-agent-roles-and-skill-catalog.md`
- ADR 0001: `docs/decisions/0001-adopt-pre-planning-artifacts.md`
- ADR 0002: `docs/decisions/0002-align-lifecycle-vocabulary-with-industry-practice.md`
- Workflow guide: `.agents/references/workflow.md`
- Documentation guide: `.agents/references/documentation.md`
- Testing guide: `.agents/references/testing.md`
- Review guide: `.agents/references/reviews.md`
- Human-facing AI guide: `WORKING_WITH_AI.md`
