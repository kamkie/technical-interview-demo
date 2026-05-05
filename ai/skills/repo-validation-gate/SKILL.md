---
name: repo-validation-gate
description: Decide and run repository-local validation after a change by classifying changed files, routing contract artifact checks, and choosing whether lightweight review or the standard wrapper build is required. Use when asked what validation is required, to run required validation only, to verify contract impact, to review documentation-only changes, or to summarize what passed, failed, or was skipped.
---

# Repo Validation Gate

## Overview

Use this skill to route validation quickly and consistently after a change.
Keep the skill focused on changed-file classification, proof selection, and reporting, then defer to the owner guides for the standing rules.

## Read Set

Read only what the request needs:

- always: `AGENTS.md`, `ai/TESTING.md`, `ai/DOCUMENTATION.md`, `ai/ENVIRONMENT_QUICK_REF.md`, and `ai/REVIEWS.md`
- plan-coupled verification: the target `ai/PLAN_*.md`
- public contract review: `README.md`, `src/docs/asciidoc/`, `src/test/resources/http/`, and `src/test/resources/openapi/approved-openapi.json` as needed
- feature-sensitive checks: the changed source or test files that determine whether compatibility or benchmark gates apply

## Workflow

1. Run `pwsh ./scripts/classify-changed-files.ps1 -Uncommitted` unless the task clearly targets another diff boundary.
2. If `skipHeavyValidation=true`, do manual consistency review only unless the user explicitly asks for more.
3. Otherwise choose the smallest sufficient proof from `ai/TESTING.md`; default to the standard wrapper build when no narrower proof is justified.
4. Use wrapper commands from `ai/ENVIRONMENT_QUICK_REF.md`; do not add manual `JAVA_HOME` or dotenv setup unless a wrapper command fails and troubleshooting is in scope.
5. Use `.env.example` only as the template for expected variable names, not as proof of local values.
6. If public behavior or documented schema changed, inspect the contract artifacts routed by `AGENTS.md` and `ai/DOCUMENTATION.md`.
7. If the change touches book search, localization lookup, or OAuth/session startup behavior, check whether `gatlingBenchmark` is required; if `build` is also required, prefer one combined wrapper invocation such as `./build.ps1 build gatlingBenchmark --no-daemon`.
8. Report the exact commands run, what passed, what failed, what was skipped, and which artifacts likely need updates.

## Guardrails

- do not refresh the approved OpenAPI baseline unless the contract intentionally changed and that review already happened
- treat benchmark and compatibility gates as spec checks, not optional cleanup
- if required validation or wrapper-based execution cannot run, say exactly why and what remains unverified
- do not claim a documentation-only shortcut when the classifier or changed files indicate code, contract, or workflow impact
- when the user asked for a review, lead with findings before the summary

## Useful Prompt Titles

- `Run Required Validation`
- `Check Contract Impact`
- `Verify Milestone`
- `Review Diff Risks`
- `Triage Validation Failure`

Use prompt titles from `ai/PROMPTS.md` when the user wants a reusable starter.
