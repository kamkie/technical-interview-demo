# Plan: AI Workflow And Instruction Hardening

## Lifecycle
| Status | Current |
| --- | --- |
| Phase | Closed |
| Status | Released |

## Summary
- Tighten the repository's AI guidance so execution, PR handling, release preparation, planning intake, and multi-agent coordination follow one explicit, non-conflicting workflow.
- This work matters because the current AI instruction set is spread across multiple files, leaves some planning and review expectations implicit, and does not yet make the PR-before-release sequence or phase-specific agent roles explicit enough.
- Success is measured by: the AI documents giving one consistent story for local execution, PR creation, merge-before-release handling, clarification-heavy planning, phase-based delegation, and focused guidance ownership; the human-facing maintainer docs staying aligned where their scope overlaps; and `./gradlew.bat build` passing without any application-contract drift.

## Scope
- In scope:
  - update AI workflow guidance so plan execution treats pushing changes to GitHub and creating a PR as the last execution step after local implementation, validation, and integration readiness work are complete
  - update release guidance so release preparation begins only after the approved PR has been merged onto `main`
  - tighten planning guidance so agents ask more targeted clarification questions when user input leaves scope, compatibility, rollout, or validation ambiguous
  - require planning outputs to record requirement gaps, locked assumptions, and unresolved user-input holes instead of silently guessing
  - audit the current AI instruction files for overlap and missing guidance, then introduce a small focused set of new AI documents to cover code style, testing, review/security review, and documentation guidance
  - update the multi-agent workflow to define explicit phase ownership for requirements gathering, planning, investigation, coding, testing, code review, security review, and documentation
  - update prompt starters and human-facing workflow docs where their scope overlaps with the new AI guidance
- Out of scope:
  - changing application code, public API behavior, REST Docs content, OpenAPI, reviewer HTTP examples, or benchmark behavior
  - changing GitHub Actions trigger logic, branch protection settings, PR templates, or remote repository settings
  - creating or pushing a PR, merging branches, cutting a release, or creating a tag
  - broad contributor-guide cleanup beyond the specific PR/release/planning/review guidance needed to keep human-facing docs aligned

## Current State
- Current behavior:
  - `ai/EXECUTION.md` explains local plan execution and stopping before release, but it does not explicitly frame PR creation and remote push as the terminal execution step before release work.
  - `ai/WORKFLOW.md` keeps `main` as the integration branch and routes release creation through `main`, but it does not yet describe a dedicated phase split for requirements, planning, investigation, coding, testing, review, security review, and documentation.
  - `ai/RELEASES.md` already requires releases from integrated `main`, but it starts from syncing local `main` rather than explicitly from an approved PR merge event.
  - `ai/PLAN.md` already requires high-value questions and locked assumptions, but it does not yet push hard enough on clarification-first planning when user input is incomplete.
  - `ai/PROMPTS.md` stays intentionally lean, but some prompts still rely on the operator inferring planning and release sequencing details from the other docs.
  - there are no focused AI instruction files dedicated to code style, testing, review/security review, or documentation; those concerns are currently spread across `AGENTS.md`, `README.md`, `CONTRIBUTING.md`, and several `ai/` files.
- Current constraints:
  - `AGENTS.md` requires AI-document roles to stay distinct, `ai/PROMPTS.md` to stay lean, and overlapping guidance to be compacted into the best owning file rather than duplicated.
  - the repository's public API/runtime contract is already stable in `README.md`, generated docs, HTTP examples, and the approved OpenAPI baseline, so this work must remain guidance-only unless the user explicitly asks for contract changes.
  - release rules already require work to land on `main` before release, so the hardening should clarify and sequence that rule rather than invent a second release path.
  - human-facing docs must stay aligned when their scope overlaps the AI workflow, especially for release model, project map, contributor review flow, and maintainer expectations.
- Relevant existing specs and code:
  - roadmap scope anchor: `ROADMAP.md` under `Now: AI Workflow And Instruction Hardening`
  - AI governance and document ownership: `AGENTS.md`
  - human-facing project, release, and project-map guidance: `README.md`
  - contributor-facing PR, review, testing, and release expectations: `CONTRIBUTING.md`
  - planning workflow authority: `ai/PLAN.md`
  - single-agent execution workflow authority: `ai/EXECUTION.md`
  - multi-agent execution workflow authority: `ai/WORKFLOW.md`
  - release workflow authority: `ai/RELEASES.md`
  - reusable prompt entry points: `ai/PROMPTS.md`
  - current AI document inventory: `ai/ARCHITECTURE.md`, `ai/DESIGN.md`, `ai/LEARNINGS.md`, `ai/EXECUTION.md`, `ai/PLAN.md`, `ai/PROMPTS.md`, `ai/RELEASES.md`, and `ai/WORKFLOW.md`
  - automation context to preserve, not rewrite: `.github/workflows/ci.yml` and `.github/workflows/release.yml`

## Locked Decisions And Assumptions
- Preserve the current application and public contract. This plan changes repository guidance only, not API/runtime behavior.
- Treat PR creation and remote push as post-validation execution finalization, not as an intermediate implementation step.
- Treat release work as beginning from merged, validated `main`, with an approved PR merge as the expected transition from execution to release preparation.
- Keep `ai/PROMPTS.md` lean by moving standing policy into the best owning AI document and using prompts only as short entry points.
- Use a minimal focused expansion of the AI document set instead of dumping more mixed guidance into existing files. Unless execution uncovers a stronger reason otherwise, add exactly these focused guides:
  - `ai/CODE_STYLE.md`
  - `ai/TESTING.md`
  - `ai/REVIEWS.md` for code review and security review guidance
  - `ai/DOCUMENTATION.md`
- Keep the AI document roles distinct: planning stays in `ai/PLAN.md`, execution in `ai/EXECUTION.md`, multi-agent coordination in `ai/WORKFLOW.md`, release in `ai/RELEASES.md`, and prompts in `ai/PROMPTS.md`.
- Keep automation unchanged unless execution discovers a direct contradiction between docs and the checked-in GitHub Actions workflows. If that happens, stop and split workflow-automation work into a separate plan instead of silently expanding scope.
- `README.md` and `CONTRIBUTING.md` are affected because release sequencing, project-map entries, and PR/review expectations overlap with the human-facing maintainer workflow.
- REST Docs, OpenAPI, HTTP examples, and benchmarks are not affected because this plan does not change application behavior or published API surface.

## Affected Artifacts
- Tests:
  - no new executable tests are expected; preserve the existing test suite unchanged
  - rely on repository-wide validation plus manual document-consistency checks because this change is guidance-only
- Docs:
  - `AGENTS.md` is affected: yes, because the AI document inventory and maintenance rules must name any new focused AI guides and stay aligned with their roles
  - `README.md` is affected: yes, because the human-facing release model and project-map references overlap with the AI workflow and may need clarification for PR-first, merge-before-release sequencing
  - `CONTRIBUTING.md` is affected: yes, because PR, review, testing, and release expectations overlap with the requested workflow hardening
  - `ROADMAP.md` is not expected to change during implementation beyond serving as the scope anchor for this plan
  - `ai/PLAN.md` is affected: yes
  - `ai/EXECUTION.md` is affected: yes
  - `ai/WORKFLOW.md` is affected: yes
  - `ai/RELEASES.md` is affected: yes
  - `ai/PROMPTS.md` is affected: yes
  - likely new `ai/CODE_STYLE.md` is affected: yes
  - likely new `ai/TESTING.md` is affected: yes
  - likely new `ai/REVIEWS.md` is affected: yes
  - likely new `ai/DOCUMENTATION.md` is affected: yes
- OpenAPI:
  - `src/test/resources/openapi/approved-openapi.json` is affected: no
- HTTP examples:
  - `src/test/resources/http/` is affected: no
- Source files:
  - no `src/main/java/` or `src/test/java/` application/source changes are expected
  - `.github/workflows/ci.yml` and `.github/workflows/release.yml` should be read and preserved as implementation constraints, not edited under this plan unless a separate follow-up plan is created
- Build or benchmark checks:
  - `./gradlew.bat build` is required
  - `./gradlew.bat gatlingBenchmark` is not required

## Execution Milestones
### Milestone 1: Clarify Execution And Release Sequencing
- goal:
  - make the execution-to-PR-to-release sequence explicit and consistent across AI and human-facing workflow guidance
- files to update:
  - `ai/EXECUTION.md`
  - `ai/WORKFLOW.md`
  - `ai/RELEASES.md`
  - `ai/PROMPTS.md`
  - `README.md`
  - `CONTRIBUTING.md`
- behavior to preserve:
  - releases still happen only from validated `main`
  - no GitHub Actions automation or application behavior changes
  - prompts remain lean and point to the owning guidance files instead of restating long rules
- exact deliverables:
  - explicit execution guidance that local implementation/validation/integration readiness come before any GitHub push or PR creation
  - explicit workflow guidance that PR creation is the last execution step and release work does not begin from side branches
  - explicit release guidance that release preparation starts after the approved PR has been merged onto `main`
  - aligned human-facing wording in `README.md` and `CONTRIBUTING.md` where release and PR expectations overlap

### Milestone 2: Tighten Planning Intake And Prompted Clarification
- goal:
  - make planning more clarification-driven when user requests leave important requirements underspecified
- files to update:
  - `ai/PLAN.md`
  - `ai/PROMPTS.md`
  - `AGENTS.md` only if the AI document maintenance or planning-role rules need top-level clarification
- behavior to preserve:
  - planning stays spec-first and decision-complete
  - roadmap-only reprioritization remains outside plan-file creation unless the work is concrete enough to execute
- exact deliverables:
  - stronger planning rules for asking targeted requirement questions about scope, compatibility, rollout, acceptance criteria, and validation gaps
  - explicit instructions for recording unresolved gaps and fallback assumptions in plans
  - updated prompt starters that drive clarification-first planning instead of treating missing detail as a normal implementation assumption

### Milestone 3: Add Focused AI Guides And Rebalance Ownership
- goal:
  - reduce overlap in the existing AI docs by moving recurring style, testing, review, and documentation guidance into focused files with clear ownership
- files to update:
  - `AGENTS.md`
  - `README.md`
  - `CONTRIBUTING.md`
  - likely new `ai/CODE_STYLE.md`
  - likely new `ai/TESTING.md`
  - likely new `ai/REVIEWS.md`
  - likely new `ai/DOCUMENTATION.md`
  - any existing `ai/*.md` files that currently own the guidance being moved
- behavior to preserve:
  - document roles remain distinct and non-overlapping
  - `ai/PROMPTS.md` stays a prompt library, not a policy dump
  - no application-contract docs or source code are modified unnecessarily
- exact deliverables:
  - a documented audit of what guidance moved and why
  - focused AI guides covering code style, testing, code review/security review, and documentation expectations
  - updated cross-references in `AGENTS.md`, `README.md`, `CONTRIBUTING.md`, and the existing AI docs so the new files are discoverable and authoritative within their scope

### Milestone 4: Define Phase-Based Multi-Agent Workflow
- goal:
  - make delegated execution phase-aware so coordinators can split work by requirements, planning, investigation, coding, testing, review, security review, and documentation when that coordination is worth it
- files to update:
  - `ai/WORKFLOW.md`
  - `ai/PROMPTS.md`
  - `AGENTS.md` if the AI document set or maintenance rules need corresponding updates
- files to preserve:
  - `ai/EXECUTION.md` remains the single-agent authority
  - `ai/RELEASES.md` remains the release authority
- exact deliverables:
  - explicit phase-owner or dedicated-agent guidance for requirements gathering, plan creation, investigation, coding, testing, code review, security review, and documentation
  - decision rules for when phase-specific agents are worth the coordination cost and when a single-agent flow is still preferred
  - prompt and handoff guidance that keeps phase-based execution reviewable and bounded instead of ad hoc parallelism

## Edge Cases And Failure Modes
- If PR creation and push guidance is added in multiple files without a single consistent sequence, the instruction set will remain contradictory and agents will keep improvising.
- If the release docs are changed without keeping the checked-in GitHub Actions behavior in view, the repository can end up documenting a flow that the automation does not support.
- If new AI files are added without moving or deleting overlapping guidance from existing files, the repository will become harder to follow rather than clearer.
- If planning guidance demands clarification for every small uncertainty, planning will become slower and noisier instead of more precise. The updated rule must target only scope, compatibility, rollout, acceptance criteria, and validation gaps that materially affect intent.
- If multi-agent guidance mandates phase-specific workers for every task, the workflow will become heavier than the repo warrants. The final guidance must keep delegation optional and justified by coordination value.
- If `README.md` and `CONTRIBUTING.md` are left unchanged after updating AI workflow sequencing and document inventory, the human-facing and AI-facing guidance will drift again.
- If execution edits `.github/workflows/*.yml` under this plan without first proving a documentation contradiction that cannot be resolved by wording alone, the scope will silently expand from guidance hardening into delivery automation changes.

## Validation Plan
- Commands to run during execution:
  - `./gradlew.bat build`
- Tests to add or update:
  - no new `src/test/java/` tests are planned because this is AI/documentation workflow work, not application behavior work
- Docs or contract checks:
  - review the final diff to confirm PR sequencing, release sequencing, planning-intake expectations, and multi-agent phase guidance are consistent across `AGENTS.md`, `README.md`, `CONTRIBUTING.md`, and the relevant `ai/` files
  - verify any new AI files are referenced from `AGENTS.md` and any human-facing project-map entries that mention them
  - verify `ai/PROMPTS.md` remains lean and points to owning documents rather than duplicating long-form policy
  - confirm OpenAPI, REST Docs, HTTP examples, and application source files remain unchanged
- Manual verification steps:
  - read the updated execution path and confirm it clearly answers: local work first, PR creation last, merge to `main`, then release preparation
  - read the updated release path and confirm it clearly answers: release starts from merged `main`, not from a feature branch or open PR
  - read the updated planning guidance and confirm it explicitly calls for targeted clarification questions and recording unresolved requirement gaps
  - inspect the AI document inventory and confirm each new focused guide has one clear purpose and that overlapping guidance was removed from older files
  - read the updated multi-agent workflow and confirm it names the requested execution phases without forcing unnecessary delegation for small changes

## Better Engineering Notes
- The smallest coherent implementation is documentation-first hardening, not automation churn. The checked-in GitHub workflows already encode the actual CI and tag-driven release behavior; this plan should clarify how humans and agents use that automation rather than rewriting it opportunistically.
- A minimal focused-file split is preferable to adding many new overlapping guides. The plan uses four new targeted AI files to cover the roadmap's named gaps while keeping roles legible.
- If execution discovers that contributor-facing human guidance needs a larger rewrite than `README.md` and `CONTRIBUTING.md` can absorb cleanly, stop and split that broader documentation work into a follow-up plan instead of folding it into this AI hardening task.

## Validation Results
- 2026-05-02 Milestone 1 completed:
  - updated `ai/EXECUTION.md`, `ai/WORKFLOW.md`, `ai/RELEASES.md`, `ai/PROMPTS.md`, `README.md`, and `CONTRIBUTING.md` to make the local execution -> PR handoff -> merged `main` -> release sequence explicit
  - preserved `.github/workflows/ci.yml` and `.github/workflows/release.yml` unchanged
  - validation run: manual document-consistency review for the milestone changes; final `.\gradlew.bat build` still pending until the full plan is complete
- 2026-05-02 Milestone 2 completed:
  - updated `ai/PLAN.md` to require clarification-first planning for material scope, compatibility, rollout, acceptance-criteria, and validation gaps
  - updated `ai/PROMPTS.md` so planning prompts require explicit requirement-gap recording and fallback assumptions
  - validation run: manual review of plan-template and prompt consistency; final `.\gradlew.bat build` still pending until the full plan is complete
- 2026-05-02 Milestone 3 completed:
  - added focused ownership guides: `ai/CODE_STYLE.md`, `ai/TESTING.md`, `ai/REVIEWS.md`, and `ai/DOCUMENTATION.md`
  - updated `AGENTS.md`, `README.md`, `CONTRIBUTING.md`, `ai/EXECUTION.md`, and `ai/PROMPTS.md` so those new guides are discoverable and own the recurring style, testing, review, and documentation guidance
  - validation run: manual review of document ownership, cross-references, and focused-guide discoverability; final `.\gradlew.bat build` still pending until the full plan is complete
- 2026-05-02 Milestone 4 completed:
  - rewrote `ai/WORKFLOW.md` around explicit phase ownership for requirements, planning, investigation, coding, testing, code review, security review, and documentation
  - updated `ai/PROMPTS.md` so delegated-execution prompts ask for explicit phase splits instead of ad hoc task parallelism
  - validation run: manual review of phase ownership, delegation decision rules, and release handoff sequencing; final `.\gradlew.bat build` still pending until the full plan is complete
- 2026-05-02 Final validation:
  - initial `.\gradlew.bat build` failed immediately because the shell defaulted to `JAVA_HOME=C:\Program Files\Microsoft\jdk-11.0.31.11-hotspot\`, while Gradle 9.5 in this repo requires JVM 17 or later
  - reran `.\gradlew.bat build` with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`; that retry reached `spotlessMiscCheck` and failed on whitespace-only blank lines in `CHANGELOG.md`
  - removed the whitespace-only blank lines around `## [Unreleased]` in `CHANGELOG.md`
  - reran `.\gradlew.bat build` with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`: passed
  - `.\gradlew.bat gatlingBenchmark` not run because this plan changed repository guidance only, not the benchmark-sensitive application behavior listed in `AGENTS.md`

## User Validation
- Read the updated AI guidance and confirm you can trace one end-to-end story for: planning, local execution, PR creation, merge-to-main, release preparation, and optional phase-based delegation.
- Check the new focused AI files and confirm the guidance you asked for exists for code style, testing, reviews/security review, and documentation without repeating the same rules across unrelated files.
- Review `README.md` and `CONTRIBUTING.md` and confirm the human-facing maintainer workflow still matches the AI-facing workflow where their scopes overlap.
