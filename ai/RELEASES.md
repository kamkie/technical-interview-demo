# Release Guide For AI Agents

`ai/RELEASES.md` explains how AI agents should create an intentional release in this repository after implementing a plan.

Use this file when the user asks to make a release, prepare a release commit, create a release tag, or finish plan execution with a versioned release.
Do not use this file for implementation planning, setup troubleshooting, or release history. Planning belongs in `ai/PLAN.md` and `ai/PLAN_*.md`. Setup belongs in `SETUP.md`. Release history belongs in `CHANGELOG.md`.

## Release Goal

A release in this repository means:

- the requested implementation work is complete
- all intended release changes are integrated on `main`
- the governing specs and published contract artifacts are aligned
- `CHANGELOG.md` has a new released version entry
- the release commit is tagged with an annotated semantic version tag

Releases are intentional. Do not update `CHANGELOG.md` or create a tag unless the user asked for a release.
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
- confirm all intended release changes have been merged or otherwise integrated onto `main`
- confirm `git branch --show-current` is `main`
- confirm the target plan's `Validation Results` section reflects what actually ran
- confirm `.\gradlew.bat build` passed for the release candidate
- confirm OpenAPI, REST Docs, HTTP examples, and `README.md` were updated when the change required them
- confirm the worktree is in the expected state before creating the release commit

If the implementation is incomplete, specs are not aligned, the build is failing, or the release candidate is not on `main`, do not make a release.

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
2. Keep the changelog human-readable and limited to released user-visible changes. Do not add plan-completion notes, agent notes, or internal transcript detail.
3. Leave a fresh `## [Unreleased]` section at the top.
4. Review the release diff to ensure it contains only intended implementation, spec, and release metadata changes.
5. Run `.\gradlew.bat build` again if release metadata edits could have invalidated generated artifacts or if the earlier validation result is stale.

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
- the target plan's `Validation Results` section still reflects the final verified state

If any of these checks fail, fix the issue before calling the release complete.

## Pushing

Only push when the user asked for it or the task explicitly includes remote publication.

When push is requested:

1. push the release commit to the intended branch
2. push the annotated tag
3. verify the remote accepted both updates

Do not assume a remote push is always desired just because a local release tag exists.

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
4. Run `.\gradlew.bat build`.
5. Update `CHANGELOG.md` for the chosen version.
6. Commit with `Prepare vMAJOR.MINOR.PATCH release`.
7. Create an annotated tag `vMAJOR.MINOR.PATCH`.
8. Verify clean status, branch, tag placement, and changelog alignment.
