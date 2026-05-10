# Handoffs

Coordinator-owned handoff packets live here when durable workflow state is needed.

Use stable file names shaped like `<plan_stem_or_topic>__<role>.md`.
Each handoff names the objective, role, read scope, write scope, expected output, validation target, stop conditions, reporting format, and related state paths.
