# Gradle Task Dependency Graph

Use this on-demand reference to choose the smallest useful `./build.ps1` command.

Last refreshed: 2026-05-07.

Sources:

- `build.gradle.kts`
- `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/JacocoCoverageConventionsPlugin.kt`
- `buildSrc/src/main/kotlin/team/jit/technicalinterviewdemo/build/ExternalTestingConventionsPlugin.kt`
- `./build.ps1 tasks --all --no-daemon`
- `./build.ps1 -FullBuild build --dry-run --no-daemon`
- `./build.ps1 check --dry-run --no-daemon`
- `./build.ps1 checkFormat --dry-run --no-daemon`
- `./build.ps1 format --dry-run --no-daemon`
- `./build.ps1 -FullBuild build imageVulnerabilityScan --dry-run --no-daemon`
- `./build.ps1 externalSmokeTest --dry-run --no-daemon`
- `./build.ps1 externalDeploymentCheck --dry-run --no-daemon`
- `./build.ps1 scheduledExternalCheck --dry-run --no-daemon`
- `./build.ps1 gatlingBenchmark --dry-run --no-daemon`

In the graphs below, `A --> B` means running `A` also schedules `B`.
For exact expansion after build changes, run:

```powershell
./build.ps1 <task-or-tasks> --dry-run --no-daemon
```

Use `./build.ps1 -FullBuild build --dry-run --no-daemon` when inspecting the full `build` graph; without `-FullBuild`, the wrapper can take the lightweight-file shortcut and skip Gradle.
Most dry-runs also list `updatePalantirJavaFormatXml` and `updateWorkspaceXml` from the Palantir IDEA plugin. Treat those as IDE metadata synchronization tasks, not validation gates; they are omitted from the graphs below.

## Standard Build

```mermaid
graph TD
  build --> assemble
  build --> check
  build --> dockerBuild

  assemble --> jar
  assemble --> bootJar

  jar --> classes
  bootJar --> classes
  bootJar --> resolveMainClassName
  bootJar --> asciidoctor
  dockerBuild --> bootJar

  asciidoctor --> bootBuildInfo
  asciidoctor --> test

  test --> testClasses
  testClasses --> compileTestJava
  testClasses --> processTestResources
  compileTestJava --> classes

  classes --> compileJava
  classes --> processResources
  processResources --> bootBuildInfo
  processResources --> generateGitProperties
```

Decision shortcuts:

- `./build.ps1 compileJava`: fastest Java production compile check.
- `./build.ps1 test --tests <pattern>`: focused executable spec check.
- `./build.ps1 build`: normal final verification; may skip Gradle for lightweight-only uncommitted changes.
- `./build.ps1 -FullBuild build`: force final full verification, including Docker image build, checks, dependency vulnerability scan, and SBOM.
- `./build.ps1 imageVulnerabilityScan`: explicit container image vulnerability scan; not scheduled by `build`.
- Do not separately run `test`, `asciidoctor`, `dockerBuild`, PMD, SpotBugs, `checkFormat`, dependency vulnerability scan, or SBOM when `build` already provides the required proof.

## Check And Quality Gates

```mermaid
graph TD
  check --> test
  check --> jacocoTestCoverageVerification
  check --> pmdMain
  check --> pmdTest
  check --> pmdExternalTest
  check --> pmdGatling
  check --> spotlessCheck
  check --> spotbugsMain
  check --> spotbugsTest
  check --> spotbugsExternalTest
  check --> spotbugsGatling
  check --> staticSecurityScan
  check --> dependencyVulnerabilityScan
  check --> sbom

  jacocoTestCoverageVerification --> test
  test -. finalizedBy .-> jacocoCoverageSummary
  jacocoCoverageSummary --> jacocoTestReport
  jacocoTestReport --> test

  staticSecurityScan --> spotbugsMain
  vulnerabilityScan --> dependencyVulnerabilityScan
  vulnerabilityScan --> imageVulnerabilityScan

  checkFormat --> spotlessCheck
  spotlessCheck --> spotlessJavaCheck
  spotlessCheck --> spotlessKotlinCheck
  spotlessCheck --> spotlessKotlinGradleCheck
  spotlessCheck --> spotlessMiscCheck

  dependencyVulnerabilityScan --> prepareDependencyVulnerabilityScanInput
  applicationSbom --> prepareDependencyVulnerabilityScanInput
  prepareDependencyVulnerabilityScanInput --> bootJar

  imageVulnerabilityScan --> dockerBuild
  imageSbom --> dockerBuild

  sbom --> applicationSbom
  sbom --> imageSbom
```

Notes:

- `checkFormat` is an explicit convenience task for formatter checks; the standard `check` task schedules `spotlessCheck` directly.
- Palantir Java Format contributes the Java formatter step through `spotlessJava`; retained Spotless targets also cover Kotlin, Gradle Kotlin DSL, and selected support-file whitespace normalization.
- SpotBugs tasks for `test`, `externalTest`, and `gatling` are registered but disabled by the build script; `spotbugsMain` is the active static security scan target.
- Manual regression under `src/manualTests` is opt-in: automated/release builds may compile and format it, but `manualTests`, `pmdManualTests`, and `spotbugsManualTests` run only when explicitly requested.
- `imageVulnerabilityScan` remains available through explicit task selection and through `vulnerabilityScan`; it is intentionally not a `build` or `check` dependency.
- `-SkipChecks` excludes formatting, PMD, SpotBugs, Error Prone, coverage verification, the build-wired dependency vulnerability scan, explicit vulnerability scan tasks, and SBOM checks for local loops only.
- `-SkipTests` is separate from `-SkipChecks`; use both only when intentionally doing a compile/package loop.

## Formatting Entry Points

```mermaid
graph TD
  checkFormat --> spotlessCheck
  spotlessCheck --> spotlessJavaCheck
  spotlessCheck --> spotlessKotlinCheck
  spotlessCheck --> spotlessKotlinGradleCheck
  spotlessCheck --> spotlessMiscCheck

  spotlessJavaCheck --> spotlessJava
  spotlessKotlinCheck --> spotlessKotlin
  spotlessKotlinGradleCheck --> spotlessKotlinGradle
  spotlessMiscCheck --> spotlessMisc

  format --> spotlessApply
  spotlessApply --> spotlessJavaApply
  spotlessApply --> spotlessKotlinApply
  spotlessApply --> spotlessKotlinGradleApply
  spotlessApply --> spotlessMiscApply

  spotlessJavaApply --> spotlessJava
  spotlessKotlinApply --> spotlessKotlin
  spotlessKotlinGradleApply --> spotlessKotlinGradle
  spotlessMiscApply --> spotlessMisc
```

Decision shortcuts:

- `./build.ps1 checkFormat`: verify Palantir-backed Java formatting and retained Spotless formatting without running tests, PMD, SpotBugs, scans, or SBOM tasks.
- `./build.ps1 format`: apply the same formatter graph in-place.
- `./build.ps1 formatDiff`: Palantir task for formatting only chunks that appear in the git diff; useful for narrow Java cleanup, but not a replacement for `checkFormat` before handoff.

## External And Benchmark Gates

```mermaid
graph TD
  externalSmokeTest --> externalSmokeVerification
  externalSmokeVerification --> externalTestClasses
  externalSmokeVerification --> externalSmokeEnvironmentUp
  externalSmokeVerification -. finalizedBy .-> externalSmokeEnvironmentDown
  externalSmokeEnvironmentUp --> dockerBuild

  externalDeploymentCheck --> externalTestClasses
  scheduledExternalCheck --> externalDeploymentCheck

  gatlingBenchmark --> dockerBuild
  gatlingBenchmark --> gatlingClasses

  externalTestClasses --> compileExternalTestJava
  externalTestClasses --> processExternalTestResources
  compileExternalTestJava --> classes

  gatlingClasses --> compileGatlingJava
  gatlingClasses --> processGatlingResources
  compileGatlingJava --> classes

  dockerBuild --> bootJar
```

Decision shortcuts:

- `./build.ps1 externalSmokeTest`: Docker-backed deployed-shape smoke check; already builds the image.
- `./build.ps1 externalDeploymentCheck`: checks an already deployed target; it does not build or deploy an image.
- `./build.ps1 scheduledExternalCheck`: scheduled alias for `externalDeploymentCheck`.
- `./build.ps1 gatlingBenchmark`: Docker-backed benchmark; already builds the image and packaging/test/doc prerequisites needed for that image.
- If both `build` and `gatlingBenchmark`, `externalSmokeTest`, or `externalDeploymentCheck` are required, prefer one command such as `./build.ps1 build gatlingBenchmark --no-daemon`, or run them sequentially when the deployed target setup makes one invocation awkward.
- Do not run `build`, `gatlingBenchmark`, `externalSmokeTest`, `externalDeploymentCheck`, or `scheduledExternalCheck` in parallel with each other because they share Gradle outputs and Docker/test resources.

## Command Choice Table

| Change type | Fast loop | Final or extra proof |
| --- | --- | --- |
| Production Java compile change | `./build.ps1 compileJava` | `./build.ps1 build` |
| Test-only compile change | `./build.ps1 testClasses` | targeted `test --tests <pattern>` or `build` |
| Focused business or service rule | `./build.ps1 test --tests <pattern>` | `./build.ps1 build` |
| Public API behavior or REST Docs | targeted integration/docs tests | `./build.ps1 build`; refresh OpenAPI only after intentional contract review |
| Formatter-only change | `./build.ps1 checkFormat` or `./build.ps1 format --dry-run` | `./build.ps1 checkFormat`; `build` already includes equivalent formatter proof through `spotlessCheck` |
| Build wrapper or Gradle config | `./build.ps1 -SkipChecks compileJava` or `--dry-run` | `./build.ps1 -FullBuild build` |
| Docker image and runtime packaging | `./build.ps1 dockerBuild` when narrow | `./build.ps1 build` |
| Container image vulnerability scan | `./build.ps1 imageVulnerabilityScan` | `./build.ps1 vulnerabilityScan` when dependency scan is also needed |
| External smoke environment | `./build.ps1 externalSmokeTest` | combine with `build` in one invocation when both are required |
| Already deployed environment | `./build.ps1 externalDeploymentCheck` | use only after target URL and credentials are configured |
| Benchmark-sensitive behavior | targeted tests first | `./build.ps1 gatlingBenchmark`; combine with `build` in one invocation when both are required |
| Documentation-only or lightweight support files | `./build.ps1 build` classifier path | manual consistency review when classifier skips heavy validation |
