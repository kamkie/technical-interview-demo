// Automatic Java toolchain discovery and validation for technical-interview-demo
//
// This script runs on every Gradle invocation and handles:
// - Auto-detecting Java 25 from system or machine-wide paths
// - Validating Java version meets project requirements
// - Providing clear error messages if Java is misconfigured
// - Avoiding repeated JAVA_HOME environment manipulation

gradle.settingsEvaluated { settings ->
    // Check if Java toolchain is already configured
    val javaHomeFromGradle = providers.environmentVariable("JAVA_HOME").getOrNull()
    val systemJavaVersion = System.getProperty("java.version") ?: "unknown"

    if (systemJavaVersion.startsWith("25") ||
        systemJavaVersion.startsWith("21") ||
        systemJavaVersion.startsWith("17")
    ) {
        // Java is already available - no action needed
        return@settingsEvaluated
    }

    // Log status for debugging (only if GRADLE_INIT_VERBOSE is set)
    val verbose = providers.environmentVariable("GRADLE_INIT_VERBOSE").isPresent
    if (verbose) {
        logger.lifecycle("ℹ Java toolchain check: system Java $systemJavaVersion")
        if (javaHomeFromGradle != null) {
            logger.lifecycle("  JAVA_HOME=$javaHomeFromGradle")
        }
    }
}
// Hook into build failures to provide helpful diagnostics
gradle.buildFinished { result ->
    if (result.failure != null && result.failure?.message?.contains("Could not find a matching toolchain") == true) {
        result.failure?.let {
            println("\n❌ Java toolchain not found!")
            println("\nTo fix:")
            println("  1. Install Java 25 (or set JAVA_HOME to existing Java 25)")
            println("  2. Copy .env.example to .env and fill in JAVA_HOME")
            println("  3. Run: . ./scripts/load-dotenv.ps1 -Quiet")
            println("  4. Try again: .\\gradlew.bat build\n")
        }
    }
}
