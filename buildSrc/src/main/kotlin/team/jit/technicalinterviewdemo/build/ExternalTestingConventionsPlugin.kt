package team.jit.technicalinterviewdemo.build

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
        val smokeBaseUrl = providers.gradleProperty("externalSmoke.baseUrl")
            .orElse(smokeHostPort.map { "http://127.0.0.1:$it" })
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
    }

    private fun Project.registerExternalSmokeEnvironmentUpTask(
        dockerImageName: org.gradle.api.provider.Provider<String>,
        smokeHostPort: org.gradle.api.provider.Provider<Int>
    ): TaskProvider<ExternalSmokeEnvironmentUpTask> = tasks.register<ExternalSmokeEnvironmentUpTask>(
        "externalSmokeEnvironmentUp"
    ) {
        group = "verification"
        description = "Starts Docker resources required for external smoke testing."
        dependsOn(tasks.named("dockerBuild"))
        imageName.convention(dockerImageName)
        postgresImage.convention(providers.gradleProperty("externalSmoke.postgresImage").orElse("postgres:16-alpine"))
        databaseName.convention(providers.gradleProperty("externalSmoke.databaseName").orElse("technical_interview_demo"))
        databaseUser.convention(providers.gradleProperty("externalSmoke.databaseUser").orElse("postgres"))
        databasePassword.convention(providers.gradleProperty("externalSmoke.databasePassword").orElse("changeme"))
        networkName.convention(
            providers.gradleProperty("externalSmoke.networkName").orElse("technical-interview-demo-smoke-network")
        )
        postgresContainerName.convention(
            providers.gradleProperty("externalSmoke.postgresContainerName")
                .orElse("technical-interview-demo-smoke-postgres")
        )
        appContainerName.convention(
            providers.gradleProperty("externalSmoke.appContainerName").orElse("technical-interview-demo-smoke-app")
        )
        hostPort.convention(smokeHostPort)
        timeoutSeconds.convention(intProperty("externalSmoke.timeoutSeconds", 120))
    }

    private fun Project.registerExternalSmokeEnvironmentDownTask(): TaskProvider<ExternalSmokeEnvironmentDownTask> =
        tasks.register<ExternalSmokeEnvironmentDownTask>("externalSmokeEnvironmentDown") {
            group = "verification"
            description = "Stops Docker resources created for external smoke testing."
            networkName.convention(
                providers.gradleProperty("externalSmoke.networkName").orElse("technical-interview-demo-smoke-network")
            )
            postgresContainerName.convention(
                providers.gradleProperty("externalSmoke.postgresContainerName")
                    .orElse("technical-interview-demo-smoke-postgres")
            )
            appContainerName.convention(
                providers.gradleProperty("externalSmoke.appContainerName").orElse("technical-interview-demo-smoke-app")
            )
        }

    private fun Project.configureGatlingBenchmark() {
        val dockerImageName = providers.gradleProperty("dockerImageName").orElse("technical-interview-demo")
        tasks.register<GatlingBenchmarkTask>("gatlingBenchmark") {
            group = "verification"
            description = "Runs Docker-backed Gatling benchmarks for public reads and OAuth redirect startup."
            dependsOn(tasks.named("dockerBuild"))
            dependsOn(tasks.named("gatlingClasses"))
            gatlingRuntimeClasspath.from(configurations.named("gatlingRuntimeClasspath"))

            imageName.convention(dockerImageName)
            postgresImage.convention(providers.gradleProperty("benchmark.postgresImage").orElse("postgres:16-alpine"))
            databaseName.convention(providers.gradleProperty("benchmark.databaseName").orElse("technical_interview_demo"))
            databaseUser.convention(providers.gradleProperty("benchmark.databaseUser").orElse("postgres"))
            databasePassword.convention(providers.gradleProperty("benchmark.databasePassword").orElse("changeme"))
            networkName.convention(
                providers.gradleProperty("benchmark.networkName").orElse("technical-interview-demo-benchmark-network")
            )
            postgresContainerName.convention(
                providers.gradleProperty("benchmark.postgresContainerName")
                    .orElse("technical-interview-demo-benchmark-postgres")
            )
            appContainerName.convention(
                providers.gradleProperty("benchmark.appContainerName")
                    .orElse("technical-interview-demo-benchmark-app")
            )
            baseUrl.convention(providers.gradleProperty("benchmark.baseUrl").orElse("http://127.0.0.1:18080"))
            timeoutSeconds.convention(intProperty("benchmark.timeoutSeconds", 120))
            oauthClientId.convention(providers.gradleProperty("benchmark.githubClientId").orElse("benchmark-client-id"))
            oauthClientSecret.convention(
                providers.gradleProperty("benchmark.githubClientSecret").orElse("benchmark-client-secret")
            )
            updateBaseline.convention(booleanProperty("benchmark.updateBaseline", false))
            responseTimeToleranceMultiplier.convention(
                doubleProperty("benchmark.responseTimeToleranceMultiplier", 1.20)
            )
            successRateTolerancePercentage.convention(
                doubleProperty("benchmark.successRateTolerancePercentage", 0.50)
            )
            baselineFile.convention(
                layout.projectDirectory.file(
                    providers.gradleProperty("benchmark.baselineFile").orElse("performance/baselines/phase-9-local.json")
                )
            )
            latestResultFile.convention(layout.buildDirectory.file("performance/phase-9-latest.json"))
            reportDirectory.convention(layout.buildDirectory.dir("reports/gatling"))
        }
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
    }
}
