# Standalone Specs

This directory holds standalone behavior or contract specs for cases where the truth is not already clear in executable specs (integration tests, REST Docs tests, OpenAPI compatibility tests, `src/test/resources/openapi/approved-openapi.json`) or published contract docs (`README.md`, `src/docs/asciidoc/`).

For the full human-facing documentation map, see [docs/README.md](../README.md).
For lifecycle and artifact-routing guidance (ADR vs PRD vs standalone spec vs plan), see [DEVELOPMENT_LIFECYCLE.md](../DEVELOPMENT_LIFECYCLE.md).

## When To Add A Standalone Spec

Add a standalone spec when behavior or contract truth needs a single owning document and no existing executable spec or published doc already owns it. Typical triggers:

- A cross-cutting behavior (validation rule, error model, retry policy, security check) needs one canonical description.
- A contract detail must be pinned before tests or implementation exist to encode it.
- An ADR or PRD references behavior that is too detailed to live inside the ADR or PRD itself.

Prefer encoding behavior directly in executable specs when possible; standalone specs exist for the cases where executable coverage is not yet sufficient or appropriate.

If the change is product-shaped and ambiguous, use a PRD in [docs/requirements/](../requirements/).
If the change is architectural or process-level, use an ADR in [docs/decisions/](../decisions/).
For task-sized implementation checkpoints, use an execution plan under [.agents/plans/](../../.agents/plans/).

## How To Add A Standalone Spec

1. Copy [SPEC_TEMPLATE.md](SPEC_TEMPLATE.md) to a descriptive kebab-case filename in this directory.
2. Fill in the template sections (scope, behavior, acceptance criteria, links).
3. Link the spec from related ADRs, PRDs, execution plans, or executable specs that depend on it.
4. Update the index below.

## Accepted Specs

_None yet._

## Template

- [SPEC_TEMPLATE.md](SPEC_TEMPLATE.md)
