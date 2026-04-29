plugins {
    java
    id("com.diffplug.spotless") version "8.4.0"
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "4.0.5"
}

group = "team.jit"
version = "0.0.1-SNAPSHOT"
description = "technical-interview-demo"

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
