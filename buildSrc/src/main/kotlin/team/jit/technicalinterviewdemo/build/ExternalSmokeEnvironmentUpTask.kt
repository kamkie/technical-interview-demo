package team.jit.technicalinterviewdemo.build

import java.time.Duration
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Starts Docker containers required for external smoke testing.")
abstract class ExternalSmokeEnvironmentUpTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {

    @get:Input
    abstract val imageName: Property<String>

    @get:Input
    abstract val postgresImage: Property<String>

    @get:Input
    abstract val databaseName: Property<String>

    @get:Input
    abstract val databaseUser: Property<String>

    @get:Input
    abstract val databasePassword: Property<String>

    @get:Input
    abstract val networkName: Property<String>

    @get:Input
    abstract val postgresContainerName: Property<String>

    @get:Input
    abstract val appContainerName: Property<String>

    @get:Input
    abstract val hostPort: Property<Int>

    @get:Input
    abstract val postgresHostPort: Property<Int>

    @get:Input
    abstract val timeoutSeconds: Property<Int>

    init {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun provisionEnvironment() {
        val docker = DockerSupport(execOperations, logger)
        val environment = DockerApplicationEnvironment(docker, logger)
        val timeout = Duration.ofSeconds(timeoutSeconds.get().toLong())
        val smokeBaseUrl = "http://127.0.0.1:${hostPort.get()}"
        val resources = DockerApplicationEnvironmentResources(
            logPrefix = "[externalSmokeTest]",
            networkName = networkName.get(),
            postgresContainerName = postgresContainerName.get(),
            appContainerName = appContainerName.get()
        )
        val config = DockerApplicationEnvironmentConfig(
            resources = resources,
            imageName = imageName.get(),
            postgresImage = postgresImage.get(),
            databaseName = databaseName.get(),
            databaseUser = databaseUser.get(),
            databasePassword = databasePassword.get(),
            appHostPort = hostPort.get(),
            springProfiles = "prod",
            postgresHostPort = postgresHostPort.get()
        )

        environment.cleanup(resources)

        try {
            environment.provision(config, smokeBaseUrl, timeout)
            environment.verifyFlyway(config)
            logger.lifecycle("[externalSmokeTest] Docker smoke environment is ready.")
        } catch (exception: Exception) {
            logger.error("[externalSmokeTest] Failed to provision smoke environment.")
            environment.logContainerLogs(resources)
            environment.cleanup(resources)
            throw exception
        }
    }
}
