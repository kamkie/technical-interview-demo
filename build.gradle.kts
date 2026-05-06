import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask
import net.ltgt.gradle.errorprone.errorprone
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import team.jit.technicalinterviewdemo.build.TrivyFilesystemScanTask
import team.jit.technicalinterviewdemo.build.TrivyImageScanTask
import java.util.Properties

buildscript {
    configurations.classpath {
        resolutionStrategy.force(
            "org.apache.commons:commons-lang3:3.20.0",
            "org.codehaus.plexus:plexus-utils:4.0.3",
        )
    }
}

plugins {
    java
    jacoco
    pmd
    idea
    id("technical-interview-demo.jacoco-conventions")
    id("technical-interview-demo.external-testing-conventions")
    id("io.gatling.gradle") version "3.15.0.1"
    id("com.gorylenko.gradle-git-properties") version "2.5.7"
    id("com.github.spotbugs") version "6.5.4"
    id("com.diffplug.spotless") version "8.4.0"
    id("com.palantir.java-format") version "2.90.0"
    id("com.palantir.java-format-idea") version "2.90.0"
    id("net.ltgt.errorprone") version "5.1.0"
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "4.0.5"
    id("com.github.ben-manes.versions") version "0.54.0"
    id("com.palantir.git-version") version "5.0.0"
    id("com.adarshr.test-logger") version "4.0.0"
}

val gitVersion: groovy.lang.Closure<String> by extra
fun getProjectVersion(): String = try {
    gitVersion()
} catch (e: Exception) {
    "0.0.0-SNAPSHOT"
}

version = getProjectVersion()
group = "team.jit"
description = "technical-interview-demo"

val errorProneVersion = "2.49.0"
val findSecBugsVersion = "1.14.0"
val pmdVersion = "7.22.0"
val spotbugsVersion = "4.9.8"
val gradleWrapperVersion = "9.5.0"
val springdocVersion = "3.0.3"
val postgresqlVersion = "42.7.11"
val nettyVersion = "4.2.13.Final"
val asciidoctorJrubyVersion = "10.1.0.0"
val dockerImageName = providers.gradleProperty("dockerImageName").orElse("technical-interview-demo")
val snippetsDir = layout.buildDirectory.dir("generated-snippets")
val buildInfoPropertiesFile = layout.buildDirectory.file("resources/main/META-INF/build-info.properties")
val approvedOpenApiFile = layout.projectDirectory.file("src/test/resources/openapi/approved-openapi.json")
val asciidoctorTask = tasks.named<org.asciidoctor.gradle.jvm.AsciidoctorTask>("asciidoctor")
val spotbugsSecurityIncludeFile = layout.projectDirectory.file("tooling/security/spotbugs-security-include.xml")
val spotbugsSecurityExcludeFile = layout.projectDirectory.file("tooling/security/spotbugs-security-exclude.xml")
val trivyIgnoreFile = layout.projectDirectory.file("tooling/security/trivy.ignore")
val trivyContainerImage =
    providers.gradleProperty("trivyImage")
        .orElse("aquasec/trivy:0.70.0@sha256:be1190afcb28352bfddc4ddeb71470835d16462af68d310f9f4bca710961a41e")
val trivyFailOnSeverities = listOf("HIGH", "CRITICAL")
val applicationSbomReportDir = layout.buildDirectory.dir("reports/sbom/application")
val imageSbomReportDir = layout.buildDirectory.dir("reports/sbom/image")
val skipChecks = providers.gradleProperty("skipChecks").map(String::toBoolean).orElse(false)
val spotlessMiscTargets = arrayOf(
    "AGENTS.md",
    "CHANGELOG.md",
    "CONTRIBUTING.md",
    "README.md",
    "ROADMAP.md",
    "SETUP.md",
    "WORKING_WITH_AI.md",
    ".editorconfig",
    ".gitattributes",
    ".gitignore",
    "Dockerfile",
    "docker-compose*.yml",
    ".devcontainer/**/*.json",
    ".devcontainer/**/*.md",
    ".devcontainer/**/*.sql",
    ".devcontainer/**/*.yml",
    ".github/**/*.yml",
    "ai/**/*.json",
    "ai/**/*.md",
    "ai/**/*.yaml",
    "ai/**/*.yml",
    "infra/**/*.json",
    "infra/**/*.md",
    "infra/**/*.yaml",
    "infra/**/*.yml",
    "src/gatling/resources/*.xml",
    "src/main/resources/**/*.json",
    "src/main/resources/**/*.sql",
    "src/main/resources/**/*.xml",
    "src/test/resources/**/*.http",
    "tooling/pmd/**/*.xml",
    "tooling/security/**/*.xml",
)
val ktlintEditorConfigOverride = mapOf(
    "ktlint_code_style" to "intellij_idea",
    "ij_kotlin_imports_layout" to "*,javax.**,java.**,kotlin.**,^",
    "ktlint_standard_no-unused-imports" to "enabled",
)

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("io.netty:netty-bom:$nettyVersion")
    }
    dependencies {
        dependency("org.postgresql:postgresql:$postgresqlVersion")
        dependency("org.jruby:jruby:$asciidoctorJrubyVersion")
    }
}

dependencies {
    components {
        withModule("org.asciidoctor:asciidoctorj") {
            allVariants {
                withDependencies {
                    // AsciidoctorJ 2.5.7 publishes a stale JRuby dependency; keep dependency reports on the patched line.
                    removeIf { it.group == "org.jruby" && it.name == "jruby" }
                    add("org.jruby:jruby:$asciidoctorJrubyVersion")
                }
            }
        }
    }

    errorprone("com.google.errorprone:error_prone_core:$errorProneVersion")
    spotbugs("com.github.spotbugs:spotbugs:$spotbugsVersion")
    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:$findSecBugsVersion")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-micrometer-tracing-opentelemetry")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:$springdocVersion")
    implementation("org.springframework.session:spring-session-jdbc")
    implementation("com.github.ben-manes.caffeine:caffeine")

    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("io.micrometer:micrometer-tracing-bridge-otel")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    testCompileOnly("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testAnnotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-restdocs")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:testcontainers-postgresql")
}

springBoot {
    buildInfo()
}

tasks.bootRun {
    dependsOn(asciidoctorTask)
    systemProperty("spring.output.ansi.enabled", "always")
}

tasks.bootJar {
    dependsOn(asciidoctorTask)
    archiveClassifier.set("boot")
    from(asciidoctorTask) {
        into("BOOT-INF/classes/static/docs")
    }
    layered {
        enabled.set(true)
    }
}

tasks.register<Exec>("dockerBuild") {
    group = "docker"
    description = "Builds the Docker image for this project. Also runs as part of the build lifecycle."
    dependsOn(tasks.bootJar)
    doFirst {
        val bootJarFile = tasks.bootJar.get().archiveFile.get().asFile
        commandLine(
            "docker",
            "build",
            "--build-arg",
            "JAR_FILE=build/libs/${bootJarFile.name}",
            "-t",
            dockerImageName.get(),
            ".",
        )
    }
}

val prepareDependencyVulnerabilityScanInput = tasks.register<Sync>("prepareDependencyVulnerabilityScanInput") {
    group = "verification"
    description = "Extracts the packaged boot jar for dependency vulnerability scanning."
    dependsOn(tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar"))
    val bootJar = tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar")
    from({
        zipTree(bootJar.get().archiveFile.get().asFile)
    })
    into(layout.buildDirectory.dir("security/dependency-scan/input"))
}

val dependencyVulnerabilityScan = tasks.register<TrivyFilesystemScanTask>("dependencyVulnerabilityScan") {
    group = "verification"
    description = "Scans packaged runtime dependencies for known vulnerabilities."
    dependsOn(prepareDependencyVulnerabilityScanInput)
    trivyImage.convention(trivyContainerImage)
    ignoreFile.convention(trivyIgnoreFile)
    failOnSeverities.convention(trivyFailOnSeverities)
    scanLabel.convention("dependencyVulnerabilityScan")
    inputDirectory.convention(layout.buildDirectory.dir("security/dependency-scan/input"))
    reportDirectory.convention(layout.buildDirectory.dir("reports/security/dependencies"))
    cacheDirectory.convention(layout.buildDirectory.dir("security/trivy-cache/dependencies"))
}

val imageVulnerabilityScan = tasks.register<TrivyImageScanTask>("imageVulnerabilityScan") {
    group = "verification"
    description = "Scans the Docker image built by dockerBuild for OS and package vulnerabilities."
    dependsOn(tasks.named("dockerBuild"))
    trivyImage.convention(trivyContainerImage)
    ignoreFile.convention(trivyIgnoreFile)
    failOnSeverities.convention(trivyFailOnSeverities)
    scanLabel.convention("imageVulnerabilityScan")
    imageName.convention(dockerImageName)
    reportDirectory.convention(layout.buildDirectory.dir("reports/security/image"))
    cacheDirectory.convention(layout.buildDirectory.dir("security/trivy-cache/image"))
}

val applicationSbom = tasks.register<Exec>("applicationSbom") {
    group = "verification"
    description = "Generates a CycloneDX SBOM for the packaged boot jar artifact."
    dependsOn(prepareDependencyVulnerabilityScanInput)
    val inputDirectory = layout.buildDirectory.dir("security/dependency-scan/input")
    val outputFile = applicationSbomReportDir.map { it.file("application.cyclonedx.json") }
    inputs.dir(inputDirectory)
    outputs.file(outputFile)
    doFirst {
        applicationSbomReportDir.get().asFile.mkdirs()
        val inputMount = inputDirectory.get().asFile.absolutePath.replace('\\', '/')
        val outputMount = applicationSbomReportDir.get().asFile.absolutePath.replace('\\', '/')
        commandLine(
            "docker",
            "run",
            "--rm",
            "-v",
            "$inputMount:/workspace/input:ro",
            "-v",
            "$outputMount:/workspace/output",
            trivyContainerImage.get(),
            "fs",
            "/workspace/input",
            "--format",
            "cyclonedx",
            "--output",
            "/workspace/output/application.cyclonedx.json",
            "--quiet",
            "--no-progress",
        )
    }
}

val imageSbom = tasks.register<Exec>("imageSbom") {
    group = "verification"
    description = "Generates a CycloneDX SBOM for the Docker image built by dockerBuild."
    dependsOn(tasks.named("dockerBuild"))
    val outputFile = imageSbomReportDir.map { it.file("image.cyclonedx.json") }
    inputs.property("imageName", dockerImageName)
    outputs.file(outputFile)
    doFirst {
        imageSbomReportDir.get().asFile.mkdirs()
        val outputMount = imageSbomReportDir.get().asFile.absolutePath.replace('\\', '/')
        commandLine(
            "docker", "run", "--rm",
            "-v", "//var/run/docker.sock:/var/run/docker.sock",
            "-v", "$outputMount:/workspace/output",
            trivyContainerImage.get(), "image", dockerImageName.get(),
            "--format", "cyclonedx", "--output", "/workspace/output/image.cyclonedx.json", "--quiet", "--no-progress",
        )
    }
}
tasks.register("vulnerabilityScan") {
    group = "verification"
    description = "Runs dependency and Docker image vulnerability scans."
    dependsOn(dependencyVulnerabilityScan, imageVulnerabilityScan)
}

val sbom = tasks.register("sbom") {
    group = "verification"
    description = "Generates CycloneDX SBOMs for the packaged application artifact and Docker image."
    dependsOn(applicationSbom, imageSbom)
}
val staticSecurityScan = tasks.register("staticSecurityScan") {
    group = "verification"
    description = "Runs SpotBugs with FindSecBugs against production code."
    dependsOn(tasks.named("spotbugsMain"))
}

tasks.register<JavaExec>("refreshOpenApiBaseline") {
    group = "documentation"
    description = "Refreshes the approved OpenAPI baseline from the current application contract."
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("team.jit.technicalinterviewdemo.technical.docs.OpenApiBaselineGenerator")
    args(approvedOpenApiFile.asFile.absolutePath)
    jvmArgs("-XX:+EnableDynamicAgentLoading", "-Xshare:off")
}

tasks.build {
    dependsOn(tasks.named("dockerBuild"))
}

tasks.check {
    dependsOn(staticSecurityScan, dependencyVulnerabilityScan, imageVulnerabilityScan, sbom)
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading", "-Xshare:off")
    outputs.dir(snippetsDir)
    testLogging {
        // add for debuging TestLogEvent.STANDARD_OUT
        events =
            mutableSetOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED, TestLogEvent.STANDARD_ERROR)
        showExceptions = true
        exceptionFormat = TestExceptionFormat.FULL
        showCauses = true
        showStackTraces = true
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone.enabled.set(skipChecks.map { !it })
    options.errorprone.disableWarningsInGeneratedCode.set(true)
    options.errorprone.excludedPaths.set(".*/build/generated/.*")
    options.forkOptions.jvmArgs?.add("--sun-misc-unsafe-memory-access=allow")
}

tasks.withType<SpotBugsTask>().configureEach {
    enabled = name == "spotbugsMain"
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.HIGH)
    maxHeapSize.set("1g")
    showProgress.set(false)
}

tasks.named<SpotBugsTask>("spotbugsMain") {
    group = "verification"
    description = "Runs code-focused static application security analysis on production code."
    includeFilter.set(spotbugsSecurityIncludeFile)
    excludeFilter.set(spotbugsSecurityExcludeFile)
    reports.create("xml") {
        required.set(true)
        outputLocation.set(layout.buildDirectory.file("reports/security/static/main/report.xml"))
    }
    reports.create("html") {
        required.set(true)
        outputLocation.set(layout.buildDirectory.file("reports/security/static/main/report.html"))
    }
}

tasks.matching { it.name == "compileGatlingJava" }.configureEach {
    if (this is JavaCompile) {
        options.release.set(21)
    }
}

pmd {
    toolVersion = pmdVersion
    ruleSets = emptyList()
    ruleSetFiles = files("tooling/pmd/pmd-ruleset.xml")
}

asciidoctorj {
    setJrubyVersion(asciidoctorJrubyVersion)
}

tasks.withType<Pmd>().configureEach {
    isConsoleOutput = true
    exclude("**/build/generated/**")
}

asciidoctorTask {
    dependsOn(tasks.named("bootBuildInfo"), tasks.test)
    setExecutionMode("JAVA_EXEC")
    jvm {
        jvmArgs(
            "--enable-native-access=ALL-UNNAMED",
            "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
            "--add-opens=java.base/java.io=ALL-UNNAMED",
            "--sun-misc-unsafe-memory-access=allow",
            "-Xshare:off",
        )
    }
    inputs.dir(snippetsDir)
    inputs.file(buildInfoPropertiesFile)
    attributes(
        mapOf(
            "snippets" to snippetsDir.get().asFile,
        ),
    )
    doFirst {
        val buildInfo = Properties()
        buildInfoPropertiesFile.get().asFile.inputStream().use { buildInfo.load(it) }
        attributes(
            mapOf(
                "build-info-path" to "/META-INF/build-info.properties",
                "build-name" to buildInfo.getProperty("build.name", "unknown"),
                "build-group" to buildInfo.getProperty("build.group", "unknown"),
                "build-artifact" to buildInfo.getProperty("build.artifact", "unknown"),
                "build-version" to buildInfo.getProperty("build.version", "unknown"),
                "build-time" to buildInfo.getProperty("build.time", "unknown"),
            ),
        )
    }
}

spotless {
    java {
        // com.palantir.java-format-spotless contributes the Java formatter step for this target.
        target("src/**/*.java", "buildSrc/src/**/*.java")
        importOrder("", "javax", "java", "\\#")
        removeUnusedImports("cleanthat-javaparser-unnecessaryimport")
    }

    kotlin {
        target("buildSrc/src/**/*.kt")
        ktlint().editorConfigOverride(ktlintEditorConfigOverride)
        endWithNewline()
    }

    kotlinGradle {
        target("*.gradle.kts", "gradle/**/*.gradle.kts", "buildSrc/**/*.gradle.kts")
        ktlint().editorConfigOverride(ktlintEditorConfigOverride)
        endWithNewline()
    }

    format("misc") {
        target(*spotlessMiscTargets)
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.register("checkFormat") {
    group = "verification"
    description = "Checks repository formatting through Palantir Java Format and retained Spotless formatters."
    dependsOn(tasks.named("spotlessCheck"))
}

tasks.register("format") {
    group = "formatting"
    description = "Applies Palantir Java Format and retained Spotless formatters."
    dependsOn(tasks.named("spotlessApply"))
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf { (candidate.group == "org.jacoco") && (candidate.version != currentVersion) }
    rejectVersionIf { isNonStable(candidate.version) && !isNonStable(currentVersion) }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.wrapper {
    gradleVersion = gradleWrapperVersion
    distributionType = Wrapper.DistributionType.ALL
}
