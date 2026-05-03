# Plan: Production Log Shipping

## Summary
- Complete the checked production-logging roadmap work by switching the production profile to structured JSON Lines output and adding repo-owned log-forwarding guidance or configuration that preserves multiline Java exceptions.
- Keep this as one plan because both checked items share the same runtime logging contract, deployment examples, and operator documentation.
- Success is measured by: JSON Lines production logging, preserved stack-trace fidelity during forwarding, aligned deployment assets/docs, and passing logging-focused verification plus the standard build.

## Scope
- In scope:
  - `Use a JSON Lines logger configuration in the production profile so centralized collectors receive structured entries without terminal-oriented formatting noise`
  - `Add deployment-facing log-forwarding configuration that preserves multiline exception output when shipping production logs to a centralized logging service`
- Out of scope:
  - changing local-development logging ergonomics unless required to preserve profile separation
  - vendor-locking the repository to one hosted log backend
  - changing tracing or metrics contracts beyond the logging configuration needed to keep them represented in structured output
  - revisiting the already-completed monitoring/alerting roadmap items

## Current State
- `application.properties` still defines a colored human-oriented console pattern and keeps `spring.output.ansi.enabled=DETECT`.
- `application-prod.properties` sets production logger levels but does not switch the output format away from the shared text console pattern.
- `LoggingConfigurationContractTests` currently verifies only ANSI detection and the root log level, not structured output semantics.
- The Kubernetes and Helm deployment assets expose only the application container today; there is no checked-in log-forwarding configuration example for multiline stack traces.
- `README.md` and `SETUP.md` describe the current production logging posture as stdout text with OTLP-configurable traces, which means both docs will need coordinated updates when JSON Lines and forwarding guidance land.

## Requirement Gaps And Open Questions
- The roadmap requires deployment-facing log-forwarding configuration but does not name the forwarder.
  - Why it matters: Fluent Bit, Vector, or another collector imply different example files and multiline parsing models.
  - Fallback if the user does not answer: add a vendor-neutral, repo-owned forwarder example using one lightweight collector that can preserve multiline Java exceptions from container stdout without changing the application runtime contract.
- The roadmap does not define the exact JSON field set the production logs must carry.
  - Why it matters: too little structure weakens the value of JSON Lines, while an oversized schema risks over-engineering.
  - Fallback if the user does not answer: include timestamp, level, logger, thread, message, request/trace identifiers, and exception payload while preserving existing sensitive-data redaction behavior.
- The roadmap does not say whether local `prod` runs should also emit JSON Lines by default.
  - Why it matters: this affects developer ergonomics and how tests should assert profile-specific behavior.
  - Fallback if the user does not answer: yes; `prod` should always emit the production format, while `local` keeps the existing human-oriented pattern.

## Locked Decisions And Assumptions
- Preserve stdout logging as the app's primary production log sink.
- Keep the log-forwarding guidance deployment-facing and optional; local development should not require the forwarder example.
- Prefer profile-specific logging configuration over custom in-code logging pipelines.
- Preserve current request/trace correlation and sensitive-data-sanitization behavior in the structured format.

## Affected Artifacts
- Tests:
  - `src/test/java/team/jit/technicalinterviewdemo/technical/logging/LoggingConfigurationContractTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/logging/RequestLoggingIntegrationTests.java`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/logging/HttpTracingIntegrationTests.java` if structured output assumptions need coverage
- Docs:
  - `README.md`
  - `SETUP.md`
- Source files:
  - `src/main/resources/application.properties`
  - `src/main/resources/application-prod.properties`
  - likely a new `src/main/resources/logback-spring.xml`
  - existing logging classes only if field names or MDC population need minor adjustments
- Deployment and operator assets:
  - `k8s/base/` and `helm/technical-interview-demo/` only if application env/config exposure changes
  - likely new deployment-facing forwarder examples under `k8s/` or `monitoring/`
- Build or benchmark checks:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.logging.LoggingConfigurationContractTests --tests team.jit.technicalinterviewdemo.technical.logging.RequestLoggingIntegrationTests --tests team.jit.technicalinterviewdemo.technical.logging.HttpTracingIntegrationTests`
  - `.\gradlew.bat build`

## Execution Milestones
### Milestone 1: Define The Production JSON Lines Contract
- Goal:
  - make `prod` emit structured JSON Lines instead of the shared text pattern.
- Files to update:
  - `src/main/resources/application-prod.properties`
  - `src/main/resources/application.properties`
  - likely `src/main/resources/logback-spring.xml`
  - `src/test/java/team/jit/technicalinterviewdemo/technical/logging/LoggingConfigurationContractTests.java`
- Behavior to preserve:
  - `local` remains easy to read for humans
  - request/trace identifiers remain available in logs
- Exact deliverables:
  - profile-specific production logging format
  - documented JSON field expectations
  - contract tests proving the chosen profile behavior

### Milestone 2: Keep Existing Request And Trace Logging Useful In The New Format
- Goal:
  - ensure existing request/trace logging still surfaces the important correlation data once logs become structured.
- Files to update:
  - existing logging classes under `technical.logging` only if field emission must change
  - logging integration tests under `technical.logging`
- Exact deliverables:
  - structured records that still expose request and trace identifiers
  - preserved sanitizer behavior for logged request data
  - updated focused tests where necessary

### Milestone 3: Add Deployment-Facing Multiline Forwarding Guidance
- Goal:
  - provide a repo-owned forwarding example that preserves Java stack traces during shipping.
- Files to update:
  - likely new example assets under `k8s/` or `monitoring/`
  - `README.md`
  - `SETUP.md`
  - any related Helm/Kustomize docs if the example lives alongside current deployment assets
- Exact deliverables:
  - one checked-in forwarder example or configuration bundle
  - operator-facing explanation of how multiline exceptions are preserved
  - clear separation between app logging config and downstream collector config

## Edge Cases And Failure Modes
- JSON Lines output must remain one event per line; pretty-printed JSON would break most collectors.
- Exception serialization must not collapse stack traces into unreadable fragments or drop them entirely.
- Structured logging must not reintroduce sensitive request data that the current sanitizer prevents.
- Collector examples must not imply a required production backend the repo does not actually own.
- If ANSI detection remains globally configured, the chosen logging configuration must ensure `prod` still emits pure JSON without terminal formatting noise.

## Validation Plan
- Commands to run:
  - `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.logging.LoggingConfigurationContractTests --tests team.jit.technicalinterviewdemo.technical.logging.RequestLoggingIntegrationTests --tests team.jit.technicalinterviewdemo.technical.logging.HttpTracingIntegrationTests`
  - `.\gradlew.bat build`
  - render validation for any new Helm/Kustomize logging example files if execution places them there
- Tests to add or update:
  - logging contract tests for JSON Lines output in `prod`
  - integration coverage for request/trace correlation fields if format changes require it
- Docs or contract checks:
  - keep `README.md` and `SETUP.md` aligned on the production logging and forwarding story
  - no OpenAPI baseline refresh expected
  - no HTTP example changes expected
- Manual verification steps:
  - run the app with the `prod` profile and confirm stdout emits one JSON object per line
  - simulate an exception and confirm the forwarder example preserves the multiline stack trace as one logical event downstream

## Better Engineering Notes
- The smallest coherent approach is profile-specific Logback configuration plus one deployment-facing collector example.
- Avoid inventing an application-managed shipping pipeline; the app should emit good logs and let the deployment layer forward them.
- Preserve the existing demo posture by keeping the forwarder example optional and clearly documented rather than making it a hard runtime dependency.

## Validation Results
- `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.logging.LoggingConfigurationContractTests`
  - first run failed before task execution because `JAVA_HOME` pointed to Java 11 (`Gradle requires JVM 17 or later`).
  - rerun with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3` passed.
- `.\gradlew.bat test --tests team.jit.technicalinterviewdemo.technical.logging.LoggingConfigurationContractTests --tests team.jit.technicalinterviewdemo.technical.logging.RequestLoggingIntegrationTests --tests team.jit.technicalinterviewdemo.technical.logging.HttpTracingIntegrationTests`
  - first run failed because `RequestLoggingIntegrationTests` activated `prod` profile and hit the expected `SESSION_COOKIE_SECURE=true` validator.
  - after setting `server.servlet.session.cookie.secure=true` in that test class, rerun with Java 25 passed (14/14 tests).
- `kubectl kustomize k8s/log-forwarding/fluent-bit`
  - passed; rendered ConfigMap + DaemonSet manifests successfully.
- `.\gradlew.bat build` (with `JAVA_HOME=C:\Users\kamki\.jdks\azul-25.0.3`)
  - passed, including test suite, docs generation, container build, and security scan tasks.

## User Validation
- Start the app with `prod` and verify the stdout stream is JSON Lines rather than colored text.
- Trigger one handled error and one stack-trace-producing failure path.
- Confirm the documented forwarder example keeps those exception details intact after shipping.
