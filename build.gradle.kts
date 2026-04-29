import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import net.ltgt.gradle.errorprone.errorprone
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.withType

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
    id("com.github.ben-manes.versions") version "0.54.0"
    id("com.palantir.git-version") version "5.0.0"
    id("com.adarshr.test-logger") version "4.0.0"
}

val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion()
group = "team.jit"
description = "technical-interview-demo"

val errorProneVersion = "2.44.0"
val pmdVersion = "7.17.0"
val gradleWrapperVersion = "9.5.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
    errorprone("com.google.errorprone:error_prone_core:$errorProneVersion")
    implementation("org.springframework.boot:spring-boot-h2console")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-micrometer-tracing-opentelemetry")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("io.micrometer:micrometer-tracing-bridge-otel")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jdbc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-restclient-test")
    testImplementation("org.springframework.boot:spring-boot-starter-restdocs")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testCompileOnly("org.projectlombok:lombok")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-XX:+EnableDynamicAgentLoading")
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

tasks.test {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
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
        trimTrailingWhitespace()
        replaceRegex("normalize EOF newline", "\\s*\\z", "\n")
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
