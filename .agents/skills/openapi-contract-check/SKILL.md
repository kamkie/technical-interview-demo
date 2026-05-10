---
name: openapi-contract-check
description: Check OpenAPI compatibility and approved baseline changes for public API work. Use when controllers, REST Docs, schemas, request or response payloads, frontend contract snapshots, or approved-openapi.json may change.
---

# OpenAPI Contract Check

## Overview

Use this skill for public API contract verification.
Executable tests, REST Docs, and the approved OpenAPI baseline remain higher-priority truth.

## Read Set

- always: `AGENTS.md`, `.agents/references/testing.md`, `.agents/references/documentation.md`
- when reviewing a diff: `.agents/references/reviews.md`
- when public API docs changed: affected `src/docs/asciidoc/` files and REST Docs tests
- when baseline changed: `src/test/resources/openapi/approved-openapi.json`

## Inputs

- API change or diff
- expected contract behavior
- generated or approved OpenAPI artifacts
- validation evidence

## Workflow

1. Identify whether public API behavior intentionally changed.
2. Confirm executable specs, REST Docs, OpenAPI baseline, HTTP examples, and README move together when required.
3. Run or require the compatibility checks named by `.agents/references/testing.md`.
4. Refresh the approved OpenAPI baseline only through the documented wrapper command after intentional contract review.
5. Treat compatibility failures as spec failures, not generated noise.
6. Report whether the contract changed, remained compatible, or needs replanning.

## Stop Conditions

- public behavior changed without governing spec or documentation updates
- OpenAPI baseline changed without intentional contract review
- compatibility check fails
- README, REST Docs, frontend contract, or HTTP examples drift from executable truth

## Output

Report affected contract artifacts, commands, result, compatibility decision, baseline status, and follow-up required.
