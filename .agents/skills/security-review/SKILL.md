---
name: security-review
description: Review security-sensitive repository changes for authorization, credentials, logging, CI permissions, dependency, container, and deployment risk. Use when auth, sessions, secrets, external inputs, workflows, release paths, dependencies, or exposed endpoints change.
---

# Security Review

## Overview

Use this skill for Specialist security gates.
`.agents/references/reviews.md` owns security-review triggers and priorities.

## Read Set

- always: `AGENTS.md`, `.agents/references/reviews.md`, and the diff or handoff under review
- when docs or contract posture changes: `.agents/references/documentation.md`
- when validation scope matters: `.agents/references/testing.md`
- when workflow or CI permissions change: `.agents/references/workflow.md`
- when release behavior changes: `.agents/references/releases.md`

## Inputs

- diff or commit range
- source request, plan task, or handoff
- validation evidence
- known secrets, credentials, permissions, endpoints, or dependency changes

## Workflow

1. Identify the security-sensitive surface and governing docs or tests.
2. Check authorization, session handling, external input handling, sensitive logging, secrets, and credential exposure.
3. Check CI, release, container, dependency, and workflow permission changes when present.
4. Verify documentation and specs do not claim a weaker or stronger posture than the implementation.
5. Require validation evidence appropriate to the changed surface.
6. Return a gate decision: pass, pass with notes, blocked, or replan required.

## Stop Conditions

- missing auth or role checks
- secret or sensitive request data exposure
- broad workflow or release permissions without justification
- security-sensitive behavior changed without matching tests or documentation

## Output

Report findings first, then affected files, gate decision, required fixes, validation gaps, and residual risk.
