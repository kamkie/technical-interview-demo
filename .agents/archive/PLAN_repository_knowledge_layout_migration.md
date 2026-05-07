# Plan: Repository Knowledge Layout Migration

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Integration |
| Status | Implemented |

## Summary
- Migrate durable repository knowledge to the layout described by `.agents/references/repository-knowledge-spec.md`, with repo-specific additions for `.agents/plans/` and `.agents/templates/`.
- Keep conventional root documentation in place, including `README.md`, `SETUP.md`, `CONTRIBUTING.md`, `CHANGELOG.md`, `ROADMAP.md`, and `WORKING_WITH_AI.md`.
- Move human-and-agent project knowledge into `docs/`, Codex-specific guidance into `.agents/references/`, active plans into `.agents/plans/`, templates into `.agents/templates/`, generated reports into `.agents/reports/`, historical AI artifacts into `.agents/archive/`, and repo-local skills under `.agents/skills/`.
- Target this as next-RC work. If it lands before stable `v2.0.0`, the next release candidate is expected to be `v2.0.0-RC7` unless release sequencing changes.

## Desired State
- `AGENTS.md` stays at the repository root as the compact always-on Codex router, spec-priority guide, phase map, and definition-of-done owner.
- Conventional root docs stay at root:
  - `README.md`
  - `SETUP.md`
  - `CONTRIBUTING.md`
  - `CHANGELOG.md`
  - `ROADMAP.md`
  - `WORKING_WITH_AI.md`
- `WORKING_WITH_AI.md` is human-facing documentation. Agents do not load it by default; they load it only when the user asks about the human AI workflow or when the file itself is being updated.
- `docs/` owns durable project knowledge that humans and agents may both read:
  - architecture and design summaries
  - durable application lifecycle specs
  - future ADRs under `docs/adr/`
  - future human testing, security, operations, or API docs only when those docs are actually created
- `.agents/` owns Codex-specific material:
  - `.agents/references/` for AI-only owner guides, detailed references, and the repository knowledge spec
  - `.agents/plans/` for active execution plans
  - `.agents/templates/` for AI templates
  - `.agents/skills/` for repo-local Codex skills, including the existing `repo-task`
  - `.agents/reports/` for generated AI analysis reports
  - `.agents/archive/` for historical AI plans and artifacts
- No live former AI instruction tree remains after migration. Historical path mentions remain only inside archived or explicitly historical content.
- The build wrapper and CI classifier treat `docs/` and `.agents/` markdown/support files as lightweight unless they accompany non-lightweight changes.

## Move Mapping
| Current path | Target path | Notes |
| --- | --- | --- |
| repository knowledge spec | `.agents/references/repository-knowledge-spec.md` | Promoted before moving other knowledge files; update it with repo-specific owners for `.agents/plans/` and `.agents/templates/`. |
| project architecture summary | `docs/ARCHITECTURE.md` | Human-and-agent architecture summary. |
| product and contract direction | `docs/DESIGN.md` | Human-and-agent product and contract direction. |
| application lifecycle spec | `docs/specs/application-lifecycle-spec.md` | Durable project spec; normalized to lowercase hyphenated filename under `docs/specs/`. |
| application lifecycle diagrams | `docs/specs/application-lifecycle-diagrams.md` | Durable project diagrams/spec support. |
| lifecycle phase activities | `docs/specs/lifecycle-phase-activities.md` | Durable lifecycle phase reference. |
| AI edit-shaping guidance | `.agents/references/code-style.md` | AI edit-shaping guidance. |
| AI artifact-routing owner | `.agents/references/documentation.md` | AI artifact-routing owner. |
| AI command-wrapper quick reference | `.agents/references/environment-quick-ref.md` | AI command-wrapper quick reference. |
| AI ad hoc and milestone execution guide | `.agents/references/execution.md` | AI ad hoc and milestone execution guide. |
| AI whole-plan execution guide | `.agents/references/plan-execution.md` | AI whole-plan execution guide. |
| AI plan creation guide | `.agents/references/planning.md` | AI plan creation guide. |
| AI release workflow guide | `.agents/references/releases.md` | AI release workflow guide. |
| AI review guide | `.agents/references/reviews.md` | AI review guide. |
| AI validation routing guide | `.agents/references/testing.md` | AI validation routing guide, not a human testing manual. |
| AI branch, delegation, and integration guide | `.agents/references/workflow.md` | AI branch, delegation, and integration guide. |
| agent lessons | `.agents/references/LEARNINGS.md` | Agent lessons; do not duplicate into `docs/LEARNINGS.md` unless a separate human-facing lessons document is intentionally created. |
| detailed AI references | `.agents/references/*.md` | Filenames converted to lowercase hyphenated names where practical; update all references. |
| AI templates | `.agents/templates/` | Required target per user request. |
| active execution plans | `.agents/plans/` | Required target per user request; this plan and `ROADMAP.md` now use the moved path. |
| repo-local skills | `.agents/skills/*` | Merge with existing `.agents/skills/`; avoid overwriting `repo-task`. |
| `.agents/skills/repo-task/` | unchanged | Already in target location; update task files to new `docs/` and `.agents/` paths. |
| `.agents/reports/` | `.agents/reports/` | Generated AI analysis reports. |
| `.agents/archive/*.md` and other historical AI artifacts | `.agents/archive/` | Historical material only; do not rewrite content except for a top-level supersession note if needed. |
| `WORKING_WITH_AI.md` | unchanged | Human-facing root doc; not an agent default-load file. |
| `README.md`, `SETUP.md`, `CONTRIBUTING.md`, `CHANGELOG.md`, `ROADMAP.md` | unchanged | Conventional root docs stay as-is. |

## Scope
- In scope:
  - promote and update the repository knowledge spec under `.agents/references/`
  - move live repository knowledge files according to `## Move Mapping`
  - update `AGENTS.md`, `WORKING_WITH_AI.md`, root docs, `.agents/skills/repo-task/` task files, active plans, templates, and references so live paths are correct
  - update `ROADMAP.md` for the active plan path once plans move to `.agents/plans/`
  - update local and CI lightweight-file classification for `docs/`, `.agents/references/`, `.agents/plans/`, `.agents/templates/`, `.agents/reports/`, and `.agents/archive/`
  - record the implemented AI-guidance/layout change in `CHANGELOG.md`
  - validate stale-reference cleanup, skill validity, classifier behavior, and wrapper build
- Out of scope:
  - changing application runtime behavior
  - changing public REST API behavior, REST Docs snippets, OpenAPI baseline, HTTP examples, or database migrations
  - moving conventional root docs away from root
  - rewriting archived content merely to modernize path names
  - creating ADRs for historical decisions as part of this migration
  - creating new human docs such as `docs/SECURITY.md`, `docs/TESTING.md`, `docs/OPERATIONS.md`, or `docs/API.md` unless implementation finds existing content that should move there
  - introducing `.agents/plugins/marketplace.json`

## What Is Not Changed
- Public application behavior and API contracts do not change.
- `README.md`, `SETUP.md`, `CONTRIBUTING.md`, `CHANGELOG.md`, `ROADMAP.md`, and `WORKING_WITH_AI.md` stay at the repository root.
- `WORKING_WITH_AI.md` remains human-facing. It is not added to default agent read sets.
- The `repo-task` skill stays at `.agents/skills/repo-task/`; only its references and task-file paths are updated.
- AI-created commit-message conventions are now owned by `.agents/references/execution.md` and use Conventional Commits plus project metadata footers; the earlier custom body-line taxonomy is superseded.
- Active release mechanics stay owned by release guidance; this plan only targets the next RC when implementation is selected.
- Historical archives are not rewritten for wording churn.

## Current State
- Shared project knowledge now sits under `docs/`.
- Live Codex owner guides, detailed references, plans, templates, repo-local skills, generated reports, and historical AI artifacts now sit under `.agents/`.
- Reusable task starters remain in `.agents/skills/repo-task/`.
- `.agents/references/repository-knowledge-spec.md` is the authoritative repository knowledge layout spec.
- Root docs, tooling classification, changelog, and final validation still need alignment in later milestones.
- `ROADMAP.md` currently targets stable `v2.0.0` after final RC validation, but this plan is intended for the next RC if selected before stable.

## Requirement Gaps And Open Questions
- No blocking requirement gap remains for planning.
- Release numbering assumption: implementation targets the next release candidate after `v2.0.0-RC6`, expected to be `v2.0.0-RC7` if this work lands before stable. If stable `v2.0.0` is released first, replan this as post-`2.0` work instead of RC work.
- The exact contents of future `docs/SECURITY.md`, `docs/TESTING.md`, `docs/OPERATIONS.md`, `docs/API.md`, and `docs/adr/` are intentionally not invented by this plan.

## Locked Decisions And Assumptions
- The migration is worthwhile despite being documentation-infrastructure churn.
- Conventional root docs stay at root.
- `WORKING_WITH_AI.md` is documentation and remains at root, but agents load it only when the user asks about it or when updating it.
- `.agents/plans/` and `.agents/templates/` are part of the target layout.
- The active plan now lives at `.agents/plans/PLAN_repository_knowledge_layout_migration.md`.
- AI-only owner guides move to `.agents/references/` with lowercase hyphenated filenames.
- Human-and-agent architecture/design/lifecycle knowledge moves to `docs/`.
- No live former AI tree remains after successful migration.

## Execution Shape And Shared Files
- Recommended shape: one coordinated documentation-layout migration on a single branch.
- Do not split by worker unless explicitly requested. The same paths are referenced across `AGENTS.md`, root docs, `.agents/skills/repo-task/`, plans, templates, and references, so a single coordinator should own final path consistency.
- If delegation is later required, the only safe worker slice is a mechanical path-update pass over moved task files under `.agents/skills/repo-task/references/tasks/`; coordinator reserves `AGENTS.md`, `ROADMAP.md`, `CHANGELOG.md`, `WORKING_WITH_AI.md`, repository knowledge spec, and the final stale-reference review.

## Affected Artifacts
- Repository knowledge spec:
  - `.agents/references/repository-knowledge-spec.md`
- Root guidance and conventional docs:
  - `AGENTS.md`
  - `WORKING_WITH_AI.md`
  - `CONTRIBUTING.md`
  - `CHANGELOG.md`
  - `ROADMAP.md`
- Moved knowledge files:
  - `docs/specs/**`
  - `.agents/references/**`
  - `.agents/templates/**`
  - `.agents/plans/**`
  - `.agents/skills/**`
  - `.agents/archive/**`
- Target directories:
  - `docs/**`
  - `.agents/references/**`
  - `.agents/plans/**`
  - `.agents/templates/**`
  - `.agents/skills/**`
  - `.agents/reports/**`
  - `.agents/archive/**`
- Tooling and validation:
  - `scripts/classify-changed-files.ps1`
  - `.github/workflows/ci.yml` only if classifier outputs or assumptions need documentation changes
  - `build.ps1` only if classifier integration needs path changes
  - `.gitignore` or formatting configuration only if generated or moved files require path updates

## Execution Milestones
### Milestone 1: Promote Repository Knowledge Spec
- goal: make the proposed layout spec authoritative and repo-specific before moving files
- owned files or packages: `.agents/references/repository-knowledge-spec.md`, `AGENTS.md`, current documentation owner guide
- shared files reserved to the coordinator: `ROADMAP.md`
- context required before execution: `AGENTS.md`, current planning/execution/documentation guides, this plan, and `.agents/references/repository-knowledge-spec.md`
- behavior to preserve: spec-driven ownership, conventional root docs, and on-demand loading
- exact deliverables:
  - move the temporary repository knowledge spec to `.agents/references/repository-knowledge-spec.md`
  - add repo-specific owner rules for `.agents/plans/`, `.agents/templates/`, conventional root docs, and `WORKING_WITH_AI.md`
  - update `AGENTS.md` and the current documentation owner guide to point at the new spec
- validation checkpoint: `git diff --check`; targeted search confirms no live instruction still treats the old temporary spec path as authoritative
- commit checkpoint: commit-ready spec promotion

### Milestone 2: Move Human-And-Agent Project Knowledge
- goal: move durable project knowledge into `docs/`
- owned files or packages: `docs/**`, affected references
- shared files reserved to the coordinator: `AGENTS.md`, repository knowledge spec
- context required before execution: `AGENTS.md`, this plan, `.agents/references/repository-knowledge-spec.md`, current architecture/design/spec files
- behavior to preserve: architecture, design, and lifecycle content stays current-state and does not become executable spec authority beyond its current role
- exact deliverables:
  - create `docs/ARCHITECTURE.md` and `docs/DESIGN.md` from current files
  - create `docs/specs/` and move the current lifecycle spec files there
  - update live references from old shared-knowledge paths to new `docs/` paths
  - do not create empty ADR or security/testing/operations/API docs
- validation checkpoint: targeted `rg` for old architecture/design/spec paths in live files
- commit checkpoint: commit-ready human/project docs move

### Milestone 3: Move AI References, Plans, Templates, Skills, Reports, And Archives
- goal: complete the `.agents/` side of the target layout
- owned files or packages: `.agents/references/**`, `.agents/plans/**`, `.agents/templates/**`, `.agents/skills/**`, `.agents/reports/**`, `.agents/archive/**`
- shared files reserved to the coordinator: `ROADMAP.md`, `AGENTS.md`
- context required before execution: `AGENTS.md`, this plan, `.agents/references/repository-knowledge-spec.md`, current repository knowledge inventory, `.agents/skills/repo-task/references/spec.md`
- behavior to preserve: owner-guide content, plan lifecycle metadata, task-skill dispatch behavior, archived historical content
- exact deliverables:
  - move active plans to `.agents/plans/`
  - move templates to `.agents/templates/`
  - move AI owner guides and detailed references to `.agents/references/`
  - move repo-local skills into `.agents/skills/`
  - keep `.agents/skills/repo-task/` in place and update its task files for new paths
  - move generated AI reports to `.agents/reports/`
  - move historical AI artifacts to `.agents/archive/`
  - remove the old live AI directory if it is empty after moves
  - update `ROADMAP.md` so this plan path points to `.agents/plans/PLAN_repository_knowledge_layout_migration.md`
- validation checkpoint:
  - targeted `rg` searches for old live AI directory paths in live non-archive files
  - skill validation for `.agents/skills/repo-task`
- commit checkpoint: commit-ready AI layout move

### Milestone 4: Align Tooling, Root Docs, And Load Policy
- goal: make the new layout usable by humans, agents, local validation, and CI
- owned files or packages: `AGENTS.md`, `WORKING_WITH_AI.md`, `CONTRIBUTING.md`, `CHANGELOG.md`, `ROADMAP.md`, classifier/build support files, `.agents/skills/repo-task/**`
- shared files reserved to the coordinator: all listed files
- context required before execution: `AGENTS.md`, this plan, `.agents/references/documentation.md`, `.agents/references/testing.md`, `.agents/references/repository-knowledge-spec.md`, `WORKING_WITH_AI.md` only because it is being updated
- behavior to preserve: `WORKING_WITH_AI.md` remains human-facing and not a default agent read; conventional root docs stay root
- exact deliverables:
  - update root docs only for references to moved files
  - update `AGENTS.md` phase owner map and on-demand reference map for the new layout
  - update `.agents/skills/repo-task/references/index.md` and affected task files for `.agents/plans/`, `.agents/templates/`, `docs/`, and `.agents/references/`
  - update classifier/lightweight support for `docs/` and `.agents/`
  - update `CHANGELOG.md` under `## [Unreleased]`
  - update any build, CI, gitignore, or formatting path references that still name the old AI directory
- validation checkpoint:
  - classifier comparison over the migration reports moved docs/support files as lightweight
  - targeted live-file stale-reference searches have only intentional historical hits
- commit checkpoint: commit-ready tooling and reference alignment

### Milestone 5: Final Validation And Review
- goal: prove the repository knowledge migration is coherent and release-candidate-ready
- owned files or packages: `.agents/plans/PLAN_repository_knowledge_layout_migration.md`
- shared files reserved to the coordinator: none beyond final status updates
- context required before execution: `AGENTS.md`, this plan at its moved path, `.agents/references/testing.md`, `.agents/references/reviews.md`, `.agents/references/documentation.md`
- behavior to preserve: no application contract changes
- exact deliverables:
  - update plan lifecycle to `Integration / Implemented`
  - update `ROADMAP.md` status to `Implemented`
  - record validation results in the moved plan
  - run skill validation for `repo-task`
  - run stale-reference searches for old live AI-directory paths
  - run `git diff --check`
  - run `./build.ps1 build`
  - perform a documentation/process review for contradictory load rules, duplicate owners, broken path references, and accidental `WORKING_WITH_AI.md` default-load requirements
- validation checkpoint: all required checks pass, or any blocker is recorded before handoff
- commit checkpoint: final commit-ready migration

## Edge Cases And Failure Modes
- Leaving active plans split across old and new plan directories would break roadmap and execution guidance.
- Moving `WORKING_WITH_AI.md` into default agent load would violate the user requirement and increase routine context.
- Lowercase `.agents/references/` renames can break many cross-references if not searched comprehensively.
- Archived files will retain historical old paths; validation must distinguish active guidance from history.
- Moving reports can blur historical versus generated-report ownership; the repository knowledge spec must define the intended distinction before files move.
- Some moved paths may affect lightweight CI classification or formatting targets even though application code is unchanged.
- Cutting stable `v2.0.0` before this plan lands changes the release target; replan as post-`2.0` instead of forcing an RC-only assumption.

## Validation Plan
- Skill validation:
  - `python "<skill-creator>/scripts/quick_validate.py" .agents/skills/repo-task`
- Path and stale-reference checks:
  - targeted `rg` for old live AI-directory paths in root docs, `.agents/`, `docs/`, `ROADMAP.md`, and `CHANGELOG.md`
  - targeted `rg` for accidental `WORKING_WITH_AI.md` default-load language
  - targeted `rg` for the old temporary repository knowledge spec path
- Tooling checks:
  - classifier comparison proving `docs/` and `.agents/` support files are lightweight-only when no non-lightweight files changed
  - `git diff --check`
  - `./build.ps1 build`
- Manual review:
  - verify move mapping completed
  - verify root conventional docs stayed root
  - verify `AGENTS.md` is a compact router, not a copy of moved content
  - verify `ROADMAP.md` points to the moved plan path after Milestone 3

## Testing Strategy
- Unit tests: not applicable unless tooling helper logic is changed beyond path patterns.
- Integration tests: not applicable to application behavior.
- Contract tests: not applicable; no REST API, OpenAPI, REST Docs, or HTTP examples should change.
- Smoke or benchmark tests: not applicable.
- Documentation/tooling checks: required through stale-reference searches, skill validation, classifier validation, `git diff --check`, and `./build.ps1 build`.

## Better Engineering Notes
- This is documentation-infrastructure churn by design and is acceptable because the user explicitly wants it planned for the next RC.
- Prefer file moves with history-preserving Git renames where possible.
- Do not combine this with content compaction except where a path owner rule must change for correctness.
- Consider refreshing the archived AI-guideline evaluation only after the migration if context measurements are needed for release notes; do not make that a blocker for the layout migration.

## Validation Results
- Milestone 1:
  - `git diff --check` passed.
  - `rg -n "temp/repository-knowledge-spec[.]md" AGENTS.md ai .agents README.md SETUP.md CONTRIBUTING.md CHANGELOG.md ROADMAP.md WORKING_WITH_AI.md` returned no matches.
- Milestone 2:
  - Moved the architecture, design, and lifecycle spec files into `docs/`.
  - `rg -n "(?:ai)/(ARCHITECTURE|DESIGN|specs/)|ai\\(ARCHITECTURE|DESIGN|specs\\)" AGENTS.md docs .agents README.md SETUP.md CONTRIBUTING.md ROADMAP.md WORKING_WITH_AI.md --glob "!.agents/archive/**"` returned no matches.
  - `git diff --check` passed.
- Milestone 3:
  - Moved active plans, templates, AI owner guides, detailed references, repo-local skills, generated reports, and historical AI artifacts under `.agents/`.
  - Removed the old live AI directory after verifying it contained no remaining files.
  - `rg -n "(?:ai)/(plans|templates|references|skills|archive)|(?:ai)/(PLANNING|EXECUTION|PLAN_EXECUTION)[.]md" AGENTS.md README.md SETUP.md CONTRIBUTING.md ROADMAP.md WORKING_WITH_AI.md docs .agents --glob "!.agents/archive/**" --glob "!.agents/reports/**"` returned no matches.
  - `python "C:/Users/kamki/AppData/Local/JetBrains/IntelliJIdea2026.1/aia/codex/skills/.system/skill-creator/scripts/quick_validate.py" .agents/skills/repo-task` initially failed because `PyYAML` was unavailable; reran with `PyYAML` installed to a temporary directory outside the repository and `PYTHONPATH` set for that process, and it passed with `Skill is valid!`.
  - `git diff --check` passed.
- Milestone 4:
  - Updated root docs, AI owner-guide routing, `.gitignore`, changelog wording, repository knowledge spec, and lightweight classifier patterns for the `docs/` plus `.agents/` layout.
  - `rg -n "(?:ai)/" AGENTS.md README.md SETUP.md CONTRIBUTING.md ROADMAP.md WORKING_WITH_AI.md docs .agents/references .agents/skills --glob "!.agents/references/LEARNINGS.md"` returned no matches.
  - `rg -n "(?:ai)/" .github build.ps1 scripts .gitignore` returned no matches.
  - `./scripts/classify-changed-files.ps1 -BaseRef HEAD~2 -HeadRef HEAD` reported `nonLightweightFiles = {}` and `skipHeavyValidation = True` for the moved docs/support-file migration diff.
  - `git diff --check` passed.
- Milestone 5:
  - Updated this plan to `Integration / Implemented` and updated `ROADMAP.md` status to `Implemented`.
  - `python "C:/Users/kamki/AppData/Local/JetBrains/IntelliJIdea2026.1/aia/codex/skills/.system/skill-creator/scripts/quick_validate.py" .agents/skills/repo-task` passed with `Skill is valid!` using the temporary `PyYAML` fallback described above.
  - `Test-Path ai` returned `False`.
  - `rg -n "(?:ai)/" AGENTS.md README.md SETUP.md CONTRIBUTING.md ROADMAP.md WORKING_WITH_AI.md docs .agents/references .agents/skills .github build.ps1 scripts .gitignore --glob "!.agents/references/LEARNINGS.md"` returned no matches.
  - `rg -n "temp/repository-knowledge-spec[.]md" AGENTS.md README.md SETUP.md CONTRIBUTING.md ROADMAP.md WORKING_WITH_AI.md docs .agents .github build.ps1 scripts .gitignore CHANGELOG.md` returned no matches.
  - `rg -n "WORKING_WITH_AI[.]md.*default|default.*WORKING_WITH_AI[.]md|load.*WORKING_WITH_AI[.]md" AGENTS.md .agents/references .agents/skills docs README.md CONTRIBUTING.md` found only the repository knowledge spec rule saying agents do not load `WORKING_WITH_AI.md` by default.
  - Historical stale-reference scan found old-path hits only in `CHANGELOG.md`, `.agents/reports/`, `.agents/archive/`, and implemented active-plan historical notes with supersession wording.
  - `git diff --check` passed.
  - `./build.ps1 build` exited 0 and took the lightweight-only shortcut: `Only lightweight files changed; skipping Gradle build.`
  - Documentation/process review found no contradictory load rules, duplicate live owners, broken live path references, or accidental `WORKING_WITH_AI.md` default-load requirement; added `.agents/reports/` to the root on-demand inventory during review.

## User Validation
- Review `.agents/references/repository-knowledge-spec.md` for the final ownership rules.
- Spot-check `AGENTS.md` to confirm it is a compact router and does not default-load `WORKING_WITH_AI.md`.
- Spot-check `docs/ARCHITECTURE.md`, `.agents/references/planning.md`, `.agents/plans/`, and `.agents/templates/` to confirm the desired layout exists.
- Run a `$repo-task` exact-slug task and verify it points to `.agents/plans/` where plan paths are required.

## Required Content Checklist
- Behavior changing: repository knowledge ownership and file layout.
- Roadmap tracking: `ROADMAP.md` active release track points to this plan for next-RC work.
- Out of scope: app runtime, public API, root conventional doc relocation, archive rewriting, ADR backfill, plugin marketplace.
- Governing specs: current user request and `.agents/references/repository-knowledge-spec.md`.
- Likely files to change: listed in `## Affected Artifacts`.
- Compatibility promise: no application contract changes; old live AI-directory paths removed after migration; archived historical paths may remain.
- Risks: path churn, duplicate owners, accidental `WORKING_WITH_AI.md` default load, stale references, classifier drift, release-target timing.
- Requirement gaps: none blocking; release target replan required if stable `v2.0.0` ships first.
- Execution shape: one coordinated documentation-layout migration.
- Coordinator-owned files: `AGENTS.md`, root docs, roadmap, changelog, repository knowledge spec, and final stale-reference review.
- Context per milestone: listed explicitly.
- Artifact routing: human/project docs move to `docs/`; Codex-specific materials move to `.agents/`.
- Validation: skill validation, stale-reference searches, classifier check, `git diff --check`, `./build.ps1 build`, and manual documentation/process review.
- User verification: review desired-state artifacts and spot-check `$repo-task` path behavior.
