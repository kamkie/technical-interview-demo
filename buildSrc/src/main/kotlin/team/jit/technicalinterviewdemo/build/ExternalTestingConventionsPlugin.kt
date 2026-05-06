package team.jit.technicalinterviewdemo.build

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

class ExternalTestingConventionsPlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        pluginManager.withPlugin("java") {
            configureExternalSmokeTest()
        }
        pluginManager.withPlugin("io.gatling.gradle") {
            configureGatlingBenchmark()
        }
    }

    private fun Project.configureExternalSmokeTest() {
        val sourceSets = extensions.getByType<SourceSetContainer>()
        val mainSourceSet = sourceSets.named("main")
        val externalTestSourceSet = sourceSets.maybeCreate(EXTERNAL_TEST_SOURCE_SET_NAME).apply {
            java.setSrcDirs(listOf("src/externalTest/java"))
            resources.setSrcDirs(listOf("src/externalTest/resources"))
            compileClasspath += mainSourceSet.get().output + configurations.getByName("testCompileClasspath")
            runtimeClasspath += output + compileClasspath + configurations.getByName("testRuntimeClasspath")
        }

        configurations.named(externalTestSourceSet.implementationConfigurationName) {
            extendsFrom(configurations.getByName("testImplementation"))
        }
        configurations.named(externalTestSourceSet.runtimeOnlyConfigurationName) {
            extendsFrom(configurations.getByName("testRuntimeOnly"))
        }
        configurations.named(externalTestSourceSet.compileOnlyConfigurationName) {
            extendsFrom(configurations.getByName("testCompileOnly"))
        }
        configurations.named(externalTestSourceSet.annotationProcessorConfigurationName) {
            extendsFrom(configurations.getByName("testAnnotationProcessor"))
        }

        val dockerImageName = providers.gradleProperty("externalSmokeImageName")
            .orElse(providers.gradleProperty("dockerImageName"))
            .orElse("technical-interview-demo")
        val smokeHostPort = intProperty("externalSmoke.hostPort", 18080)
        val smokeDatabaseHostPort = intProperty("externalSmoke.postgresHostPort", 15432)
        val smokeDatabaseName = providers.gradleProperty(
            "externalSmoke.databaseName",
        ).orElse("technical_interview_demo")
        val smokeDatabaseUser = providers.gradleProperty("externalSmoke.databaseUser").orElse("postgres")
        val smokeDatabasePassword = providers.gradleProperty("externalSmoke.databasePassword").orElse("changeme")
        val smokeBaseUrl = providers.gradleProperty("externalSmoke.baseUrl")
            .orElse(smokeHostPort.map { "http://127.0.0.1:$it" })
        val smokeJdbcUrl = providers.provider {
            "jdbc:postgresql://127.0.0.1:${smokeDatabaseHostPort.get()}/${smokeDatabaseName.get()}"
        }
        val smokeUpTask = registerExternalSmokeEnvironmentUpTask(dockerImageName, smokeHostPort)
        val smokeDownTask = registerExternalSmokeEnvironmentDownTask()

        val smokeVerificationTask = tasks.register<Test>("externalSmokeVerification") {
            description = "Executes tests from src/externalTest against the containerized smoke environment."
            group = "verification"
            dependsOn(tasks.named(externalTestSourceSet.classesTaskName))
            testClassesDirs = externalTestSourceSet.output.classesDirs
            classpath = externalTestSourceSet.runtimeClasspath
            shouldRunAfter(tasks.named("test"))
            useJUnitPlatform()
            outputs.upToDateWhen { false }
            doFirst {
                val baseUrl = smokeBaseUrl.get()
                logger.lifecycle("[externalSmokeTest] Running external smoke assertions against {}.", baseUrl)
                systemProperty("app.baseUrl", baseUrl)
                systemProperty("external.baseUrl", baseUrl)
                systemProperty("external.jdbc.url", smokeJdbcUrl.get())
                systemProperty("external.jdbc.user", smokeDatabaseUser.get())
                systemProperty("external.jdbc.password", smokeDatabasePassword.get())
                environment("EXTERNAL_BASE_URL", baseUrl)
            }
        }
        smokeVerificationTask.configure {
            dependsOn(smokeUpTask)
            finalizedBy(smokeDownTask)
            extensions.configure(JacocoTaskExtension::class.java) {
                isEnabled = false
            }
        }

        tasks.register("externalSmokeTest") {
            group = "verification"
            description = "Runs Docker-backed external smoke tests against the packaged application image."
            dependsOn(smokeVerificationTask)
        }

        val deploymentBaseUrl = providers.gradleProperty("externalCheck.baseUrl")
            .orElse(providers.environmentVariable("EXTERNAL_CHECK_BASE_URL"))
            .orElse(providers.environmentVariable("EXTERNAL_BASE_URL"))
        val deploymentJdbcUrl = providers.gradleProperty("externalCheck.jdbcUrl")
            .orElse(providers.environmentVariable("EXTERNAL_CHECK_JDBC_URL"))
            .orElse(providers.environmentVariable("EXTERNAL_JDBC_URL"))
        val deploymentJdbcUser = providers.gradleProperty("externalCheck.jdbcUser")
            .orElse(providers.environmentVariable("EXTERNAL_CHECK_JDBC_USER"))
            .orElse(providers.environmentVariable("EXTERNAL_JDBC_USER"))
        val deploymentJdbcPassword = providers.gradleProperty("externalCheck.jdbcPassword")
            .orElse(providers.environmentVariable("EXTERNAL_CHECK_JDBC_PASSWORD"))
            .orElse(providers.environmentVariable("EXTERNAL_JDBC_PASSWORD"))
        val expectedBuildVersion = providers.gradleProperty("externalCheck.expectedBuildVersion")
            .orElse(providers.environmentVariable("EXTERNAL_CHECK_EXPECTED_BUILD_VERSION"))
        val expectedShortCommitId = providers.gradleProperty("externalCheck.expectedShortCommitId")
            .orElse(providers.environmentVariable("EXTERNAL_CHECK_EXPECTED_SHORT_COMMIT_ID"))
        val expectedActiveProfile = providers.gradleProperty("externalCheck.expectedActiveProfile")
            .orElse(providers.environmentVariable("EXTERNAL_CHECK_EXPECTED_ACTIVE_PROFILE"))
        val expectedSessionStoreType = providers.gradleProperty("externalCheck.expectedSessionStoreType")
            .orElse(providers.environmentVariable("EXTERNAL_CHECK_EXPECTED_SESSION_STORE_TYPE"))
        val expectedSessionTimeout = providers.gradleProperty("externalCheck.expectedSessionTimeout")
            .orElse(providers.environmentVariable("EXTERNAL_CHECK_EXPECTED_SESSION_TIMEOUT"))

        val deploymentCheckTask = tasks.register<Test>("externalDeploymentCheck") {
            description =
                "Executes src/externalTest against a deployed environment without provisioning Docker resources."
            group = "verification"
            dependsOn(tasks.named(externalTestSourceSet.classesTaskName))
            testClassesDirs = externalTestSourceSet.output.classesDirs
            classpath = externalTestSourceSet.runtimeClasspath
            shouldRunAfter(tasks.named("test"))
            useJUnitPlatform()
            outputs.upToDateWhen { false }
            doFirst {
                val baseUrl = deploymentBaseUrl.orNull?.trim()
                if (baseUrl.isNullOrBlank()) {
                    throw GradleException(
                        "externalDeploymentCheck requires externalCheck.baseUrl or EXTERNAL_CHECK_BASE_URL.",
                    )
                }
                val jdbcUrl = deploymentJdbcUrl.orNull?.trim()
                val jdbcUser = deploymentJdbcUser.orNull?.trim()
                val jdbcPassword = deploymentJdbcPassword.orNull?.trim()
                val anyJdbcConfigured = listOf(jdbcUrl, jdbcUser, jdbcPassword).any { !it.isNullOrBlank() }
                val allJdbcConfigured = listOf(jdbcUrl, jdbcUser, jdbcPassword).all { !it.isNullOrBlank() }
                if (anyJdbcConfigured && !allJdbcConfigured) {
                    throw GradleException(
                        "externalDeploymentCheck requires externalCheck.jdbcUrl, externalCheck.jdbcUser, and externalCheck.jdbcPassword together when JDBC-backed checks are enabled.",
                    )
                }
                val buildVersion = expectedBuildVersion.orNull?.trim()
                val shortCommitId = expectedShortCommitId.orNull?.trim()
                val anyIdentityConfigured = listOf(buildVersion, shortCommitId).any { !it.isNullOrBlank() }
                val allIdentityConfigured = listOf(buildVersion, shortCommitId).all { !it.isNullOrBlank() }
                if (anyIdentityConfigured && !allIdentityConfigured) {
                    throw GradleException(
                        "externalDeploymentCheck requires externalCheck.expectedBuildVersion and externalCheck.expectedShortCommitId together when release identity checks are enabled.",
                    )
                }
                val activeProfile = expectedActiveProfile.orNull?.trim()
                val sessionStoreType = expectedSessionStoreType.orNull?.trim()
                val sessionTimeout = expectedSessionTimeout.orNull?.trim()
                val anyRuntimeConfigured = listOf(activeProfile, sessionStoreType, sessionTimeout)
                    .any { !it.isNullOrBlank() }
                val allRuntimeConfigured = listOf(activeProfile, sessionStoreType, sessionTimeout)
                    .all { !it.isNullOrBlank() }
                if (anyRuntimeConfigured && !allRuntimeConfigured) {
                    throw GradleException(
                        "externalDeploymentCheck requires externalCheck.expectedActiveProfile, externalCheck.expectedSessionStoreType, and externalCheck.expectedSessionTimeout together when runtime posture checks are enabled.",
                    )
                }
                logger.lifecycle("[externalDeploymentCheck] Running deployed smoke assertions against {}.", baseUrl)
                systemProperty("app.baseUrl", baseUrl)
                systemProperty("external.baseUrl", baseUrl)
                environment("EXTERNAL_BASE_URL", baseUrl)
                if (allJdbcConfigured) {
                    logger.lifecycle(
                        "[externalDeploymentCheck] JDBC-backed session, GET /api/session, CSRF write, and Flyway checks are enabled.",
                    )
                    systemProperty("external.jdbc.url", jdbcUrl)
                    systemProperty("external.jdbc.user", jdbcUser)
                    systemProperty("external.jdbc.password", jdbcPassword)
                } else {
                    logger.lifecycle(
                        "[externalDeploymentCheck] JDBC-backed checks are disabled; running HTTP-only smoke assertions.",
                    )
                }
                if (allIdentityConfigured) {
                    val resolvedBuildVersion = requireNotNull(buildVersion)
                    val resolvedShortCommitId = requireNotNull(shortCommitId)
                    logger.lifecycle(
                        "[externalDeploymentCheck] Expecting deployed release identity build.version=$resolvedBuildVersion and git.shortCommitId=$resolvedShortCommitId.",
                    )
                    systemProperty("external.expected.buildVersion", resolvedBuildVersion)
                    systemProperty("external.expected.shortCommitId", resolvedShortCommitId)
                    environment("EXTERNAL_CHECK_EXPECTED_BUILD_VERSION", resolvedBuildVersion)
                    environment("EXTERNAL_CHECK_EXPECTED_SHORT_COMMIT_ID", resolvedShortCommitId)
                }
                if (allRuntimeConfigured) {
                    val resolvedActiveProfile = requireNotNull(activeProfile)
                    val resolvedSessionStoreType = requireNotNull(sessionStoreType)
                    val resolvedSessionTimeout = requireNotNull(sessionTimeout)
                    logger.lifecycle(
                        "[externalDeploymentCheck] Expecting prod posture profile=$resolvedActiveProfile, session store=$resolvedSessionStoreType, session timeout=$resolvedSessionTimeout, csrf enabled with XSRF-TOKEN/X-XSRF-TOKEN and edge-owned abuse protection.",
                    )
                    systemProperty("external.expected.activeProfile", resolvedActiveProfile)
                    systemProperty("external.expected.sessionStoreType", resolvedSessionStoreType)
                    systemProperty("external.expected.sessionTimeout", resolvedSessionTimeout)
                    environment("EXTERNAL_CHECK_EXPECTED_ACTIVE_PROFILE", resolvedActiveProfile)
                    environment("EXTERNAL_CHECK_EXPECTED_SESSION_STORE_TYPE", resolvedSessionStoreType)
                    environment("EXTERNAL_CHECK_EXPECTED_SESSION_TIMEOUT", resolvedSessionTimeout)
                }
            }
            extensions.configure(JacocoTaskExtension::class.java) {
                isEnabled = false
            }
        }

        tasks.register("scheduledExternalCheck") {
            group = "verification"
            description = "Alias for the deployed external smoke task used by scheduled post-deploy automation."
            dependsOn(deploymentCheckTask)
        }
    }

    private fun Project.registerExternalSmokeEnvironmentUpTask(
        dockerImageName: org.gradle.api.provider.Provider<String>,
        smokeHostPort: org.gradle.api.provider.Provider<Int>,
    ): TaskProvider<ExternalSmokeEnvironmentUpTask> = tasks.register<ExternalSmokeEnvironmentUpTask>(
        "externalSmokeEnvironmentUp",
    ) {
        group = "verification"
        description = "Starts Docker resources required for external smoke testing."
        dependsOn(tasks.named("dockerBuild"))
        imageName.convention(dockerImageName)
        postgresImage.convention(providers.gradleProperty("externalSmoke.postgresImage").orElse("postgres:16-alpine"))
        databaseName.convention(
            providers.gradleProperty("externalSmoke.databaseName").orElse("technical_interview_demo"),
        )
        databaseUser.convention(providers.gradleProperty("externalSmoke.databaseUser").orElse("postgres"))
        databasePassword.convention(providers.gradleProperty("externalSmoke.databasePassword").orElse("changeme"))
        networkName.convention(
            providers.gradleProperty("externalSmoke.networkName").orElse("technical-interview-demo-smoke-network"),
        )
        postgresContainerName.convention(
            providers.gradleProperty("externalSmoke.postgresContainerName")
                .orElse("technical-interview-demo-smoke-postgres"),
        )
        appContainerName.convention(
            providers.gradleProperty("externalSmoke.appContainerName").orElse("technical-interview-demo-smoke-app"),
        )
        hostPort.convention(smokeHostPort)
        postgresHostPort.convention(intProperty("externalSmoke.postgresHostPort", 15432))
        timeoutSeconds.convention(intProperty("externalSmoke.timeoutSeconds", 120))
    }

    private fun Project.registerExternalSmokeEnvironmentDownTask(): TaskProvider<ExternalSmokeEnvironmentDownTask> =
        tasks.register<ExternalSmokeEnvironmentDownTask>("externalSmokeEnvironmentDown") {
            group = "verification"
            description = "Stops Docker resources created for external smoke testing."
            networkName.convention(
                providers.gradleProperty("externalSmoke.networkName").orElse("technical-interview-demo-smoke-network"),
            )
            postgresContainerName.convention(
                providers.gradleProperty("externalSmoke.postgresContainerName")
                    .orElse("technical-interview-demo-smoke-postgres"),
            )
            appContainerName.convention(
                providers.gradleProperty("externalSmoke.appContainerName").orElse("technical-interview-demo-smoke-app"),
            )
        }

    private fun Project.configureGatlingBenchmark() {
        val dockerImageName = providers.gradleProperty("dockerImageName").orElse("technical-interview-demo")
        val defaultBaselinePath = defaultGatlingBenchmarkBaselinePath()
        tasks.register<GatlingBenchmarkTask>("gatlingBenchmark") {
            group = "verification"
            description = "Runs Docker-backed Gatling benchmarks for public reads and OAuth redirect startup."
            dependsOn(tasks.named("dockerBuild"))
            dependsOn(tasks.named("gatlingClasses"))
            gatlingRuntimeClasspath.from(configurations.named("gatlingRuntimeClasspath"))

            imageName.convention(dockerImageName)
            postgresImage.convention(providers.gradleProperty("benchmark.postgresImage").orElse("postgres:16-alpine"))
            databaseName.convention(
                providers.gradleProperty("benchmark.databaseName").orElse("technical_interview_demo"),
            )
            databaseUser.convention(providers.gradleProperty("benchmark.databaseUser").orElse("postgres"))
            databasePassword.convention(providers.gradleProperty("benchmark.databasePassword").orElse("changeme"))
            networkName.convention(
                providers.gradleProperty("benchmark.networkName").orElse("technical-interview-demo-benchmark-network"),
            )
            postgresContainerName.convention(
                providers.gradleProperty("benchmark.postgresContainerName")
                    .orElse("technical-interview-demo-benchmark-postgres"),
            )
            appContainerName.convention(
                providers.gradleProperty("benchmark.appContainerName")
                    .orElse("technical-interview-demo-benchmark-app"),
            )
            baseUrl.convention(providers.gradleProperty("benchmark.baseUrl").orElse("http://127.0.0.1:18080"))
            timeoutSeconds.convention(intProperty("benchmark.timeoutSeconds", 120))
            oauthClientId.convention(providers.gradleProperty("benchmark.githubClientId").orElse("benchmark-client-id"))
            oauthClientSecret.convention(
                providers.gradleProperty("benchmark.githubClientSecret").orElse("benchmark-client-secret"),
            )
            updateBaseline.convention(booleanProperty("benchmark.updateBaseline", false))
            responseTimeToleranceMultiplier.convention(
                doubleProperty("benchmark.responseTimeToleranceMultiplier", 1.25),
            )
            successRateTolerancePercentage.convention(
                doubleProperty("benchmark.successRateTolerancePercentage", 0.50),
            )
            baselineFile.convention(
                layout.projectDirectory.file(
                    providers.gradleProperty("benchmark.baselineFile")
                        .orElse(defaultBaselinePath),
                ),
            )
            latestResultFile.convention(layout.buildDirectory.file("performance/phase-9-latest.json"))
            reportDirectory.convention(layout.buildDirectory.dir("reports/gatling"))
        }
    }

    private fun Project.defaultGatlingBenchmarkBaselinePath() =
        extensions.getByType<SourceSetContainer>().named(GATLING_SOURCE_SET_NAME).map { gatlingSourceSet ->
            val resourceDirectories = gatlingSourceSet.resources.srcDirs
                .map { relativePath(it).replace('\\', '/') }
                .sorted()
            val resourceDirectory = resourceDirectories.singleOrNull()
                ?: throw GradleException(
                    "gatlingBenchmark requires exactly one Gatling resources directory " +
                        "to derive the default baseline path, but found: " +
                        resourceDirectories.ifEmpty { listOf("none") }.joinToString(", "),
                )
            "$resourceDirectory/$GATLING_BENCHMARK_BASELINE_FILE_NAME"
        }

    private fun Project.intProperty(name: String, defaultValue: Int) =
        providers.gradleProperty(name).map(String::toInt).orElse(defaultValue)

    private fun Project.doubleProperty(name: String, defaultValue: Double) =
        providers.gradleProperty(name).map(String::toDouble).orElse(defaultValue)

    private fun Project.booleanProperty(name: String, defaultValue: Boolean) =
        providers.gradleProperty(name).map { value ->
            when (value.trim().lowercase()) {
                "true" -> true
                "false" -> false
                else -> defaultValue
            }
        }.orElse(defaultValue)

    private companion object {
        const val EXTERNAL_TEST_SOURCE_SET_NAME: String = "externalTest"
        const val GATLING_SOURCE_SET_NAME: String = "gatling"
        const val GATLING_BENCHMARK_BASELINE_FILE_NAME: String = "gatling-benchmark-baseline.json"
    }
}
