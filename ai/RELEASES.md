# Release Guide For AI Agents

`ai/RELEASES.md` owns intentional release preparation after implementation is already complete on `main`.

Use this file when the task is to prepare a release commit, tag it, push it, or verify a published release.
Stay in `ai/PLAN_EXECUTION.md`, `ai/EXECUTION.md`, or `ai/WORKFLOW.md` while work is still local, on a side branch, or in an open PR.

When a coordinated multi-plan workflow used root-level `CHANGELOG_<topic>.md` files, those files are release inputs. Merge accepted entries into `CHANGELOG.md` and delete the temporary files in the same release-preparation change.

## Versioning And Release Rules

- use semantic version tags in the form `vMAJOR.MINOR.PATCH` for stable releases or `vMAJOR.MINOR.PATCH-PRERELEASE` for prereleases such as `v2.0.0-M1`, `v2.0.0-ALFA1`, `v2.0.0-BETA2`, or `v2.0.0-RC1`
- keep version numbers increasing in `git log --first-parent` order
- create releases only from `main` after all intended changes are integrated there
- create annotated tags for intentional releases
- keep `CHANGELOG.md` aligned with releases
- update `ROADMAP.md` after each release so completed items are removed, only active work remains, and `## Current Project State` reflects the new release phase, breaking-change policy, and next target version
- do not introduce another human-facing completion archive file

## Release Preconditions

Do not start release work until all of these are true:

- the target plan is fully executed and its `Lifecycle` and `Validation Results` are current
- the approved implementation PR is already merged onto `main`, or the user explicitly chose a no-PR flow and the reviewed change set is already on `main`
- any worktree or side-branch work is already integrated onto `main`
- local `main` is synced to the release-candidate state
- required specs, contract docs, and maintainer docs are aligned through `ai/DOCUMENTATION.md`
- required validation from `ai/TESTING.md` passed for the exact release candidate

## On-Demand Release References

Load detailed release mechanics only after the release task reaches the matching phase:

- `ai/references/RELEASE_CHECKLIST.md`: use when preparing the release metadata commit and annotated tag from local `main`
- `ai/references/RELEASE_ARTIFACT_VERIFICATION.md`: use when pushing a prepared release, monitoring publication, or verifying published artifacts

## Choosing The Version

Use semantic tags `vMAJOR.MINOR.PATCH` or prerelease tags `vMAJOR.MINOR.PATCH-PRERELEASE`.

Choose deliberately:

- increment `PATCH` for backward-compatible fixes, cleanup, or doc-aligned contract corrections
- increment `MINOR` for backward-compatible feature additions or meaningful contract expansion
- increment `MAJOR` only for intentional breaking changes
- apply prerelease suffixes such as `-M1`, `-ALFA1`, `-BETA2`, or `-RC1` only after choosing the underlying base version

Before choosing the version:

- inspect tags with `git tag --sort=v:refname`
- inspect first-parent history with `git log --first-parent --decorate --oneline`
- keep version numbers increasing in first-parent history order

## What Not To Do

- do not record unreleased work as released
- do not cut a release from any branch other than `main`
- do not tag a commit that has not passed the required validation
- do not start release work from an unmerged PR or side branch
- do not refresh the OpenAPI baseline unless the contract change was intentional and reviewed
- do not rewrite release history unless the user explicitly asks for recovery work
