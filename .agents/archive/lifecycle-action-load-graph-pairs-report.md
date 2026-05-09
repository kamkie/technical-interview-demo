# Lifecycle Action Load Graph Pairs Report

This report maps each user-initiated phase-action occurrence from `docs/specs/application-lifecycle-spec.md` and `docs/specs/lifecycle-phase-activities.md` to document-load graphs:

- `Full`: all mandatory and optional load edges for the action.
- `Deduplicated`: shown only when removing edges whose target document was already loaded earlier in that action graph changes the visible graph.

The report is descriptive. `AGENTS.md` and the focused owner guides remain authoritative.

The graphs show full document and file-family names directly.
Every action includes mandatory `AGENTS.md`.
Solid edges marked `M` are mandatory. Dashed edges marked `O` are optional or conditional.
Alternative nodes such as `docs/DESIGN.md or task-specific governing spec or published contract artifact` count as one selected load slot in the metrics because a single execution selects one of the alternatives.
Task-specific file-family names count as one abstract load slot even when a real task touches several concrete files.
The action node names the user-initiated action only; it is not a loaded document and does not contribute to chain depth. `AGENTS.md` is the mandatory first loaded document and primary routing source. Owner guides may route to deeper documents when they are the artifact that names the dependency. Chain depth is the longest acyclic document-to-document load depth; if document A loads documents B and C directly, depth is `1`. Loop-back edges are shown but excluded from depth counting.

This report shows only the full graph for each action because all current full and deduplicated action graphs resolve to the same document-load targets. If a future action has a real deduplicated difference, include both graphs for that action.

## Stats

| Statistic | Value |
| --- | ---: |
| User-initiated action occurrences | 63 |
| Actions with identical full and deduplicated graph targets | 63 |
| Actions shown as full-only graphs | 63 |
| Actions showing both full and deduplicated graphs | 0 |
| Chain depth range | 1-2 |
| Chain length range | 2-8 |
| Total loaded slots across all actions | 292 |
| Distinct loaded slots across all actions | 292 |
| Chain length distribution | 2: 3; 3: 10; 4: 17; 5: 20; 6: 6; 7: 4; 8: 3 |

## Prompt-Triggered Action Chains

These charts show common chained lifecycle actions for concrete prompts. Each action node uses the per-action document-load graph defined later in this report. Conditional branches are dashed and are not unrolled indefinitely.

| Prompt | Primary actions | Primary loaded slots | Primary distinct documents | Loaded slots with conditionals once | Distinct documents with conditionals |
| --- | ---: | ---: | ---: | ---: | ---: |
| `create plan PLAN_book_publisher.md to add publisher field to Book entity` | 6 | 35 | 18 | 46 | 20 |
| `implement plan PLAN_book_publisher.md` | 8 | 43 | 23 | 53 | 26 |
| `integrate changes and prepare release and publish it` | 8 | 44 | 18 | 49 | 20 |

### Prompt Chain / Create Plan

Metrics: primary action chain length 6; action nodes shown 7; conditional action nodes 1; primary loaded slots 35; distinct documents 18; loaded slots with conditionals once 46; distinct documents with conditionals 20.

```mermaid
flowchart TD
    PromptPlan["Prompt: create plan PLAN_book_publisher.md to add publisher field to Book entity"] --> PlanFrame["Planning / Frame"]
    PlanFrame --> PlanDesign["Planning / Design"]
    PlanDesign --> PlanSpec["Planning / Spec"]
    PlanSpec --> PlanDecompose["Planning / Decompose"]
    PlanDecompose --> PlanValidate["Planning / Validate-Plan"]
    PlanValidate --> PlanSync["Planning / Sync"]
    PlanValidate -. missing decision or open question .-> PlanReplan["Planning / Replan?"]
    PlanReplan -. revised plan .-> PlanValidate
```

### Prompt Chain / Implement Plan

Metrics: primary action chain length 8; action nodes shown 10; conditional action nodes 2; primary loaded slots 43; distinct documents 23; loaded slots with conditionals once 53; distinct documents with conditionals 26.

```mermaid
flowchart TD
    PromptImplement["Prompt: implement plan PLAN_book_publisher.md"] --> ImplSpec["Implementation / Spec"]
    ImplSpec --> ImplCode["Implementation / Code"]
    ImplCode --> ImplDocs["Implementation / Docs"]
    ImplDocs --> ImplRun["Implementation / Run"]
    ImplRun --> ImplSelfReview["Implementation / Self-Review"]
    ImplSelfReview --> ImplCodeReview["Implementation / Code Review"]
    ImplCodeReview --> ImplCommit["Implementation / Commit"]
    ImplCommit --> ImplHandoff["Implementation / Handoff"]
    ImplRun -. validation failure .-> ImplRun
    ImplRun -. scope gap or contradicted decision .-> ImplReplan["Implementation / Replan?"]
    ImplReplan -. plan updated .-> ImplSpec
    ImplCodeReview -. security-sensitive change .-> ImplSecurity["Implementation / Security Review?"]
    ImplSecurity --> ImplCommit
    ImplCodeReview -. changes requested .-> ImplCode
```

### Prompt Chain / Integrate And Release

Metrics: primary action chain length 8; action nodes shown 9; conditional action nodes 1; primary loaded slots 44; distinct documents 18; loaded slots with conditionals once 49; distinct documents with conditionals 20.

```mermaid
flowchart TD
    PromptRelease["Prompt: integrate changes and prepare release and publish it"] --> IntValidate["Integration / Re-validate"]
    IntValidate --> IntMerge["Integration / Merge"]
    IntValidate -. conflicts found .-> IntConflicts["Integration / Resolve-Conflicts?"]
    IntConflicts -. resolved .-> IntMerge
    IntMerge --> IntPostMerge["Integration / Post-Merge-Verify"]
    IntPostMerge --> RelGate["Release / Gate"]
    RelGate --> RelTag["Release / Tag"]
    RelTag --> RelNotes["Release / Notes"]
    RelNotes --> RelPublish["Release / Publish"]
    RelPublish --> RelCleanup["Release / Post-Release-Cleanup"]
    IntPostMerge -. verification failure .-> IntValidate
    RelGate -. precondition fails .-> IntValidate
```

## Prompt-Triggered Document Load Chains

These charts show primary-path document loads for the same prompts. Repeated document labels are intentional: each lifecycle action starts from `AGENTS.md` again, then routes to the documents for that action. Dashed document edges are optional or conditional loads.

### Document Load Chain / Create Plan

Metrics: document chain depth 1; action chain length 6; total loaded slots 35; mandatory loaded slots 18; optional loaded slots 17; distinct documents 18.

```mermaid
flowchart TD
    CreatePrompt["Prompt: create plan PLAN_book_publisher.md to add publisher field to Book entity"] --> CPF["Planning / Frame"]
    CPF --> CPD["Planning / Design"]
    CPD --> CPS["Planning / Spec"]
    CPS --> CPDec["Planning / Decompose"]
    CPDec --> CPV["Planning / Validate-Plan"]
    CPV --> CPSync["Planning / Sync"]

    CPF -->|M| CPF_AGENTS["AGENTS.md"]
    CPF_AGENTS -->|M| CPF_Planning[".agents/references/planning.md"]
    CPF_AGENTS -->|M| CPF_Roadmap["ROADMAP.md"]
    CPF_AGENTS -. O .-> CPF_Design["docs/DESIGN.md"]
    CPF_AGENTS -. O .-> CPF_TaskSpec["task-specific governing spec or published contract artifact"]
    CPF_AGENTS -. O .-> CPF_Reference["referenced ticket, pull request, example, document, or web page"]
    CPF_AGENTS -. O .-> CPF_LifecycleSpec["docs/specs/application-lifecycle-spec.md"]
    CPF_AGENTS -. O .-> CPF_PhaseActivities["docs/specs/lifecycle-phase-activities.md"]

    CPD -->|M| CPD_AGENTS["AGENTS.md"]
    CPD_AGENTS -->|M| CPD_Planning[".agents/references/planning.md"]
    CPD_AGENTS -->|M| CPD_DesignOrSpec["docs/DESIGN.md or task-specific governing spec or published contract artifact"]
    CPD_AGENTS -. O .-> CPD_Documentation[".agents/references/documentation.md"]
    CPD_AGENTS -. O .-> CPD_Readme["README.md"]
    CPD_AGENTS -. O .-> CPD_Asciidoc["src/docs/asciidoc/"]

    CPS -->|M| CPS_AGENTS["AGENTS.md"]
    CPS_AGENTS -->|M| CPS_Planning[".agents/references/planning.md"]
    CPS_AGENTS -->|M| CPS_TaskSpec["task-specific governing spec or published contract artifact"]
    CPS_AGENTS -. O .-> CPS_Documentation[".agents/references/documentation.md"]
    CPS_AGENTS -. O .-> CPS_OpenApi["src/test/resources/openapi/approved-openapi.json"]
    CPS_AGENTS -. O .-> CPS_Readme["README.md"]
    CPS_AGENTS -. O .-> CPS_Asciidoc["src/docs/asciidoc/"]

    CPDec -->|M| CPDec_AGENTS["AGENTS.md"]
    CPDec_AGENTS -->|M| CPDec_Planning[".agents/references/planning.md"]
    CPDec_AGENTS -->|M| CPDec_Template[".agents/templates/plan-template.md"]
    CPDec_AGENTS -. O .-> CPDec_Workflow[".agents/references/workflow.md"]
    CPDec_AGENTS -. O .-> CPDec_Authoring[".agents/references/plan-authoring-guide.md"]

    CPV -->|M| CPV_AGENTS["AGENTS.md"]
    CPV_AGENTS -->|M| CPV_Planning[".agents/references/planning.md"]
    CPV_AGENTS -->|M| CPV_Plan[".agents/plans/PLAN_book_publisher.md"]
    CPV_AGENTS -. O .-> CPV_Testing[".agents/references/testing.md"]
    CPV_AGENTS -. O .-> CPV_Documentation[".agents/references/documentation.md"]
    CPV_AGENTS -. O .-> CPV_Workflow[".agents/references/workflow.md"]

    CPSync -->|M| CPSync_AGENTS["AGENTS.md"]
    CPSync_AGENTS -->|M| CPSync_Roadmap["ROADMAP.md"]
    CPSync_AGENTS -->|M| CPSync_Plan[".agents/plans/PLAN_book_publisher.md"]
```

### Document Load Chain / Implement Plan

Metrics: document chain depth 2; action chain length 8; total loaded slots 43; mandatory loaded slots 22; optional loaded slots 21; distinct documents 23.

```mermaid
flowchart TD
    ImplementPrompt["Prompt: implement plan PLAN_book_publisher.md"] --> ISpec["Implementation / Spec"]
    ISpec --> ICode["Implementation / Code"]
    ICode --> IDocs["Implementation / Docs"]
    IDocs --> IRun["Implementation / Run"]
    IRun --> ISelfReview["Implementation / Self-Review"]
    ISelfReview --> ICodeReview["Implementation / Code Review"]
    ICodeReview --> ICommit["Implementation / Commit"]
    ICommit --> IHandoff["Implementation / Handoff"]

    ISpec -->|M| ISpec_AGENTS["AGENTS.md"]
    ISpec_AGENTS -->|M| ISpec_Execution[".agents/references/execution.md"]
    ISpec_AGENTS -->|M| ISpec_TaskSpec["task-specific governing spec or published contract artifact"]
    ISpec_AGENTS -. O .-> ISpec_Documentation[".agents/references/documentation.md"]

    ICode -->|M| ICode_AGENTS["AGENTS.md"]
    ICode_AGENTS -->|M| ICode_Execution[".agents/references/execution.md"]
    ICode_AGENTS -->|M| ICode_Style[".agents/references/code-style.md"]
    ICode_AGENTS -->|M| ICode_Source["task-specific source files"]
    ICode_AGENTS -. O .-> ICode_Architecture["docs/ARCHITECTURE.md"]
    ICode_AGENTS -. O .-> ICode_Design["docs/DESIGN.md"]
    ICode_AGENTS -. O .-> ICode_TaskSpec["task-specific governing spec or published contract artifact"]

    IDocs -->|M| IDocs_AGENTS["AGENTS.md"]
    IDocs_AGENTS -->|M| IDocs_Documentation[".agents/references/documentation.md"]
    IDocs_AGENTS -->|M| IDocs_ChangedDocs["changed documentation or contract files"]
    IDocs_AGENTS -. O .-> IDocs_Rules[".agents/references/references-rules.md"]
    IDocs_AGENTS -. O .-> IDocs_Readme["README.md"]
    IDocs_AGENTS -. O .-> IDocs_Asciidoc["src/docs/asciidoc/"]
    IDocs_AGENTS -. O .-> IDocs_FrontendContract["docs/FRONTEND_AI_CONTRACT.md"]
    IDocs_AGENTS -. O .-> IDocs_OpenApi["src/test/resources/openapi/approved-openapi.json"]

    IRun -->|M| IRun_AGENTS["AGENTS.md"]
    IRun_AGENTS -->|M| IRun_Testing[".agents/references/testing.md"]
    IRun_AGENTS -->|M| IRun_Environment[".agents/references/environment-quick-ref.md"]
    IRun_AGENTS -. O .-> IRun_GradleGraph[".agents/references/gradle-task-graph.md"]
    IRun_AGENTS -. O .-> IRun_Troubleshooting[".agents/references/troubleshooting.md"]

    ISelfReview -->|M| ISelfReview_AGENTS["AGENTS.md"]
    ISelfReview_AGENTS -->|M| ISelfReview_Reviews[".agents/references/reviews.md"]
    ISelfReview_AGENTS -. O .-> ISelfReview_Testing[".agents/references/testing.md"]
    ISelfReview_AGENTS -. O .-> ISelfReview_Documentation[".agents/references/documentation.md"]

    ICodeReview -->|M| ICodeReview_AGENTS["AGENTS.md"]
    ICodeReview_AGENTS -->|M| ICodeReview_Reviews[".agents/references/reviews.md"]
    ICodeReview_AGENTS -. O .-> ICodeReview_TaskSpec["task-specific governing spec or published contract artifact"]
    ICodeReview_AGENTS -. O .-> ICodeReview_Source["task-specific source files"]

    ICommit -->|M| ICommit_AGENTS["AGENTS.md"]
    ICommit_AGENTS -->|M| ICommit_Execution[".agents/references/execution.md"]
    ICommit_Execution -->|M| ICommit_GitMessage[".gitmessage"]
    ICommit_AGENTS -. O .-> ICommit_Plan[".agents/plans/PLAN_book_publisher.md"]
    ICommit_AGENTS -. O .-> ICommit_WorkflowLog[".agents/tmp/workflow/*.md"]
    ICommit_AGENTS -. O .-> ICommit_Workflow[".agents/references/workflow.md"]

    IHandoff -->|M| IHandoff_AGENTS["AGENTS.md"]
    IHandoff_AGENTS -->|M| IHandoff_Execution[".agents/references/execution.md"]
    IHandoff_AGENTS -. O .-> IHandoff_Workflow[".agents/references/workflow.md"]
    IHandoff_AGENTS -. O .-> IHandoff_Plan[".agents/plans/PLAN_book_publisher.md"]
    IHandoff_AGENTS -. O .-> IHandoff_WorkflowLog[".agents/tmp/workflow/*.md"]
```

### Document Load Chain / Integrate And Release

Metrics: document chain depth 1; action chain length 8; total loaded slots 44; mandatory loaded slots 28; optional loaded slots 16; distinct documents 18.

```mermaid
flowchart TD
    ReleasePrompt["Prompt: integrate changes and prepare release and publish it"] --> IRV["Integration / Re-validate"]
    IRV --> IMerge["Integration / Merge"]
    IMerge --> IPMV["Integration / Post-Merge-Verify"]
    IPMV --> RGate["Release / Gate"]
    RGate --> RTag["Release / Tag"]
    RTag --> RNotes["Release / Notes"]
    RNotes --> RPublish["Release / Publish"]
    RPublish --> RCleanup["Release / Post-Release-Cleanup"]

    IRV -->|M| IRV_AGENTS["AGENTS.md"]
    IRV_AGENTS -->|M| IRV_Testing[".agents/references/testing.md"]
    IRV_AGENTS -->|M| IRV_Environment[".agents/references/environment-quick-ref.md"]
    IRV_AGENTS -. O .-> IRV_Workflow[".agents/references/workflow.md"]
    IRV_AGENTS -. O .-> IRV_GradleGraph[".agents/references/gradle-task-graph.md"]

    IMerge -->|M| IMerge_AGENTS["AGENTS.md"]
    IMerge_AGENTS -->|M| IMerge_Workflow[".agents/references/workflow.md"]
    IMerge_AGENTS -. O .-> IMerge_Plan[".agents/plans/PLAN_book_publisher.md"]
    IMerge_AGENTS -. O .-> IMerge_WorkflowLog[".agents/tmp/workflow/*.md"]

    IPMV -->|M| IPMV_AGENTS["AGENTS.md"]
    IPMV_AGENTS -->|M| IPMV_Testing[".agents/references/testing.md"]
    IPMV_AGENTS -->|M| IPMV_Workflow[".agents/references/workflow.md"]
    IPMV_AGENTS -. O .-> IPMV_Execution[".agents/references/execution.md"]
    IPMV_AGENTS -. O .-> IPMV_PlanExecution[".agents/references/plan-execution.md"]

    RGate -->|M| RGate_AGENTS["AGENTS.md"]
    RGate_AGENTS -->|M| RGate_Releases[".agents/references/releases.md"]
    RGate_AGENTS -->|M| RGate_Testing[".agents/references/testing.md"]
    RGate_AGENTS -->|M| RGate_Documentation[".agents/references/documentation.md"]
    RGate_AGENTS -. O .-> RGate_Plan[".agents/plans/PLAN_book_publisher.md"]
    RGate_AGENTS -. O .-> RGate_Roadmap["ROADMAP.md"]
    RGate_AGENTS -. O .-> RGate_Changelog["CHANGELOG.md"]

    RTag -->|M| RTag_AGENTS["AGENTS.md"]
    RTag_AGENTS -->|M| RTag_Releases[".agents/references/releases.md"]
    RTag_AGENTS -->|M| RTag_Checklist[".agents/references/release-checklist.md"]
    RTag_AGENTS -->|M| RTag_Changelog["CHANGELOG.md"]
    RTag_AGENTS -->|M| RTag_Roadmap["ROADMAP.md"]
    RTag_AGENTS -. O .-> RTag_Learnings[".agents/references/LEARNINGS.md"]
    RTag_AGENTS -. O .-> RTag_Archive[".agents/archive/"]

    RNotes -->|M| RNotes_AGENTS["AGENTS.md"]
    RNotes_AGENTS -->|M| RNotes_Releases[".agents/references/releases.md"]
    RNotes_AGENTS -->|M| RNotes_Changelog["CHANGELOG.md"]
    RNotes_AGENTS -. O .-> RNotes_Checklist[".agents/references/release-checklist.md"]
    RNotes_AGENTS -. O .-> RNotes_TemporaryChangelog["temporary CHANGELOG_<topic>.md files"]

    RPublish -->|M| RPublish_AGENTS["AGENTS.md"]
    RPublish_AGENTS -->|M| RPublish_Releases[".agents/references/releases.md"]
    RPublish_AGENTS -. O .-> RPublish_ArtifactVerification[".agents/references/release-artifact-verification.md"]

    RCleanup -->|M| RCleanup_AGENTS["AGENTS.md"]
    RCleanup_AGENTS -->|M| RCleanup_Releases[".agents/references/releases.md"]
    RCleanup_AGENTS -->|M| RCleanup_Checklist[".agents/references/release-checklist.md"]
    RCleanup_AGENTS -->|M| RCleanup_Roadmap["ROADMAP.md"]
    RCleanup_AGENTS -->|M| RCleanup_Changelog["CHANGELOG.md"]
    RCleanup_AGENTS -->|M| RCleanup_Archive[".agents/archive/"]
    RCleanup_AGENTS -. O .-> RCleanup_Learnings[".agents/references/LEARNINGS.md"]
    RCleanup_AGENTS -. O .-> RCleanup_Workflow[".agents/references/workflow.md"]
```

## Prompt-Triggered Document-Only Load Graphs

These charts collapse the prompt document-load chains to distinct documents and file families only. They intentionally omit prompt and lifecycle action nodes, so repeated loads across actions are represented once. `M/O` means a document is mandatory in at least one source action and optional in at least one other source action.

### Document-Only Load Graph / Create Plan

Metrics: document-only chain depth 1; distinct documents shown 18; total loaded slots represented 35; mandatory or mixed edges 6; optional-only edges 11.

```mermaid
flowchart TD
    DOCP_AGENTS["AGENTS.md"]
    DOCP_AGENTS -->|M| DOCP_Planning[".agents/references/planning.md"]
    DOCP_AGENTS -->|M| DOCP_Roadmap["ROADMAP.md"]
    DOCP_AGENTS -->|M/O| DOCP_TaskSpec["task-specific governing spec or published contract artifact"]
    DOCP_AGENTS -->|M| DOCP_DesignOrSpec["docs/DESIGN.md or task-specific governing spec or published contract artifact"]
    DOCP_AGENTS -->|M| DOCP_Template[".agents/templates/plan-template.md"]
    DOCP_AGENTS -->|M| DOCP_Plan[".agents/plans/PLAN_book_publisher.md"]
    DOCP_AGENTS -. O .-> DOCP_Design["docs/DESIGN.md"]
    DOCP_AGENTS -. O .-> DOCP_Reference["referenced ticket, pull request, example, document, or web page"]
    DOCP_AGENTS -. O .-> DOCP_LifecycleSpec["docs/specs/application-lifecycle-spec.md"]
    DOCP_AGENTS -. O .-> DOCP_PhaseActivities["docs/specs/lifecycle-phase-activities.md"]
    DOCP_AGENTS -. O .-> DOCP_Documentation[".agents/references/documentation.md"]
    DOCP_AGENTS -. O .-> DOCP_Readme["README.md"]
    DOCP_AGENTS -. O .-> DOCP_Asciidoc["src/docs/asciidoc/"]
    DOCP_AGENTS -. O .-> DOCP_OpenApi["src/test/resources/openapi/approved-openapi.json"]
    DOCP_AGENTS -. O .-> DOCP_Workflow[".agents/references/workflow.md"]
    DOCP_AGENTS -. O .-> DOCP_Authoring[".agents/references/plan-authoring-guide.md"]
    DOCP_AGENTS -. O .-> DOCP_Testing[".agents/references/testing.md"]
```

### Document-Only Load Graph / Implement Plan

Metrics: document-only chain depth 2; distinct documents shown 23; total loaded slots represented 43; mandatory or mixed edges 10; optional-only edges 12.

```mermaid
flowchart TD
    DOIP_AGENTS["AGENTS.md"]
    DOIP_AGENTS -->|M| DOIP_Execution[".agents/references/execution.md"]
    DOIP_AGENTS -->|M/O| DOIP_TaskSpec["task-specific governing spec or published contract artifact"]
    DOIP_AGENTS -->|M/O| DOIP_Documentation[".agents/references/documentation.md"]
    DOIP_AGENTS -->|M| DOIP_CodeStyle[".agents/references/code-style.md"]
    DOIP_AGENTS -->|M/O| DOIP_Source["task-specific source files"]
    DOIP_AGENTS -->|M| DOIP_ChangedDocs["changed documentation or contract files"]
    DOIP_AGENTS -->|M/O| DOIP_Testing[".agents/references/testing.md"]
    DOIP_AGENTS -->|M| DOIP_Environment[".agents/references/environment-quick-ref.md"]
    DOIP_AGENTS -->|M| DOIP_Reviews[".agents/references/reviews.md"]
    DOIP_Execution -->|M| DOIP_GitMessage[".gitmessage"]
    DOIP_AGENTS -. O .-> DOIP_Architecture["docs/ARCHITECTURE.md"]
    DOIP_AGENTS -. O .-> DOIP_Design["docs/DESIGN.md"]
    DOIP_AGENTS -. O .-> DOIP_Rules[".agents/references/references-rules.md"]
    DOIP_AGENTS -. O .-> DOIP_Readme["README.md"]
    DOIP_AGENTS -. O .-> DOIP_Asciidoc["src/docs/asciidoc/"]
    DOIP_AGENTS -. O .-> DOIP_FrontendContract["docs/FRONTEND_AI_CONTRACT.md"]
    DOIP_AGENTS -. O .-> DOIP_OpenApi["src/test/resources/openapi/approved-openapi.json"]
    DOIP_AGENTS -. O .-> DOIP_GradleGraph[".agents/references/gradle-task-graph.md"]
    DOIP_AGENTS -. O .-> DOIP_Troubleshooting[".agents/references/troubleshooting.md"]
    DOIP_AGENTS -. O .-> DOIP_Plan[".agents/plans/PLAN_book_publisher.md"]
    DOIP_AGENTS -. O .-> DOIP_WorkflowLog[".agents/tmp/workflow/*.md"]
    DOIP_AGENTS -. O .-> DOIP_Workflow[".agents/references/workflow.md"]
```

### Document-Only Load Graph / Integrate And Release

Metrics: document-only chain depth 1; distinct documents shown 18; total loaded slots represented 44; mandatory or mixed edges 9; optional-only edges 8.

```mermaid
flowchart TD
    DOIR_AGENTS["AGENTS.md"]
    DOIR_AGENTS -->|M/O| DOIR_Testing[".agents/references/testing.md"]
    DOIR_AGENTS -->|M| DOIR_Environment[".agents/references/environment-quick-ref.md"]
    DOIR_AGENTS -->|M/O| DOIR_Workflow[".agents/references/workflow.md"]
    DOIR_AGENTS -->|M| DOIR_Releases[".agents/references/releases.md"]
    DOIR_AGENTS -->|M| DOIR_Documentation[".agents/references/documentation.md"]
    DOIR_AGENTS -->|M/O| DOIR_Roadmap["ROADMAP.md"]
    DOIR_AGENTS -->|M/O| DOIR_Changelog["CHANGELOG.md"]
    DOIR_AGENTS -->|M/O| DOIR_Checklist[".agents/references/release-checklist.md"]
    DOIR_AGENTS -->|M/O| DOIR_Archive[".agents/archive/"]
    DOIR_AGENTS -. O .-> DOIR_GradleGraph[".agents/references/gradle-task-graph.md"]
    DOIR_AGENTS -. O .-> DOIR_Plan[".agents/plans/PLAN_book_publisher.md"]
    DOIR_AGENTS -. O .-> DOIR_WorkflowLog[".agents/tmp/workflow/*.md"]
    DOIR_AGENTS -. O .-> DOIR_Execution[".agents/references/execution.md"]
    DOIR_AGENTS -. O .-> DOIR_PlanExecution[".agents/references/plan-execution.md"]
    DOIR_AGENTS -. O .-> DOIR_Learnings[".agents/references/LEARNINGS.md"]
    DOIR_AGENTS -. O .-> DOIR_TemporaryChangelog["temporary CHANGELOG_<topic>.md files"]
    DOIR_AGENTS -. O .-> DOIR_ArtifactVerification[".agents/references/release-artifact-verification.md"]
```

## Discovery

### Discovery / Scan

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Discovery / Scan"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| docs_ARCHITECTURE_md_full["docs/ARCHITECTURE.md"]
    AGENTS_md_full -. O .-> agents_references_LEARNINGS_md_full[".agents/references/LEARNINGS.md"]
    AGENTS_md_full -. O .-> task_specific_source_files_full["task-specific source files"]
```

### Discovery / Frame

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Discovery / Frame"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_planning_md_full[".agents/references/planning.md"]
    AGENTS_md_full -. O .-> agents_references_execution_md_full[".agents/references/execution.md"]
    AGENTS_md_full -. O .-> ROADMAP_md_full["ROADMAP.md"]
```

### Discovery / Clarify?

Metrics: chain depth 1; chain length 2; total loaded 2; deduplicated chain length 2; distinct loaded 2.

```mermaid
flowchart TD
    FA["Discovery / Clarify?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_planning_md_full[".agents/references/planning.md"]
    agents_references_planning_md_full -. loop after answer .-> FA
```

### Discovery / Capture?

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Discovery / Capture?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_LEARNINGS_md_full[".agents/references/LEARNINGS.md"]
    AGENTS_md_full -. O .-> focused_owner_guide_for_a_durable_correction_full["focused owner guide for a durable correction"]
    AGENTS_md_full -. O .-> agents_references_references_rules_md_full[".agents/references/references-rules.md"]
```

## Roadmap Intake

### Roadmap Intake / Intake

Metrics: chain depth 1; chain length 3; total loaded 3; deduplicated chain length 3; distinct loaded 3.

```mermaid
flowchart TD
    FA["Roadmap Intake / Intake"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -. O .-> agents_references_planning_md_full[".agents/references/planning.md"]
```

### Roadmap Intake / Refine

Metrics: chain depth 1; chain length 3; total loaded 3; deduplicated chain length 3; distinct loaded 3.

```mermaid
flowchart TD
    FA["Roadmap Intake / Refine"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -. O .-> docs_DESIGN_md_full["docs/DESIGN.md"]
```

### Roadmap Intake / Prioritize

Metrics: chain depth 1; chain length 3; total loaded 3; deduplicated chain length 3; distinct loaded 3.

```mermaid
flowchart TD
    FA["Roadmap Intake / Prioritize"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -. O .-> docs_DESIGN_md_full["docs/DESIGN.md"]
```

### Roadmap Intake / Sequence

Metrics: chain depth 1; chain length 3; total loaded 3; deduplicated chain length 3; distinct loaded 3.

```mermaid
flowchart TD
    FA["Roadmap Intake / Sequence"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -. O .-> concrete_agents_plans_PLAN_md_full["concrete .agents/plans/PLAN_*.md"]
```

### Roadmap Intake / Sync

Metrics: chain depth 1; chain length 3; total loaded 3; deduplicated chain length 3; distinct loaded 3.

```mermaid
flowchart TD
    FA["Roadmap Intake / Sync"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -. O .-> concrete_agents_plans_PLAN_md_full["concrete .agents/plans/PLAN_*.md"]
```

## Planning

### Planning / Frame

Metrics: chain depth 1; chain length 8; total loaded 8; deduplicated chain length 8; distinct loaded 8.

```mermaid
flowchart TD
    FA["Planning / Frame"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_planning_md_full[".agents/references/planning.md"]
    AGENTS_md_full -->|M| ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -. O .-> docs_DESIGN_md_full["docs/DESIGN.md"]
    AGENTS_md_full -. O .-> task_specific_governing_spec_or_published_contract_artifact_full["task-specific governing spec or published contract artifact"]
    AGENTS_md_full -. O .-> referenced_ticket_pull_request_example_document_or_web_page_full["referenced ticket, pull request, example, document, or web page"]
    AGENTS_md_full -. O .-> docs_specs_application_lifecycle_spec_md_full["docs/specs/application-lifecycle-spec.md"]
    AGENTS_md_full -. O .-> docs_specs_lifecycle_phase_activities_md_full["docs/specs/lifecycle-phase-activities.md"]
```

### Planning / Design

Metrics: chain depth 1; chain length 6; total loaded 6; deduplicated chain length 6; distinct loaded 6.

```mermaid
flowchart TD
    FA["Planning / Design"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_planning_md_full[".agents/references/planning.md"]
    AGENTS_md_full -->|M| AltA["docs/DESIGN.md or task-specific governing spec or published contract artifact"]
    AGENTS_md_full -. O .-> agents_references_documentation_md_full[".agents/references/documentation.md"]
    AGENTS_md_full -. O .-> README_md_full["README.md"]
    AGENTS_md_full -. O .-> src_docs_asciidoc_full["src/docs/asciidoc/"]
```

### Planning / Spec

Metrics: chain depth 1; chain length 7; total loaded 7; deduplicated chain length 7; distinct loaded 7.

```mermaid
flowchart TD
    FA["Planning / Spec"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_planning_md_full[".agents/references/planning.md"]
    AGENTS_md_full -->|M| task_specific_governing_spec_or_published_contract_artifact_full["task-specific governing spec or published contract artifact"]
    AGENTS_md_full -. O .-> agents_references_documentation_md_full[".agents/references/documentation.md"]
    AGENTS_md_full -. O .-> src_test_resources_openapi_approved_openapi_json_full["src/test/resources/openapi/approved-openapi.json"]
    AGENTS_md_full -. O .-> README_md_full["README.md"]
    AGENTS_md_full -. O .-> src_docs_asciidoc_full["src/docs/asciidoc/"]
```

### Planning / Decompose

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Planning / Decompose"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_planning_md_full[".agents/references/planning.md"]
    AGENTS_md_full -->|M| agents_templates_plan_template_md_full[".agents/templates/plan-template.md"]
    AGENTS_md_full -. O .-> agents_references_workflow_md_full[".agents/references/workflow.md"]
    AGENTS_md_full -. O .-> agents_references_plan_authoring_guide_md_full[".agents/references/plan-authoring-guide.md"]
```

### Planning / Validate-Plan

Metrics: chain depth 1; chain length 6; total loaded 6; deduplicated chain length 6; distinct loaded 6.

```mermaid
flowchart TD
    FA["Planning / Validate-Plan"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_planning_md_full[".agents/references/planning.md"]
    AGENTS_md_full -->|M| concrete_agents_plans_PLAN_md_full["concrete .agents/plans/PLAN_*.md"]
    AGENTS_md_full -. O .-> agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -. O .-> agents_references_documentation_md_full[".agents/references/documentation.md"]
    AGENTS_md_full -. O .-> agents_references_workflow_md_full[".agents/references/workflow.md"]
```

### Planning / Sync

Metrics: chain depth 1; chain length 3; total loaded 3; deduplicated chain length 3; distinct loaded 3.

```mermaid
flowchart TD
    FA["Planning / Sync"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -->|M| concrete_agents_plans_PLAN_md_full["concrete .agents/plans/PLAN_*.md"]
```

### Planning / Replan?

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Planning / Replan?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_planning_md_full[".agents/references/planning.md"]
    AGENTS_md_full -->|M| concrete_agents_plans_PLAN_md_full["concrete .agents/plans/PLAN_*.md"]
    AGENTS_md_full -. O .-> agents_references_execution_md_full[".agents/references/execution.md"]
    AGENTS_md_full -. O .-> agents_references_plan_execution_md_full[".agents/references/plan-execution.md"]
    agents_references_planning_md_full -. loop .-> FA
```

## Implementation

### Implementation / Spec

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Implementation / Spec"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_execution_md_full[".agents/references/execution.md"]
    AGENTS_md_full -->|M| task_specific_governing_spec_or_published_contract_artifact_full["task-specific governing spec or published contract artifact"]
    AGENTS_md_full -. O .-> agents_references_documentation_md_full[".agents/references/documentation.md"]
```

### Implementation / Code

Metrics: chain depth 1; chain length 7; total loaded 7; deduplicated chain length 7; distinct loaded 7.

```mermaid
flowchart TD
    FA["Implementation / Code"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_execution_md_full[".agents/references/execution.md"]
    AGENTS_md_full -->|M| agents_references_code_style_md_full[".agents/references/code-style.md"]
    AGENTS_md_full -->|M| task_specific_source_files_full["task-specific source files"]
    AGENTS_md_full -. O .-> docs_ARCHITECTURE_md_full["docs/ARCHITECTURE.md"]
    AGENTS_md_full -. O .-> docs_DESIGN_md_full["docs/DESIGN.md"]
    AGENTS_md_full -. O .-> task_specific_governing_spec_or_published_contract_artifact_full["task-specific governing spec or published contract artifact"]
```

### Implementation / Docs

Metrics: chain depth 1; chain length 8; total loaded 8; deduplicated chain length 8; distinct loaded 8.

```mermaid
flowchart TD
    FA["Implementation / Docs"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_documentation_md_full[".agents/references/documentation.md"]
    AGENTS_md_full -->|M| changed_documentation_or_contract_files_full["changed documentation or contract files"]
    AGENTS_md_full -. O .-> agents_references_references_rules_md_full[".agents/references/references-rules.md"]
    AGENTS_md_full -. O .-> README_md_full["README.md"]
    AGENTS_md_full -. O .-> src_docs_asciidoc_full["src/docs/asciidoc/"]
    AGENTS_md_full -. O .-> docs_FRONTEND_AI_CONTRACT_md_full["docs/FRONTEND_AI_CONTRACT.md"]
    AGENTS_md_full -. O .-> src_test_resources_openapi_approved_openapi_json_full["src/test/resources/openapi/approved-openapi.json"]
```

### Implementation / Run

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Implementation / Run"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -->|M| agents_references_environment_quick_ref_md_full[".agents/references/environment-quick-ref.md"]
    AGENTS_md_full -. O .-> agents_references_gradle_task_graph_md_full[".agents/references/gradle-task-graph.md"]
    AGENTS_md_full -. O .-> agents_references_troubleshooting_md_full[".agents/references/troubleshooting.md"]
    agents_references_troubleshooting_md_full -. failure loop .-> FA
```

### Implementation / Replan?

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Implementation / Replan?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_planning_md_full[".agents/references/planning.md"]
    AGENTS_md_full -->|M| concrete_agents_plans_PLAN_md_full["concrete .agents/plans/PLAN_*.md"]
    AGENTS_md_full -. O .-> agents_references_plan_execution_md_full[".agents/references/plan-execution.md"]
    AGENTS_md_full -. O .-> agents_references_workflow_md_full[".agents/references/workflow.md"]
    agents_references_planning_md_full -. returns to plan loop .-> FA
```

### Implementation / Self-Review

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Implementation / Self-Review"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_reviews_md_full[".agents/references/reviews.md"]
    AGENTS_md_full -. O .-> agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -. O .-> agents_references_documentation_md_full[".agents/references/documentation.md"]
```

### Implementation / Code Review

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Implementation / Code Review"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_reviews_md_full[".agents/references/reviews.md"]
    AGENTS_md_full -. O .-> task_specific_governing_spec_or_published_contract_artifact_full["task-specific governing spec or published contract artifact"]
    AGENTS_md_full -. O .-> task_specific_source_files_full["task-specific source files"]
```

### Implementation / Security Review?

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Implementation / Security Review?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_reviews_md_full[".agents/references/reviews.md"]
    AGENTS_md_full -. O .-> security_sensitive_source_workflow_config_or_release_files_full["security-sensitive source, workflow, config, or release files"]
    AGENTS_md_full -. O .-> agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -. O .-> agents_references_documentation_md_full[".agents/references/documentation.md"]
```

### Implementation / Commit

Metrics: chain depth 2; chain length 6; total loaded 6; deduplicated chain length 6; distinct loaded 6.

```mermaid
flowchart TD
    FA["Implementation / Commit"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_execution_md_full[".agents/references/execution.md"]
    agents_references_execution_md_full -->|M| gitmessage_full[".gitmessage"]
    AGENTS_md_full -. O .-> concrete_agents_plans_PLAN_md_full["concrete .agents/plans/PLAN_*.md"]
    AGENTS_md_full -. O .-> agents_tmp_workflow_md_full[".agents/tmp/workflow/*.md"]
    AGENTS_md_full -. O .-> agents_references_workflow_md_full[".agents/references/workflow.md"]
```

### Implementation / Handoff

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Implementation / Handoff"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_execution_md_full[".agents/references/execution.md"]
    AGENTS_md_full -. O .-> agents_references_workflow_md_full[".agents/references/workflow.md"]
    AGENTS_md_full -. O .-> concrete_agents_plans_PLAN_md_full["concrete .agents/plans/PLAN_*.md"]
    AGENTS_md_full -. O .-> agents_tmp_workflow_md_full[".agents/tmp/workflow/*.md"]
```

## Testing

### Testing / Plan-Tests

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Testing / Plan-Tests"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -. O .-> agents_references_documentation_md_full[".agents/references/documentation.md"]
    AGENTS_md_full -. O .-> agents_references_gradle_task_graph_md_full[".agents/references/gradle-task-graph.md"]
```

### Testing / Author-Tests

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Testing / Author-Tests"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -->|M| task_specific_test_or_executable_spec_files_full["task-specific test or executable-spec files"]
    AGENTS_md_full -. O .-> agents_references_code_style_md_full[".agents/references/code-style.md"]
    AGENTS_md_full -. O .-> task_specific_source_files_full["task-specific source files"]
```

### Testing / Run

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Testing / Run"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -->|M| agents_references_environment_quick_ref_md_full[".agents/references/environment-quick-ref.md"]
    AGENTS_md_full -. O .-> agents_references_gradle_task_graph_md_full[".agents/references/gradle-task-graph.md"]
    AGENTS_md_full -. O .-> agents_references_troubleshooting_md_full[".agents/references/troubleshooting.md"]
    agents_references_troubleshooting_md_full -. failure .-> FA
```

### Testing / Diagnose?

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Testing / Diagnose?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_troubleshooting_md_full[".agents/references/troubleshooting.md"]
    AGENTS_md_full -. O .-> agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -. O .-> SETUP_md_full["SETUP.md"]
    AGENTS_md_full -. O .-> agents_references_LEARNINGS_md_full[".agents/references/LEARNINGS.md"]
```

### Testing / Fix?

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Testing / Fix?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_execution_md_full[".agents/references/execution.md"]
    AGENTS_md_full -->|M| AffectedA["task-specific source files / task-specific test or executable-spec files / task-specific governing spec or published contract artifact"]
    AGENTS_md_full -. O .-> agents_references_planning_md_full[".agents/references/planning.md"]
```

### Testing / Re-run

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Testing / Re-run"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -->|M| agents_references_environment_quick_ref_md_full[".agents/references/environment-quick-ref.md"]
    AGENTS_md_full -. O .-> agents_references_gradle_task_graph_md_full[".agents/references/gradle-task-graph.md"]
    agents_references_gradle_task_graph_md_full -. still fails .-> FA
```

### Testing / Record

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Testing / Record"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -->|M| PlanOrLogA["concrete .agents/plans/PLAN_*.md or .agents/tmp/workflow/*.md"]
    AGENTS_md_full -. O .-> agents_references_workflow_md_full[".agents/references/workflow.md"]
```

## Review

### Review / Self-Review

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Review / Self-Review"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_reviews_md_full[".agents/references/reviews.md"]
    AGENTS_md_full -. O .-> agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -. O .-> agents_references_documentation_md_full[".agents/references/documentation.md"]
```

### Review / Code Review

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Review / Code Review"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_reviews_md_full[".agents/references/reviews.md"]
    AGENTS_md_full -->|M| changed_documentation_or_contract_files_full["changed documentation or contract files"]
    AGENTS_md_full -. O .-> task_specific_governing_spec_or_published_contract_artifact_full["task-specific governing spec or published contract artifact"]
    AGENTS_md_full -. O .-> agents_references_testing_md_full[".agents/references/testing.md"]
```

### Review / Security Review?

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Review / Security Review?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_reviews_md_full[".agents/references/reviews.md"]
    AGENTS_md_full -. O .-> security_sensitive_source_workflow_config_or_release_files_full["security-sensitive source, workflow, config, or release files"]
    AGENTS_md_full -. O .-> agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -. O .-> agents_references_documentation_md_full[".agents/references/documentation.md"]
```

### Review / Docs Review?

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Review / Docs Review?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_reviews_md_full[".agents/references/reviews.md"]
    AGENTS_md_full -->|M| agents_references_documentation_md_full[".agents/references/documentation.md"]
    AGENTS_md_full -. O .-> agents_references_references_rules_md_full[".agents/references/references-rules.md"]
    AGENTS_md_full -. O .-> published_contract_docs_not_otherwise_named_in_this_row_full["published contract docs not otherwise named in this row"]
```

### Review / Decide

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Review / Decide"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_reviews_md_full[".agents/references/reviews.md"]
    AGENTS_md_full -. O .-> agents_references_execution_md_full[".agents/references/execution.md"]
    AGENTS_md_full -. O .-> agents_references_testing_md_full[".agents/references/testing.md"]
    agents_references_execution_md_full -. changes requested .-> FA
```

## Integration

### Integration / Re-validate

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Integration / Re-validate"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -->|M| agents_references_environment_quick_ref_md_full[".agents/references/environment-quick-ref.md"]
    AGENTS_md_full -. O .-> agents_references_workflow_md_full[".agents/references/workflow.md"]
    AGENTS_md_full -. O .-> agents_references_gradle_task_graph_md_full[".agents/references/gradle-task-graph.md"]
```

### Integration / Resolve-Conflicts?

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Integration / Resolve-Conflicts?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_workflow_md_full[".agents/references/workflow.md"]
    AGENTS_md_full -->|M| conflicting_files_being_resolved_full["conflicting files being resolved"]
    AGENTS_md_full -. O .-> concrete_agents_plans_PLAN_md_full["concrete .agents/plans/PLAN_*.md"]
    AGENTS_md_full -. O .-> agents_references_reviews_md_full[".agents/references/reviews.md"]
```

### Integration / Merge

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Integration / Merge"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_workflow_md_full[".agents/references/workflow.md"]
    AGENTS_md_full -. O .-> concrete_agents_plans_PLAN_md_full["concrete .agents/plans/PLAN_*.md"]
    AGENTS_md_full -. O .-> agents_tmp_workflow_md_full[".agents/tmp/workflow/*.md"]
```

### Integration / Post-Merge-Verify

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Integration / Post-Merge-Verify"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -->|M| agents_references_workflow_md_full[".agents/references/workflow.md"]
    AGENTS_md_full -. O .-> agents_references_execution_md_full[".agents/references/execution.md"]
    AGENTS_md_full -. O .-> agents_references_plan_execution_md_full[".agents/references/plan-execution.md"]
    agents_references_testing_md_full -. failure .-> FA
```

## Release

### Release / Gate

Metrics: chain depth 1; chain length 7; total loaded 7; deduplicated chain length 7; distinct loaded 7.

```mermaid
flowchart TD
    FA["Release / Gate"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_releases_md_full[".agents/references/releases.md"]
    AGENTS_md_full -->|M| agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -->|M| agents_references_documentation_md_full[".agents/references/documentation.md"]
    AGENTS_md_full -. O .-> concrete_agents_plans_PLAN_md_full["concrete .agents/plans/PLAN_*.md"]
    AGENTS_md_full -. O .-> ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -. O .-> CHANGELOG_md_full["CHANGELOG.md"]
    agents_references_releases_md_full -. precondition fails .-> FA
```

### Release / Tag

Metrics: chain depth 1; chain length 7; total loaded 7; deduplicated chain length 7; distinct loaded 7.

```mermaid
flowchart TD
    FA["Release / Tag"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_releases_md_full[".agents/references/releases.md"]
    AGENTS_md_full -->|M| agents_references_release_checklist_md_full[".agents/references/release-checklist.md"]
    AGENTS_md_full -->|M| CHANGELOG_md_full["CHANGELOG.md"]
    AGENTS_md_full -->|M| ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -. O .-> agents_references_LEARNINGS_md_full[".agents/references/LEARNINGS.md"]
    AGENTS_md_full -. O .-> agents_archive_full[".agents/archive/"]
```

### Release / Notes

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Release / Notes"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_releases_md_full[".agents/references/releases.md"]
    AGENTS_md_full -->|M| CHANGELOG_md_full["CHANGELOG.md"]
    AGENTS_md_full -. O .-> agents_references_release_checklist_md_full[".agents/references/release-checklist.md"]
    AGENTS_md_full -. O .-> temporary_CHANGELOG_topic_md_files_full["temporary CHANGELOG_<topic>.md files"]
```

### Release / Publish

Metrics: chain depth 1; chain length 3; total loaded 3; deduplicated chain length 3; distinct loaded 3.

```mermaid
flowchart TD
    FA["Release / Publish"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_releases_md_full[".agents/references/releases.md"]
    AGENTS_md_full -. O .-> agents_references_release_artifact_verification_md_full[".agents/references/release-artifact-verification.md"]
```

### Release / Post-Release-Cleanup

Metrics: chain depth 1; chain length 8; total loaded 8; deduplicated chain length 8; distinct loaded 8.

```mermaid
flowchart TD
    FA["Release / Post-Release-Cleanup"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_releases_md_full[".agents/references/releases.md"]
    AGENTS_md_full -->|M| agents_references_release_checklist_md_full[".agents/references/release-checklist.md"]
    AGENTS_md_full -->|M| ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -->|M| CHANGELOG_md_full["CHANGELOG.md"]
    AGENTS_md_full -->|M| agents_archive_full[".agents/archive/"]
    AGENTS_md_full -. O .-> agents_references_LEARNINGS_md_full[".agents/references/LEARNINGS.md"]
    AGENTS_md_full -. O .-> agents_references_workflow_md_full[".agents/references/workflow.md"]
```

## Deployment

### Deployment / Stage

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Deployment / Stage"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -. O .-> agents_references_release_artifact_verification_md_full[".agents/references/release-artifact-verification.md"]
    AGENTS_md_full -. O .-> infra_full["infra/"]
    AGENTS_md_full -. O .-> src_externalTest_full["src/externalTest/"]
```

### Deployment / Smoke

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Deployment / Smoke"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -. O .-> agents_references_release_artifact_verification_md_full[".agents/references/release-artifact-verification.md"]
    AGENTS_md_full -. O .-> src_manualTests_http_suites_README_md_full["src/manualTests/http/suites/README.md"]
    AGENTS_md_full -. O .-> src_externalTest_full["src/externalTest/"]
```

### Deployment / Promote

Metrics: chain depth 1; chain length 2; total loaded 2; deduplicated chain length 2; distinct loaded 2.

```mermaid
flowchart TD
    FA["Deployment / Promote"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -. O .-> deployment_specific_workflow_configuration_or_check_files_full["deployment-specific workflow, configuration, or check files"]
```

### Deployment / Verify

Metrics: chain depth 1; chain length 3; total loaded 3; deduplicated chain length 3; distinct loaded 3.

```mermaid
flowchart TD
    FA["Deployment / Verify"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -. O .-> agents_references_release_artifact_verification_md_full[".agents/references/release-artifact-verification.md"]
    AGENTS_md_full -. O .-> deployment_specific_workflow_configuration_or_check_files_full["deployment-specific workflow, configuration, or check files"]
```

### Deployment / Rollback?

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Deployment / Rollback?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -. O .-> agents_references_workflow_md_full[".agents/references/workflow.md"]
    AGENTS_md_full -. O .-> agents_references_releases_md_full[".agents/references/releases.md"]
    AGENTS_md_full -. O .-> deployment_specific_workflow_configuration_or_check_files_full["deployment-specific workflow, configuration, or check files"]
```

## Operations

### Operations / Observe

Metrics: chain depth 1; chain length 2; total loaded 2; deduplicated chain length 2; distinct loaded 2.

```mermaid
flowchart TD
    FA["Operations / Observe"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -. O .-> user_supplied_monitoring_log_trace_or_incident_files_full["user-supplied monitoring, log, trace, or incident files"]
```

### Operations / Triage

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Operations / Triage"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -. O .-> agents_references_planning_md_full[".agents/references/planning.md"]
    AGENTS_md_full -. O .-> agents_references_LEARNINGS_md_full[".agents/references/LEARNINGS.md"]
    AGENTS_md_full -. O .-> user_supplied_monitoring_log_trace_or_incident_files_full["user-supplied monitoring, log, trace, or incident files"]
```

### Operations / Hotfix?

Metrics: chain depth 1; chain length 6; total loaded 6; deduplicated chain length 6; distinct loaded 6.

```mermaid
flowchart TD
    FA["Operations / Hotfix?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_execution_md_full[".agents/references/execution.md"]
    AGENTS_md_full -->|M| agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -->|M| agents_references_reviews_md_full[".agents/references/reviews.md"]
    AGENTS_md_full -. O .-> agents_references_workflow_md_full[".agents/references/workflow.md"]
    AGENTS_md_full -. O .-> agents_references_planning_md_full[".agents/references/planning.md"]
```

### Operations / Patch?

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Operations / Patch?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| AltA[".agents/references/planning.md or .agents/references/execution.md"]
    AGENTS_md_full -. O .-> ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -. O .-> agents_references_testing_md_full[".agents/references/testing.md"]
    AGENTS_md_full -. O .-> agents_references_reviews_md_full[".agents/references/reviews.md"]
```

### Operations / Backport?

Metrics: chain depth 1; chain length 3; total loaded 3; deduplicated chain length 3; distinct loaded 3.

```mermaid
flowchart TD
    FA["Operations / Backport?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -. O .-> agents_references_workflow_md_full[".agents/references/workflow.md"]
    AGENTS_md_full -. O .-> CHANGELOG_md_full["CHANGELOG.md"]
```

### Operations / Deprecate?

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Operations / Deprecate?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -->|M| docs_DESIGN_md_full["docs/DESIGN.md"]
    AGENTS_md_full -. O .-> agents_references_planning_md_full[".agents/references/planning.md"]
    AGENTS_md_full -. O .-> published_contract_docs_not_otherwise_named_in_this_row_full["published contract docs not otherwise named in this row"]
```

## Continuous Improvement

### Continuous Improvement / Retrospect

Metrics: chain depth 1; chain length 5; total loaded 5; deduplicated chain length 5; distinct loaded 5.

```mermaid
flowchart TD
    FA["Continuous Improvement / Retrospect"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_LEARNINGS_md_full[".agents/references/LEARNINGS.md"]
    AGENTS_md_full -. O .-> agents_references_releases_md_full[".agents/references/releases.md"]
    AGENTS_md_full -. O .-> concrete_agents_plans_PLAN_md_full["concrete .agents/plans/PLAN_*.md"]
    AGENTS_md_full -. O .-> ROADMAP_md_full["ROADMAP.md"]
```

### Continuous Improvement / Capture-Learning

Metrics: chain depth 1; chain length 4; total loaded 4; deduplicated chain length 4; distinct loaded 4.

```mermaid
flowchart TD
    FA["Continuous Improvement / Capture-Learning"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_LEARNINGS_md_full[".agents/references/LEARNINGS.md"]
    AGENTS_md_full -. O .-> focused_owner_guide_for_a_durable_correction_full["focused owner guide for a durable correction"]
    AGENTS_md_full -. O .-> agents_references_references_rules_md_full[".agents/references/references-rules.md"]
```

### Continuous Improvement / Refactor?

Metrics: chain depth 1; chain length 6; total loaded 6; deduplicated chain length 6; distinct loaded 6.

```mermaid
flowchart TD
    FA["Continuous Improvement / Refactor?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_planning_md_full[".agents/references/planning.md"]
    AGENTS_md_full -->|M| ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -. O .-> docs_ARCHITECTURE_md_full["docs/ARCHITECTURE.md"]
    AGENTS_md_full -. O .-> agents_references_code_style_md_full[".agents/references/code-style.md"]
    AGENTS_md_full -. O .-> agents_references_testing_md_full[".agents/references/testing.md"]
```

### Continuous Improvement / Tech-Debt-Plan?

Metrics: chain depth 1; chain length 6; total loaded 6; deduplicated chain length 6; distinct loaded 6.

```mermaid
flowchart TD
    FA["Continuous Improvement / Tech-Debt-Plan?"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| agents_references_planning_md_full[".agents/references/planning.md"]
    AGENTS_md_full -->|M| ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -. O .-> agents_references_LEARNINGS_md_full[".agents/references/LEARNINGS.md"]
    AGENTS_md_full -. O .-> docs_ARCHITECTURE_md_full["docs/ARCHITECTURE.md"]
    AGENTS_md_full -. O .-> docs_DESIGN_md_full["docs/DESIGN.md"]
```

### Continuous Improvement / Sync

Metrics: chain depth 1; chain length 3; total loaded 3; deduplicated chain length 3; distinct loaded 3.

```mermaid
flowchart TD
    FA["Continuous Improvement / Sync"] -->|M| AGENTS_md_full["AGENTS.md"]
    AGENTS_md_full -->|M| ROADMAP_md_full["ROADMAP.md"]
    AGENTS_md_full -. O .-> CHANGELOG_md_full["CHANGELOG.md"]
```
