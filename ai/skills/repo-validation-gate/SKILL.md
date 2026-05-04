---
name: repo-validation-gate
description: Decide and run repository-local validation after a change by classifying changed files, routing contract artifact checks, and choosing whether lightweight review or `.\gradlew.bat build` is required. Use when asked what validation is required, to run required validation only, to verify contract impact, to review documentation-only changes, or to summarize what passed, failed, or was skipped.
---

# Repo Validation Gate

## Overview

Use this skill to route validation quickly and consistently after a change.
Keep the skill focused on changed-file classification, proof selection, and reporting, then defer to the owner guides for the standing rules.

## Read Set

Read only what the request needs:

- always: `AGENTS.md`, `ai/TESTING.md`, `ai/DOCUMENTATION.md`, and `ai/REVIEWS.md`
- plan-coupled verification: the target `ai/PLAN_*.md`
- public contract review: `README.md`, `src/docs/asciidoc/`, `src/test/resources/http/`, and `src/test/resources/openapi/approved-openapi.json` as needed
- feature-sensitive checks: the changed source or test files that determine whether compatibility or benchmark gates apply

## Workflow

1. Run `pwsh ./scripts/classify-changed-files.ps1 -Uncommitted` unless the task clearly targets another diff boundary.
2. If `skipHeavyValidation=true`, do manual consistency review only unless the user explicitly asks for more.
3. Before env-dependent validation commands, check whether a local `.env` file exists and, in PowerShell, prefer dot-sourcing `./scripts/load-dotenv.ps1` so values such as `JAVA_HOME` load into the current shell.
4. Use `.env.example` only as the template for expected variable names, not as proof of local values.
5. Otherwise choose the smallest sufficient proof from `ai/TESTING.md`; default to `.\gradlew.bat build` when no narrower proof is justified.
6. If public behavior or documented schema changed, inspect the contract artifacts routed by `AGENTS.md` and `ai/DOCUMENTATION.md`.
7. If the change touches book search, localization lookup, or OAuth/session startup behavior, check whether `./gradlew gatlingBenchmark` is required.
8. Report the exact commands run, what passed, what failed, what was skipped, and which artifacts likely need updates.

## Guardrails

- do not refresh the approved OpenAPI baseline unless the contract intentionally changed and that review already happened
- treat benchmark and compatibility gates as spec checks, not optional cleanup
- if required validation cannot run, say exactly why and what remains unverified
- if `.env` is missing or incomplete for an env-dependent command, say which fallback was used
- do not claim a documentation-only shortcut when the classifier or changed files indicate code, contract, or workflow impact
- when the user asked for a review, lead with findings before the summary

## Useful Prompt Titles

- `Run Required Validation Only`
- `Verify Contract Impact`
- `Verify An Implemented Milestone`
- `Review A Diff For Risks`
- `Triage A Failed Validation Run`

Use prompt titles from `ai/PROMPTS.md` when the user wants a reusable starter.
