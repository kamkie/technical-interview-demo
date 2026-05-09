# Application Development Lifecycle Diagrams

This document accompanies `docs/specs/application-lifecycle-spec.md` and visualizes its three layered models (Phases, Phase Activities, Loops) plus cross-cutting triggers.

All diagrams use [Mermaid](https://mermaid.js.org/) syntax so they render natively on GitHub, IntelliJ Markdown preview, and most static-site renderers. The diagrams are descriptive, not normative; the spec text remains authoritative when text and diagrams disagree.

## How To Read These Diagrams

- **Solid arrows** mark the planned forward path between phases or phase activities.
- **Dashed arrows** mark *conditional* transitions: re-entry on failure, conditional phase activities (`?` in the spec), or trigger-driven switches.
- **Dotted arrows** mark cross-cutting triggers that may fire from any phase.
- A node label ending in `?` is a conditional activity or phase, matching the spec.
- Loop boundaries are shown as Mermaid `subgraph` boxes.

## 1. Phase Flow

The eleven phases and their normal forward transitions, with re-entry edges for failed gates and roadmap-feedback edges for scope changes discovered mid-cycle.

```mermaid
flowchart TD
    D[1. Discovery] --> RI[2. Roadmap Intake]
    RI --> P[3. Planning]
    P --> I[4. Implementation]
    I --> T[5. Testing]
    T --> R[6. Review]
    R --> IG[7. Integration]
    IG --> RL[8. Release]
    RL --> DP[9. Deployment]
    DP --> OP[10. Operations]
    OP --> CI[11. Continuous Improvement]
    CI --> RI

    %% Conditional re-entries on failed gates
    R -. changes requested .-> I
    T -. failure .-> I
    DP -. rollback .-> I
    OP -. hotfix .-> P
    IG -. post-merge failure .-> I

    %% Cross-cutting Replan
    I -. replan .-> P
    T -. replan .-> P

    %% Roadmap-feedback edges (localized Sync trigger; see diagram 6)
    P -. sync new/changed scope .-> RI
    I -. defer item to next cycle .-> RI
    OP -. backport / patch / deprecate backlog .-> RI

    classDef terminal fill:#eef,stroke:#446,stroke-width:1px;
    class CI terminal;
```

## 2. Loop Nesting

The six loops nest from outermost (per release) to innermost (per failing validation or per diff).

```mermaid
flowchart TB
    subgraph Outer[Outer Product Loop &nbsp;-&nbsp; per release]
        subgraph OAI[Operate-and-Improve Loop &nbsp;-&nbsp; continuous]
            obs[Observe → Triage → Hotfix?/Patch? → Capture-Learning → Sync]
        end
        subgraph PL[Plan Loop &nbsp;-&nbsp; per plan]
            subgraph MEL[Milestone Execution Loop &nbsp;-&nbsp; per milestone]
                subgraph RGL[Red-Green Loop &nbsp;-&nbsp; per failing validation]
                    rg[Run → Diagnose → Fix → Re-run]
                end
                subgraph RVL[Review Loop &nbsp;-&nbsp; per diff before merge]
                    rv[Self-Review → Code Review → Security Review? → Docs Review? → Decide]
                end
            end
        end
    end
```

## 3. Implementation Milestone Execution Loop

The activity sequence inside a single milestone, with the inner Red-Green and Review loops drawn explicitly. This is the loop the spec's section 5.3 names as "Milestone Execution Loop".

```mermaid
flowchart LR
    Spec[Spec] --> Code[Code]
    Code --> Docs[Docs]
    Docs --> Run[Run]
    Run --> Diag{passes?}
    Diag -- yes --> SR[Self-Review]
    Diag -- no --> Diagnose[Diagnose]
    Diagnose --> Fix[Fix]
    Fix --> Run
    Diagnose -. cannot fix .-> Replan[Replan?]
    Replan -. back to plan .-> Spec

    SR --> CR[Code Review]
    CR --> SecR{security trigger?}
    SecR -- yes --> Sec[Security Review]
    SecR -- no --> Decide
    Sec --> Decide{Decide}
    Decide -- changes --> Code
    Decide -- approve --> Commit[Commit]
    Commit --> Handoff[Handoff]
```

## 4. Plan Loop

Activity sequence for producing a decision-complete plan, with `Replan?` re-entry.

```mermaid
flowchart LR
    Frame --> Design --> Spec --> Decompose --> Validate[Validate-Plan]
    Validate -- ready --> Approved((Approved))
    Validate -- gaps --> Replan[Replan?]
    Replan --> Frame
```

## 5. Phase Activity Sequence

A compact view of the in-order primary activity sequence per phase. Each row mirrors section 4 of the spec.

```mermaid
flowchart TB
    subgraph Discovery
        d1[Scan] --> d2[Frame] --> d3[Clarify?] --> d4[Capture?]
    end
    subgraph RoadmapIntake[Roadmap Intake]
        r1[Intake] --> r2[Refine] --> r3[Prioritize] --> r4[Sequence] --> r5[Sync]
    end
    subgraph Planning
        p1[Frame] --> p2[Design] --> p3[Spec] --> p4[Decompose] --> p5[Validate-Plan] --> p6[Sync] --> p7[Replan?]
    end
    subgraph Implementation
        i1[Spec] --> i2[Code] --> i3[Docs] --> i4[Run] --> i5[Replan?] --> i6[Self-Review] --> i7[Code Review] --> i8[Security Review?] --> i9[Commit] --> i10[Handoff]
    end
    subgraph Testing
        t1[Plan-Tests] --> t2[Author-Tests] --> t3[Run] --> t4[Diagnose?] --> t5[Fix?] --> t6[Re-run] --> t7[Record]
    end
    subgraph Review
        rv1[Self-Review] --> rv2[Code Review] --> rv3[Security Review?] --> rv4[Docs Review?] --> rv5[Decide]
    end
    subgraph Integration
        ig1[Re-validate] --> ig2[Resolve-Conflicts?] --> ig3[Merge] --> ig4[Post-Merge-Verify]
    end
    subgraph Release
        rl1[Gate] --> rl2[Tag] --> rl3[Notes] --> rl4[Publish] --> rl5[Post-Release-Cleanup]
    end
    subgraph Deployment
        dp1[Stage] --> dp2[Smoke] --> dp3[Promote] --> dp4[Verify] --> dp5[Rollback?]
    end
    subgraph Operations
        op1[Observe] --> op2[Triage] --> op3[Hotfix?] --> op4[Patch?] --> op5[Backport?] --> op6[Deprecate?]
    end
    subgraph ContinuousImprovement[Continuous Improvement]
        ci1[Retrospect] --> ci2[Capture-Learning] --> ci3[Refactor?] --> ci4[Tech-Debt-Plan?] --> ci5[Sync]
    end

    Discovery --> RoadmapIntake --> Planning --> Implementation --> Testing --> Review --> Integration --> Release --> Deployment --> Operations --> ContinuousImprovement
    ContinuousImprovement -. next cycle .-> RoadmapIntake
```

## 6. Cross-Cutting Triggers

Triggers fire from any phase and force a switch. They are not a phase of their own.

```mermaid
flowchart LR
    AnyPhase((Any Phase))
    AnyPhase -. plan gap / scope drift .-> Replan[Replan]
    AnyPhase -. security-relevant change .-> SecR[Security Review]
    AnyPhase -. tracking impact .-> Sync[Sync]
    AnyPhase -. recurring lesson .-> CL[Capture-Learning]
    AnyPhase -. contract/doc touch .-> DR[Docs-Routing]
    AnyPhase -. activity switch .-> CH[Context-Hygiene]
    AnyPhase -. failed verify .-> RB[Rollback]
    AnyPhase -. production incident .-> HF[Hotfix]
```

## 7. Spec-Driven Development Sequence

The five-step rule from spec section 8, rendered as a sequence to highlight the spec-first ordering.

```mermaid
sequenceDiagram
    autonumber
    participant Author as Change Author
    participant Spec as Governing Spec
    participant Code as Implementation
    participant Verify as Verification

    Author->>Spec: 1. Identify behavior being changed
    Author->>Spec: 2. Identify the owning spec artifact
    Author->>Spec: 3. Update or add the spec FIRST
    Spec-->>Code: 4. Implement smallest change satisfying the updated spec
    Code->>Verify: 5. Verify executable and published specs remain aligned
    Verify-->>Author: pass / fail
```

## 8. Conformance Levels

Section 11 of the spec defines four conformance levels. Visualized as a maturity ladder.

```mermaid
flowchart LR
    L1["L1 — Phases identified, owners named"] --> L2["L2 — Phase Activities named, switches explicit"]
    L2 --> L3["L3 — Loops named, gates mechanical where possible"]
    L3 --> L4["L4 — Cross-cutting triggers automated, learnings closed back to roadmap"]
```

## Cross-References

- `docs/specs/application-lifecycle-spec.md` — the normative spec these diagrams accompany
- `docs/specs/lifecycle-phase-activities.md` — the repo-specific instantiation of the activity vocabulary
