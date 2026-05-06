# Flyway Migration Metadata

New or modified SQL migrations under `src/main/resources/db/migration/` must carry a checked-in JSON sidecar in this
directory.

Use the same base file name as the SQL migration:

- SQL: `src/main/resources/db/migration/V7__expand_example.sql`
- metadata: `src/main/resources/db/migration/metadata/V7__expand_example.json`

Required fields:

- `summary`: short human explanation of the migration intent
- `rolloutCategory`: one of `expand`, `contract`, `backfill`, or `breaking`
- `deploymentOrder`: one of `db-first`, `app-first`, or `out-of-band`
- `rollingCompatible`: `true` when old and new app instances can safely share the intermediate schema state during a
  rolling rollout
- `rollbackPosture`: one of `image-only` or `forward-fix-or-restore`

Recommended meanings:

- `expand`: additive schema work that keeps the old app version working while the new app rolls out
- `contract`: cleanup or constraint-tightening work that should happen only after the new app is fully deployed or after
  an out-of-band cutover
- `backfill`: data rewrite or population work that may need a separate operational step
- `breaking`: schema or data changes that are not safe for mixed-version rollout

Release impact classification uses this metadata:

- `none`: no SQL migration files changed since the previous release tag
- `rolling-compatible`: every changed migration is `rollingCompatible=true` and `rollbackPosture=image-only`
- `restore-sensitive`: any changed migration is not rolling-compatible or requires `forward-fix-or-restore`

Historical migrations do not need backfilled metadata unless the SQL file itself is being modified now.

Example sidecar:

```json
{
  "summary": "Adds a nullable column that the new write path can start filling after rollout.",
  "rolloutCategory": "expand",
  "deploymentOrder": "db-first",
  "rollingCompatible": true,
  "rollbackPosture": "image-only"
}
```
