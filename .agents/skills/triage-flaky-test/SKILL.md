---
name: triage-flaky-test
description: Triage intermittent or suspected flaky repository test failures. Use when a test fails inconsistently, timing or environment instability is suspected, or Codex must decide whether to diagnose, fix, quarantine, or replan validation.
---

# Triage Flaky Test

## Overview

Use this skill to investigate suspected flaky validation without weakening tests prematurely.
`.agents/references/testing.md` and `.agents/references/troubleshooting.md` remain authoritative.

## Read Set

- always: `AGENTS.md`, `.agents/references/testing.md`, `.agents/references/troubleshooting.md`
- when reviewing a diff: `.agents/references/reviews.md`
- when task overlap matters: `.agents/references/gradle-task-graph.md`
- when the failing test owns public behavior: affected specs, REST Docs, or OpenAPI artifacts

## Inputs

- failing command and log summary
- test name, class, or task
- recent diff or commit range
- whether the failure is local, CI-only, or repeated

## Workflow

1. Preserve the first failing command and failure snippet.
2. Re-run the narrowest useful test or task enough to distinguish repeatable failure from intermittent behavior.
3. Check recent diffs for changes to timing, data setup, transactions, clocks, ports, Docker, external services, or shared state.
4. If repeatable, treat it as a real failure and fix the cause.
5. If intermittent, identify the stabilization fix before considering quarantine.
6. Do not weaken assertions, skip tests, or refresh baselines to hide a failure.
7. Record the final decision: `Diagnose?`, `Fix?`, `Replan?`, or validated environment issue.

## Stop Conditions

- failure affects compatibility, benchmark, security, or public contract checks
- logs are insufficient to identify the failing test or task
- a quarantine or skip would hide a real product failure
- validation target changes beyond the approved plan

## Output

Report commands run, pass/fail counts, suspected cause, evidence, recommended next activity, and any residual risk.
