---
name: run-validation
description: Select, run, and record repository validation for code, docs, skills, and plan changes. Use when a Worker or Verifier needs the smallest sufficient proof, wrapper command selection, failure triage routing, or validation evidence for a plan or handoff.
---

# Run Validation

## Overview

Use this skill to choose and run validation for repository work.
`.agents/references/testing.md` remains the authority for validation scope and commands.

## Read Set

- always: `AGENTS.md`, `.agents/references/testing.md`, and the current diff or assigned handoff
- if command-wrapper behavior matters: `.agents/references/command-wrapper.md`
- if task overlap matters: `.agents/references/gradle-task-graph.md`
- if validation fails: `.agents/references/troubleshooting.md`
- if recording in a plan: the active plan's `Validation Results`

## Inputs

- changed files or diff boundary
- change type: public behavior, internal refactor, docs-only, skill, workflow, build, security, or release
- required validation target from the request, plan, or handoff
- whether the proof must cover only uncommitted changes or cumulative committed work

## Workflow

1. Classify the change using `.agents/references/testing.md`.
2. Choose the smallest sufficient proof, defaulting to `./build.ps1 build` unless the guide requires a narrower or broader command.
3. Use `./build.ps1 -FullBuild build` when proving cumulative committed work, whole plans, release candidates, or prior non-lightweight changes.
4. Do not run overlapping Gradle validations in parallel.
5. Run the command and preserve the exact result.
6. If validation fails, load `.agents/references/troubleshooting.md` before changing code, tests, or expectations.
7. Record exact commands, passed/failed/skipped status, wrapper shortcut behavior, and residual risk in the plan, workflow state, or final report.

## Stop Conditions

- validation target is unclear
- a command requires unavailable prerequisites
- a failure looks like a real spec, contract, benchmark, security, or compatibility failure
- the requested proof boundary is broader than the current diff and `-FullBuild` or another broader command is needed

## Output

Report the command, scope, result, skipped checks, failure summary when applicable, and recommended next activity: `Diagnose?`, `Fix?`, or `Replan?`.
