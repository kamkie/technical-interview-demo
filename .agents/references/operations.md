# Operations Guide For AI Agents

`.agents/references/operations.md` owns Deployment and Operations phase activities, plus the cross-cutting `Rollback`, `Hotfix`, `Patch`, `Backport`, and `Deprecate` triggers.
It does not own release publication, branch mechanics, validation command syntax, review policy, local environment setup, local development workflows, or incident-specific runbooks.

Use this file only after a released artifact is being staged, promoted, verified, rolled back, observed in runtime, or corrected because of a post-release signal.
Use `.agents/references/releases.md` for release preparation and publication before promotion starts.
Use `.agents/references/workflow.md` for branch, worktree, integration, and handoff mechanics after this guide chooses the operational path.
Use `.agents/references/testing.md` for validation command selection.

## When To Load

Load this guide when the task asks for:

- deployment staging, promotion, post-deploy verification, or deployed smoke checks
- rollback after a failed deployment or failed post-deploy verification
- incident response, runtime-signal triage, or post-release defect classification
- hotfix, patch, backport, or deprecation routing after a release or live signal
- deciding whether a runtime finding should become a roadmap item, planned fix, or durable learning

Do not load this guide for:

- release planning, release notes, tagging, publication, or release artifact verification before promotion starts
- local environment setup, local `bootRun`, local Docker onboarding, IDE setup, local development commands, or generic troubleshooting
- ordinary implementation, validation, review, or integration work before a release artifact exists
- speculative deployment policy when the user has not named a target environment or operational signal

If a task crosses from release publication into staging, promotion, post-deploy smoke, or rollback, switch from `.agents/references/releases.md` to this guide and drop release-only context.

## Operating Rules

- Identify the released artifact, target environment, source signal, affected behavior, and current lifecycle phase before acting.
- Do not invent deployment targets, credentials, cluster names, incident severity, rollback authority, or production access.
- If a required target, credential, release reference, or validation signal is missing, record the blocker and ask for the missing input.
- Keep public behavior, executable specs, REST Docs, OpenAPI, and README contract summaries aligned when a post-release fix changes behavior.
- Keep branch and worktree mechanics in `.agents/references/workflow.md`; this guide owns when to choose rollback, hotfix, patch, backport, or deprecation.
- Keep validation command details in `.agents/references/testing.md`; this guide owns which operational activity needs proof.
- Apply `.agents/references/reviews.md` security review triggers when the operational change touches auth, sessions, secrets, CI permissions, release paths, deployment-facing config, or exposed endpoints.
- Route durable lessons from avoidable operational failures through `.agents/references/learning-rules.md`; store accepted repo-wide lessons in `.agents/references/LEARNINGS.md`.
- Update `ROADMAP.md` when a live signal becomes active, planned, deferred, or intentionally rejected work.

## Deployment Loop

The Deployment loop is `Stage -> Smoke -> Promote -> Verify -> Rollback?`.
Use it only when a release artifact exists and the user has asked for deployment or post-deploy verification work.

### Stage

Question: should the released artifact enter a named non-production or promotion target?

Required inputs:

- artifact reference, such as image tag, digest, release tag, or deployed commit
- target environment or cluster
- expected runtime profile and operational metadata when the target exposes it
- relevant runbook or deployment artifact path when one exists

Exit when the artifact is staged, staging is blocked with a named reason, or the task is routed back to Release because the artifact is not publish-ready.

### Smoke

Question: does the staged artifact satisfy the smallest required runtime proof?

Use existing runbooks and wrapper tasks from `docs/OPERATIONS.md`, deployment assets, CI workflow documentation, or the user-supplied environment instructions.
Choose validation through `.agents/references/testing.md`.

Record:

- exact command, workflow, endpoint, or manual check
- target base URL or environment name when safe to record
- pass, fail, skipped, or blocked result
- missing credentials, unavailable tooling, or environment limitations

Exit when smoke passes, fails with a diagnosis path, or is blocked by missing operational input.

### Promote

Question: should the staged artifact move to the next named environment?

Promote only when:

- the user requested or approved promotion
- staging smoke passed or the user accepted the risk of a narrower proof
- rollback or recovery expectations are known for the target

Do not promote from conversation assumptions.
If approval, target, or proof is ambiguous, stop and ask.

### Verify

Question: does the promoted artifact behave correctly in the target runtime?

Verification may include health, readiness, root metadata, API smoke, session or CSRF checks, database migration checks, metrics scraping, or user-supplied post-deploy assertions.
Use the narrowest proof that matches the deployment risk and record residual risk if the environment cannot prove a path.

Exit when verification passes, fails and triggers `Rollback`, or is blocked by missing access or unavailable signals.

### Rollback?

Question: should the deployment be backed out instead of fixed forward?

Trigger rollback consideration when deployed behavior fails verification, readiness fails, runtime metadata identifies the wrong artifact, critical user behavior is broken, or a release-specific migration/runbook says the current rollout is unsafe.
Rollback does not by itself change application code.
Use the target environment's deployment or release runbook for commands, and verify after rollback with the same affected checks that failed where practical.

If schema or data changes make rollback unsafe, record the blocker and route to hotfix, forward-fix, restore, or human escalation according to the target runbook.

## Operations Loop

The Operations loop is `Observe -> Operational-Triage -> (Hotfix? or Patch?) -> Capture-Learning -> Sync`.
Use it when an artifact is live or a post-release signal arrives.

### Observe

Question: what signal indicates runtime behavior needs attention?

Valid signals include:

- user report, operator report, or support reproduction
- scheduled post-deploy smoke failure
- readiness, liveness, metrics, alert, or log signal
- dependency, platform, or credential expiry signal
- security, abuse, data-integrity, or privacy concern

Capture the source, time window, affected release or environment, observed behavior, expected behavior, and any known reproduction.
Avoid recording secrets, tokens, personal data, or sensitive request payloads.

### Operational-Triage

Question: what response path fits the signal?

Use this severity ladder:

- `Critical`: outage, active security exposure, data corruption, or broken authentication/session behavior in a live target. Prefer rollback when safe; otherwise route to hotfix or human escalation.
- `High`: important documented behavior is broken for released users, but rollback is not clearly required. Route to hotfix or patch based on urgency.
- `Normal`: defect or operational gap can follow normal planning. Route to patch or roadmap triage.
- `Maintenance`: cleanup, deprecation, backport consideration, or durable learning. Route to the matching trigger below.

Record uncertainty explicitly.
If the signal changes product scope or public contract behavior, route through requirements, specs, or ADRs before implementation.

### Hotfix?

Question: does this need a minimal fix outside normal planning flow?

Use hotfix only when waiting for normal planning creates unacceptable operational, security, or user-facing risk.
A hotfix still follows spec-driven development:

- identify the governing spec or contract artifact
- update the smallest necessary spec, test, or documentation artifact
- implement the smallest correction
- run the required validation
- run review and security review when triggered
- use `.agents/references/releases.md` for any new release publication

Use `.agents/references/workflow.md` for branch and integration mechanics once hotfix is selected.

### Patch?

Question: can the correction follow the normal planned work path?

Use patch for non-urgent post-release defects, runbook gaps, documentation corrections, or maintenance fixes.
Patch work normally routes through `ROADMAP.md`, `.agents/references/planning.md`, and `.agents/references/execution.md` before release.
Do not label ordinary planned work as hotfix only because the source signal came from Operations.

### Capture-Learning

Question: did the signal reveal durable repo-wide guidance?

Use `.agents/references/learning-rules.md` to decide whether the lesson should survive the current incident, release, or plan, then update `.agents/references/LEARNINGS.md` only for accepted repo-wide lessons.
Otherwise record the result in the relevant roadmap row, plan, final response, or operational evidence.

### Sync

Question: are the durable tracking artifacts aligned with the operational outcome?

Update `ROADMAP.md` when work becomes active, planned, deferred, released, or intentionally rejected.
Update `CHANGELOG.md` only as part of release work.
Keep post-release evidence in the owning plan, workflow state, release artifact, or user-facing final report as appropriate.

## Cross-Cutting Triggers

### Rollback

A `Rollback` trigger fires when deployed behavior fails verification or an operational signal says the current rollout should be backed out.
Use deployment runbooks for commands, `.agents/references/testing.md` for verification proof, and `.agents/references/workflow.md` only if repository branches or worktrees are needed for follow-up work.

### Hotfix

A `Hotfix` trigger fires when a live or post-release issue requires a minimal correction faster than normal planning can deliver.
Use the hotfix path in this guide to choose urgency, then switch to execution, workflow, release, and testing guides for the implementation and publication details.

### Patch

A `Patch` trigger fires when a post-release issue should be corrected through normal planned work.
Route to Triage or Planning, keep active-work tracking in `ROADMAP.md`, and avoid bypassing normal validation just because the defect is known.

### Backport

A `Backport` trigger fires only when the user names an older supported line or branch that must receive a fix.
Do not assume a backport policy exists.
Confirm the target line, compatibility constraints, release path, and validation scope before editing.

### Deprecate

A `Deprecate` trigger fires when a behavior should be scheduled for future removal or replacement.
Deprecation is a contract decision: update ADRs, PRDs, standalone specs, REST Docs, OpenAPI, README, roadmap, or changelog only according to the lifecycle phase and release state.
Do not break stable-line behavior unless `ROADMAP.md` and the governing contract policy allow it.

## Validation Expectations

- Deployment `Smoke` and `Verify` use environment-specific checks selected through `.agents/references/testing.md` and the applicable runbook.
- Rollback validation repeats the failed health, readiness, smoke, or behavior check where practical.
- Hotfix and patch implementation validation follows `.agents/references/testing.md` for the changed code, docs, workflow, or deployment artifacts.
- Backports require validation on the named target line; if the target line cannot be checked locally, record the exact gap.
- Deprecation work requires documentation and contract consistency review, plus executable checks when behavior changes.
- If validation cannot run because the environment, credentials, or tooling are unavailable, record the missing prerequisite and residual risk.

## Cross-References

- `.agents/references/application-lifecycle.md`: phase, activity, loop, and trigger vocabulary.
- `.agents/references/releases.md`: release preparation, tagging, publishing, and artifact verification before promotion starts.
- `.agents/references/workflow.md`: branch, worktree, delegation, integration, and remote handoff mechanics.
- `.agents/references/testing.md`: validation command selection and evidence recording.
- `.agents/references/reviews.md`: review and security review triggers.
- `.agents/references/documentation.md`: artifact routing and cross-file alignment.
- `.agents/references/learning-rules.md`: durable lesson routing and recording criteria.
- `.agents/references/LEARNINGS.md`: durable lesson storage for operational failures or repeated signals.
