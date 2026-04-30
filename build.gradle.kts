import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import net.ltgt.gradle.errorprone.errorprone
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    jacoco
    pmd
    idea
    id("com.gorylenko.gradle-git-properties") version "2.5.7"
    id("com.diffplug.spotless") version "8.4.0"
    id("net.ltgt.errorprone") version "5.1.0"
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "4.0.5"
    id("org.jetbrains.qodana") version "2026.1.0"
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

val errorProneVersion = "2.44.0"
val pmdVersion = "7.17.0"
val gradleWrapperVersion = "9.5.0"
val dockerImageName = providers.gradleProperty("dockerImageName").orElse("technical-interview-demo")
val snippetsDir = layout.buildDirectory.dir("generated-snippets")
val asciidoctorTask = tasks.named<org.asciidoctor.gradle.jvm.AsciidoctorTask>("asciidoctor")

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    errorprone("com.google.errorprone:error_prone_core:$errorProneVersion")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("org.springframework.boot:spring-boot-h2console")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-micrometer-tracing-opentelemetry")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    runtimeOnly("com.h2database:h2")
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
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:testcontainers-postgresql")
}

springBoot {
    buildInfo()
}

qodana {
    resultsPath.set(layout.buildDirectory.dir("qodana/results").map { it.asFile.absolutePath })
    cachePath.set(layout.buildDirectory.dir("qodana/cache").map { it.asFile.absolutePath })
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
    description = "Builds the Docker image for this project."
    commandLine("docker", "build", "-t", dockerImageName.get(), ".")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading")
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
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    sourceDirectories.setFrom(files("$projectDir/src/main/java"))
    classDirectories.setFrom(sourceSets.main.get().output.asFileTree)
    executionData.setFrom(fileTree(layout.buildDirectory.dir("jacoco")).include("*.exec"))
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone.disableWarningsInGeneratedCode.set(true)
    options.errorprone.excludedPaths.set(".*/build/generated/.*")
}

pmd {
    toolVersion = pmdVersion
    ruleSets = emptyList()
    ruleSetFiles = files("config/pmd/pmd-ruleset.xml")
}

tasks.withType<Pmd>().configureEach {
    isConsoleOutput = true
    exclude("**/build/generated/**")
}

asciidoctorTask {
    dependsOn(tasks.test)
    inputs.dir(snippetsDir)
    attributes(
        mapOf(
            "snippets" to snippetsDir.get().asFile
        )
    )
}

val ideaFormatterBinaryCandidate: String? =
    providers.gradleProperty("ideaFormatterBinary")
        .orElse(providers.environmentVariable("IDEA_FORMATTER_BINARY"))
        .orElse(
            providers.environmentVariable("IDEA_HOME").map { ideaHome ->
                val executable = if (System.getProperty("os.name").startsWith("Windows")) "idea64.exe" else "idea"
                file("$ideaHome/bin/$executable").absolutePath
            }
        )
        .orNull

val ideaFormatterBinary = ideaFormatterBinaryCandidate?.takeIf { file(it).isFile }

if (ideaFormatterBinaryCandidate != null && ideaFormatterBinary == null) {
    logger.warn(
        "Spotless Java formatting is disabled because the IntelliJ formatter binary was not found at '{}'.",
        ideaFormatterBinaryCandidate
    )
}

spotless {
    if (ideaFormatterBinary != null) {
        java {
            target("src/main/java/**/*.java", "src/test/java/**/*.java")
            importOrder()
            removeUnusedImports()
            idea().apply {
                withDefaults(true)
                binaryPath(ideaFormatterBinary)
            }
        }
    }

    kotlinGradle {
        target("*.gradle.kts", "gradle/**/*.gradle.kts")
        ktlint().editorConfigOverride(
            mapOf(
                "ktlint_code_style" to "intellij_idea"
            )
        )
        endWithNewline()
    }

    format("misc") {
        target("*.md", ".gitignore", ".gitattributes", ".editorconfig", "src/**/*.properties")
        targetExclude("HELP.md")
        trimTrailingWhitespace()
        endWithNewline()
    }
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
