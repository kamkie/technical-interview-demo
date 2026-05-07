# Release Artifact Verification Reference

This on-demand reference owns detailed mechanics for pushing a prepared release, monitoring publication, and verifying published artifacts.
Load it only when the user asked to push a prepared release, verify a published release, or diagnose release-publication state.

## Published Artifact Verification

Use the immutable digest from the `Release` workflow summary, not a mutable GHCR tag.

The `Release` workflow currently pins `cosign v3.0.5` for signing and immediate post-sign verification. Use the same Cosign line for local manual checks unless `.github/workflows/release.yml` intentionally changes.

Verify the keyless image signature:

```powershell
docker run --rm ghcr.io/sigstore/cosign/cosign:v3.0.5 verify `
  ghcr.io/<owner>/<repo>@sha256:<digest> `
  --certificate-identity "https://github.com/<owner>/<repo>/.github/workflows/release.yml@refs/tags/vMAJOR.MINOR.PATCH[-PRERELEASE]" `
  --certificate-oidc-issuer https://token.actions.githubusercontent.com
```

Verify the GitHub provenance attestation:

```powershell
gh attestation verify oci://ghcr.io/<owner>/<repo>@sha256:<digest> `
  --repo <owner>/<repo> `
  --signer-workflow <owner>/<repo>/.github/workflows/release.yml `
  --source-ref refs/tags/vMAJOR.MINOR.PATCH[-PRERELEASE]
```

If an older local Cosign build reports `no signatures found`, switch to the repo-pinned Cosign line before assuming GHCR is missing the signature artifact.

## Push And Remote Verification

Push only when the user asked or the task explicitly includes remote publication.

When pushing:

1. push `main`
2. push the annotated tag
3. verify the remote accepted both updates
4. monitor the tag-driven `Release` workflow until `./build.ps1 externalSmokeTest` passes for the tagged image, the GitHub Release is created, and GitHub code scanning still reflects the expected CodeQL posture
5. confirm GHCR published both `ghcr.io/<owner>/<repo>:vMAJOR.MINOR.PATCH[-PRERELEASE]` and `ghcr.io/<owner>/<repo>:sha-<12-char-commit>`
6. confirm the immutable published digest is signed and has provenance attestation by running the commands in `Published Artifact Verification`
7. confirm the GitHub Release body includes every `CHANGELOG.md` section from the new tag back to, but not including, the previous published GitHub Release tag section, plus the tag image reference, short-SHA image reference, and package link
8. run the manual `Post-Deploy Smoke` workflow against the deployed environment with the release-summary inputs `expected_build_version`, `expected_short_commit_id`, `expected_active_profile=prod`, `expected_session_store_type=jdbc`, and `expected_session_timeout=15m`
9. when the JDBC secret set exists, confirm the same smoke run also proves `GET /api/session`, readable `XSRF-TOKEN` bootstrap, authenticated `PUT /api/account/language`, and persisted authenticated account access; environments without that JDBC access remain HTTP-only by design
10. remove temporary worktrees and branches used only to execute the released plan after confirming their changes are already integrated onto `main`

The `Release` workflow is expected to validate the exact tagged image, derive cumulative release notes from `CHANGELOG.md`, fail closed when the previous published release boundary or changelog section cannot be resolved, and treat the immutable digest rather than mutable tags as the authenticity anchor.
