# Release Guide For AI Agents

`ai/RELEASES.md` explains how AI agents should create an intentional release in this repository after implementing a plan.

Use this file when the user asks to make a release, prepare a release commit, create a release tag, or when plan execution reaches the final release step.
Do not use this file for implementation planning, non-release execution, setup troubleshooting, or release history. Planning belongs in `ai/PLAN.md` and `ai/PLAN_*.md`. Non-release execution belongs in `ai/EXECUTION.md`. Setup belongs in `SETUP.md`. Release history belongs in `CHANGELOG.md`.

## Release Goal

A release in this repository means:

- the requested implementation work is complete
- all intended release changes are integrated on `main`
- the governing specs and published contract artifacts are aligned
- `CHANGELOG.md` has a new released version entry
- `ROADMAP.md` has been updated so newly completed work is no longer tracked as active roadmap work
- the release commit is tagged with an annotated semantic version tag
- pushing that annotated tag may publish the container image and create the GitHub Release artifact

Releases are intentional. Do not update `CHANGELOG.md` or create a tag before the implemented plan is complete and validated.
Cut releases only from `main`. Do not create a release from a feature branch, detached `HEAD`, or a partially integrated branch tip.

## Before You Release

Read these artifacts before making release changes:

- `AGENTS.md`
- the executed `ai/PLAN_*.md` file
- `CHANGELOG.md`
- `README.md` if the supported contract changed
- relevant docs under `src/docs/asciidoc/` if public behavior changed

Inspect repository state before editing release metadata:

- confirm the target plan was fully executed
- confirm any work done in a git worktree or side branch has already been integrated back onto `main`
- confirm all intended release changes have been merged or otherwise integrated onto `main`
- confirm `git branch --show-current` is `main`
- confirm the target plan's `Validation Results` section reflects what actually ran
- confirm `.\gradlew.bat build` passed for the release candidate
- confirm OpenAPI, REST Docs, HTTP examples, and `README.md` were updated when the change required them
- confirm `ROADMAP.md` was updated to remove work completed by the release from the active roadmap
- confirm the worktree is in the expected state before creating the release commit

If the implementation is incomplete, specs are not aligned, the build is failing, or the release candidate is not on `main`, do not make a release.

## Maintainer Release Checklist

Before creating an annotated release tag, confirm all of these on `main`:

1. review any new Flyway migration files under `src/main/resources/db/migration/` and confirm they are intentional for the target version
2. confirm `.\gradlew.bat build` passed for the exact release candidate
3. confirm OpenAPI compatibility still passes as part of the standard build and no unreviewed baseline refresh slipped in
4. decide whether `.\gradlew.bat gatlingBenchmark` is required because the change touched book search/list behavior, localization lookup behavior, or OAuth/session startup behavior
5. move the intended `CHANGELOG.md` entries into the versioned release section
6. update `ROADMAP.md` to remove the completed released work from the active roadmap
7. archive the executed `ai/PLAN_*.md` file and update moved-path references in the same change
8. create the annotated tag only after the release commit exists locally on `main`
9. after push, verify the remote accepted both `main` and the tag, the `Release` workflow passed, the GitHub Release was created, and GHCR published both:
   - the semantic tag image `ghcr.io/<owner>/<repo>:vMAJOR.MINOR.PATCH`
   - the immutable short-SHA image `ghcr.io/<owner>/<repo>:sha-<12-char-commit>`

## Choosing The Version

Use semantic version tags in the form `vMAJOR.MINOR.PATCH`.

Choose the next version deliberately:

- increment `PATCH` for backward-compatible fixes, cleanup, or doc-aligned contract corrections
- increment `MINOR` for backward-compatible feature additions or meaningful contract expansion
- increment `MAJOR` only for intentional breaking changes

Before choosing the version:

1. inspect existing tags with `git tag --sort=v:refname`
2. inspect first-parent history with `git log --first-parent --decorate --oneline`
3. keep version numbers increasing in first-parent history order

Do not reuse or skip to a lower version than an already published first-parent release.

## Preparing The Release Commit

Start by syncing your local `main` checkout to the intended integrated state. If required work is still sitting on another branch, integrate it first and only then prepare the release from `main`.

1. Move the relevant `CHANGELOG.md` content from `## [Unreleased]` into a new version section using the chosen tag and the release date in `YYYY-MM-DD` format.
2. Update `ROADMAP.md` so completed items released in this version are removed from active roadmap sections.
3. Keep the changelog human-readable and limited to released user-visible changes. Do not add plan-completion notes, agent notes, or internal transcript detail.
4. Leave a fresh `## [Unreleased]` section at the top.
5. Review the release diff to ensure it contains only intended implementation, spec, roadmap, and release metadata changes.
6. Run `.\gradlew.bat build` again if release metadata edits could have invalidated generated artifacts or if the earlier validation result is stale.

The release commit message should match the existing repository pattern:

```text
Prepare vMAJOR.MINOR.PATCH release
```

Use a normal non-interactive commit. Do not amend an existing commit unless the user explicitly asks for it.

## Creating The Release Tag

After the release commit exists:

1. create an annotated tag named `vMAJOR.MINOR.PATCH`
2. use a concise annotation message such as `Release vMAJOR.MINOR.PATCH`
3. verify the tag points at the release commit with `git show --stat <tag>`

Releases in this repository are based on annotated tags. Lightweight tags are not sufficient.

## Final Release Verification

Before reporting completion, verify:

- `git branch --show-current` is `main`
- `git status --short` is clean
- `git log --first-parent --decorate --oneline -n 5` shows the release commit and tag in the expected place
- `CHANGELOG.md` matches the chosen tag and date
- `ROADMAP.md` no longer lists the released work as active
- the target plan's `Validation Results` section still reflects the final verified state
- the executed plan file has been moved to `ai/archive/` and any moved-path references were updated in the same change

If any of these checks fail, fix the issue before calling the release complete.

## Pushing

Only push when the user asked for it or the task explicitly includes remote publication.

When push is requested:

1. push the release commit to the intended branch
2. push the annotated tag
3. verify the remote accepted both updates
4. monitor the tag-driven `Release` workflow until `./gradlew externalSmokeTest` passes for the tagged image and both container-image tags plus the GitHub Release are published
5. confirm the GitHub Release body matches the exact `## [vMAJOR.MINOR.PATCH]` section from `CHANGELOG.md` and includes the tag image reference, short-SHA image reference, and package-page link
6. remove temporary worktrees and branches that were used only to execute the released plan, after confirming their changes are already integrated onto `main`

Do not assume a remote push is always desired just because a local release tag exists.

The tag-driven `Release` workflow is also expected to validate the packaged tagged image via `./gradlew externalSmokeTest` before image publication.
The automated GitHub Release body is sourced from the exact matching `CHANGELOG.md` version section. If the tagged section is missing or duplicated, the release workflow is expected to fail closed instead of publishing partial notes.

## What Not To Do

- do not create another human-facing completion archive file
- do not record unreleased work as released
- do not cut a release from any branch other than `main`
- do not tag a commit that has not passed the required validation
- do not refresh the OpenAPI baseline unless the contract change was intentional and reviewed
- do not rewrite release history unless the user explicitly asks for recovery work

## Minimal Release Checklist

1. Execute the target plan.
2. Integrate all intended changes onto `main` and switch to `main`.
3. Update the plan's `Validation Results`.
4. Update `ROADMAP.md` to remove work completed by the release from the active roadmap.
5. Move the executed `ai/PLAN_*.md` file to `ai/archive/` and update moved-path references in the same change.
6. Run `.\gradlew.bat build`.
7. Update `CHANGELOG.md` for the chosen version.
8. Commit with `Prepare vMAJOR.MINOR.PATCH release`.
9. Create an annotated tag `vMAJOR.MINOR.PATCH`.
10. Push the release commit and annotated tag when requested, verify remote publication, and then clean up temporary execution worktrees and branches.
11. Verify clean status, branch, tag placement, changelog alignment, roadmap cleanup, and plan archival.
