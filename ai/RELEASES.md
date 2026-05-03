# Release Guide For AI Agents

`ai/RELEASES.md` explains how AI agents should create an intentional release in this repository after implementation is complete.

Use this file when the user asks to prepare a release commit, create a release tag, push a prepared release, or verify a published release.
Do not use this file for planning, ordinary implementation, setup troubleshooting, or release history ownership. Planning belongs in `ai/PLAN.md`. Non-release execution belongs in `ai/EXECUTION.md` or `ai/WORKFLOW.md`. Setup belongs in `SETUP.md`. Released history belongs in `CHANGELOG.md`.

Release work starts only after the implementation flow is finished and the approved implementation PR has already been merged onto `main`.
If the work is still local, still on a side branch, or still in an open PR, stay in execution mode.

When `ai/WORKFLOW.md` `Parallel Plans` mode was used, worker-owned temporary `CHANGELOG_<topic>.md` files are release inputs. Release preparation on `main` must merge their accepted entries into the canonical `CHANGELOG.md` and then delete the temporary files in the same release-preparation change.

## Release Goal

A release in this repository means:

- the requested implementation work is complete and integrated on `main`
- the governing specs and published contract artifacts are aligned
- `CHANGELOG.md` has a new released version entry
- `ROADMAP.md` no longer lists the released work as active
- the executed `ai/PLAN_*.md` file is archived under `ai/archive/`
- the release commit is tagged with an annotated semantic version tag

Releases are intentional. Do not update `CHANGELOG.md` or create a tag before the implemented plan is complete and validated.
Cut releases only from `main`.

## Before You Release

Read these artifacts before editing release metadata:

- `AGENTS.md`
- the executed `ai/PLAN_*.md` file
- `CHANGELOG.md`
- any root-level `CHANGELOG_*.md` temporary changelog files that belong to the released work
- `ROADMAP.md`
- any contract docs touched by the released work
- `ai/TESTING.md` and `ai/DOCUMENTATION.md` when validation scope or artifact alignment is unclear

Confirm all of these before proceeding:

- the target plan was fully executed
- the approved implementation PR has already been merged onto `main`, or the user explicitly chose a no-PR flow and the reviewed change set is already on `main`
- any work done in a branch or worktree has already been integrated onto `main`
- local `main` is synced to the release candidate state
- the target plan's `Validation Results` reflects what actually ran
- the required validation from `ai/TESTING.md` passed for the release candidate
- required contract and maintainer artifacts are aligned through `ai/DOCUMENTATION.md`

If any of those checks fail, do not start release work.

## Maintainer Release Checklist

Before creating an annotated release tag on merged `main`:

1. review any new Flyway migration files under `src/main/resources/db/migration/` and confirm they are intentional for the target version
2. confirm the required validation from `ai/TESTING.md` passed for the exact release candidate, including `./gradlew gatlingBenchmark` when that guide says it is required
3. confirm OpenAPI compatibility still passes as part of the standard build, the latest default-branch CodeQL run is healthy or intentionally reviewed, and no unreviewed baseline refresh slipped in
4. merge any accepted `CHANGELOG_<topic>.md` files for the release into `CHANGELOG.md`, then remove the temporary files
5. move the intended `CHANGELOG.md` entries into the versioned release section
6. update `ROADMAP.md` to remove the completed released work from active roadmap sections
7. archive the executed `ai/PLAN_*.md` file and update moved-path references in the same change
8. create the annotated tag only after the release commit exists locally on `main`
9. after push, verify the remote accepted both `main` and the tag, the `Release` workflow passed, the GitHub Release was created, GitHub code scanning still reflects the expected CodeQL posture, and GHCR published both:
   - the semantic tag image `ghcr.io/<owner>/<repo>:vMAJOR.MINOR.PATCH`
   - the immutable short-SHA image `ghcr.io/<owner>/<repo>:sha-<12-char-commit>`
   - a keyless signature and provenance attestation for the immutable published digest `ghcr.io/<owner>/<repo>@sha256:...`

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

## Preparing The Release Commit

Start from local `main` synced to the approved merged state.

1. Collect any accepted root-level `CHANGELOG_<topic>.md` files that belong to the release candidate.
2. Merge their unreleased entries into the canonical `CHANGELOG.md` under `## [Unreleased]`, preserving the intended released wording and removing duplicates.
3. Delete the consumed `CHANGELOG_<topic>.md` files in the same change once their content is represented in `CHANGELOG.md`.
4. Move the relevant `CHANGELOG.md` content from `## [Unreleased]` into a new version section using the chosen tag and the release date in `YYYY-MM-DD` format.
5. Update `ROADMAP.md` so completed items released in this version are removed from active roadmap sections.
6. Keep the changelog human-readable and limited to released user-visible changes.
7. Leave a fresh `## [Unreleased]` section at the top.
8. Review the release diff to ensure it contains only intended implementation, spec, roadmap, release metadata, and temporary-changelog cleanup changes.
9. Rerun `./gradlew.bat build` only if release-metadata edits could have invalidated generated artifacts or the earlier validation result is stale.

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
- no consumed `CHANGELOG_<topic>.md` files remain in the worktree after their entries were merged
- `ROADMAP.md` no longer lists the released work as active
- the target plan's `Validation Results` still reflects the final verified state
- the executed plan file has been moved to `ai/archive/` and any moved-path references were updated in the same change

## Pushing

Only push when the user asked for it or the task explicitly includes remote publication.

When push is requested:

1. push the release commit to the intended branch
2. push the annotated tag
3. verify the remote accepted both updates
4. monitor the tag-driven `Release` workflow until `./gradlew externalSmokeTest` passes for the tagged image and both container-image tags plus the GitHub Release are published
5. confirm the GitHub Release body includes every `CHANGELOG.md` version section from the new tag back to (but not including) the previous published GitHub Release tag section, plus the tag image reference, short-SHA image reference, and package-page link
6. confirm the published container package exposes the expected digest-first authenticity path: the pushed digest is signed and has provenance attestation bound to the same digest
7. remove temporary worktrees and branches used only to execute the released plan after confirming their changes are already integrated onto `main`

The tag-driven `Release` workflow is expected to validate the packaged tagged image via `./gradlew externalSmokeTest` before image publication.
The automated GitHub Release body is sourced cumulatively from `CHANGELOG.md`, spanning from the new tag section back to the previous published GitHub Release tag section.
The release workflow must fail closed when it cannot derive that previous published release boundary or the required changelog sections unambiguously.
The release workflow is also expected to sign and attest the immutable pushed GHCR digest rather than treating mutable tags as the trust anchor.

## What Not To Do

- do not record unreleased work as released
- do not cut a release from any branch other than `main`
- do not tag a commit that has not passed the required validation
- do not start release work from an unmerged PR or side branch
- do not refresh the OpenAPI baseline unless the contract change was intentional and reviewed
- do not rewrite release history unless the user explicitly asks for recovery work

## Minimal Release Checklist

1. Finish execution and any requested PR handoff.
2. Wait for the approved implementation PR to be merged onto `main`, then sync local `main`.
3. Confirm the plan's `Validation Results`, required artifacts, and required validation are current.
4. Merge any release-scoped `CHANGELOG_<topic>.md` files into `CHANGELOG.md`, then delete the temporary files.
5. Update `ROADMAP.md` to remove work completed by the release from the active roadmap.
6. Move the executed `ai/PLAN_*.md` file to `ai/archive/` and update moved-path references in the same change.
7. Update `CHANGELOG.md` for the chosen version.
8. Commit with `Prepare vMAJOR.MINOR.PATCH release`.
9. Create an annotated tag `vMAJOR.MINOR.PATCH`.
10. Push the release commit and annotated tag when requested, then verify remote publication.
11. Verify clean status, tag placement, changelog alignment, roadmap cleanup, temporary changelog cleanup, and plan archival.
