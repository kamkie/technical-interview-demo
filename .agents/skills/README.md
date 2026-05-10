# Repo-Local Skills Index

This directory holds reusable, model-portable procedures invoked by AI agents during repository work.
Each subdirectory contains a `SKILL.md` (Anthropic-style) with frontmatter (`name`, `description`), a Read Set, Inputs, a Workflow, Stop Conditions, and an Output contract.
Some skills also include `agents/openai.yaml` siblings for Codex/OpenAI invocation; they are inert for other runtimes.

Skills are the *verbs*; agent roles defined in `.agents/references/workflow.md` are the *subjects*.
Skills do not own policy. They point back to owner guides under `.agents/references/`.

## Skill Catalog

| Skill | Owner Guide | Primary Use |
| --- | --- | --- |
| `select-mode-and-skills` | `.agents/references/workflow.md` | Choose `M0`–`M4` mode, role roster, and skill chain for a request or plan |
| `repo-plan-author` | `.agents/references/planning.md`, `.agents/references/plan-template.md` | Author or revise `.agents/plans/PLAN_*.md` |
| `handoff-pack` | `.agents/references/workflow.md` | Produce a durable handoff packet under `.agents/context/*` before delegated work |
| `repo-task-execute` | `.agents/references/execution.md`, `.agents/references/code-style.md` | Execute one bounded task or one plan task |
| `run-validation` | `.agents/references/testing.md` | Choose, run, and record the smallest sufficient validation |
| `diff-review` | `.agents/references/reviews.md` | Review a diff for bugs, spec drift, contract drift, missing validation, security risk |
| `security-review` | `.agents/references/reviews.md` | Specialist gate for auth, secrets, CI permissions, dependency, container, and release-path risk |
| `integrate-branch` | `.agents/references/workflow.md` | Coordinator-owned merge/cherry-pick into `main` with post-integration validation |
| `openapi-contract-check` | `.agents/references/reviews.md`, contract baselines | Verify OpenAPI compatibility and approved-baseline drift on public-API changes |
| `triage-flaky-test` | `.agents/references/troubleshooting.md`, `.agents/references/testing.md` | Decide diagnose / fix / quarantine / replan for an intermittent failure |
| `gh-fix-ci` | `.agents/references/workflow.md`, `.agents/references/troubleshooting.md` | Inspect failing GitHub Actions checks and draft or implement a fix |
| `gh-fix-security-quality` | `.agents/references/reviews.md` | Inspect GitHub Security/quality alerts and draft or implement a fix |

## Standard Multi-Agent Skill Chain

Use this chain as the canonical M4-gated workflow.
Degrade to fewer stages for lower modes:

- `M0: direct` — Stage 0 → 3 → 5 only (one agent, self-validation).
- `M2: delegated` — Stage 0 → 1 (if needed) → 2 → 3 → 5 → 7.
- `M3: parallel` — `M2` × N disjoint Workers, then 7.
- `M4: gated` — full chain below.

```
Stage 0  Coordinator       select-mode-and-skills        → mode, roles, chain, scopes
Stage 1  Planner           repo-plan-author              → .agents/plans/PLAN_*.md (when needed)
Stage 2  Coordinator       handoff-pack                  → .agents/context/handoffs/*.md
Stage 3  Worker(s)         repo-task-execute             → edits + .agents/context/workers/*.md
         Worker self-check run-validation, diff-review   → green + self-review
Stage 4  Reviewer          diff-review                   → .agents/context/reviews/*.md (fresh context)
Stage 5  Verifier          run-validation                → .agents/context/verifications/*.md (fresh context)
Stage 6  Specialist        security-review,              → .agents/context/specialists/*.md (when triggered)
                           openapi-contract-check,
                           gh-fix-security-quality
Stage 7  Coordinator       integrate-branch              → merge, post-integration run-validation,
                                                           plan/ROADMAP/CHANGELOG updates
```

### Trigger Rules

- Stage 1 fires on any Task Gate trigger from `.agents/references/execution.md` (unclear intent, public API change, multi-task work, ownership splits).
- Stage 4 (independent Reviewer) is mandatory for `M4`; recommended for `M2`/`M3` on non-trivial diffs.
- Stage 5 (independent Verifier) is mandatory for `M4`; recommended whenever the Worker's environment differs from the integration target.
- Stage 6 specialists fire on declared triggers in their `SKILL.md` (e.g., security review on auth/crypto/secrets/CI permissions; OpenAPI check on REST controller or schema changes; GH security on Dependabot/code-scanning alerts).
- Stage 7 always runs for any change that produces tracked-file edits.

### Sidecar Independence

Reviewer, Verifier, and Specialist agents must run from a *fresh context* (no shared scratchpad with the Worker) for their findings to add signal beyond the Worker's self-review.

### Durable State Bus

Use `.agents/context/*` as the integration bus between stages.
File names follow `<plan_stem_or_topic>__<role>.md`.
Conversational memory is not a substitute for these files.

## Authoring New Skills

When adding a skill:

1. Confirm no existing skill already owns the procedure; extend the existing skill instead of duplicating it.
2. Identify the owner guide under `.agents/references/` and link to it as the policy authority — never inline standing rules.
3. Use the standard sections: frontmatter, Overview, Read Set, Inputs, Workflow, Stop Conditions, Output.
4. Keep the Read Set minimal and conditional; do not bulk-load references.
5. Run `./scripts/ai/validate-skills.ps1` before handoff.
6. Add the new skill to the catalog table above and, if it belongs in the standard chain, to the diagram and trigger rules.
7. If the skill needs a Codex/OpenAI manifest, add `agents/openai.yaml`; other runtimes can ignore it.

## Junie Sessions

Junie sessions are single-agent and execute as `M0: direct` (see `.junie/AGENTS.md`).
Junie may invoke any skill as a procedural wrapper, but the full multi-agent chain (Stages 4–6 with independent sidecars) requires separate runtimes or sessions and is out of scope for a single Junie session.
