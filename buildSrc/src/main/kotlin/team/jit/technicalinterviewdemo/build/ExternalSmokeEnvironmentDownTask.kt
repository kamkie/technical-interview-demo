package team.jit.technicalinterviewdemo.build

import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Stops Docker containers created for external smoke testing.")
abstract class ExternalSmokeEnvironmentDownTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {

    @get:Input
    abstract val networkName: Property<String>

    @get:Input
    abstract val postgresContainerName: Property<String>

    @get:Input
    abstract val appContainerName: Property<String>

    init {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun tearDownEnvironment() {
        val docker = DockerSupport(execOperations, logger)
        val environment = DockerApplicationEnvironment(docker, logger)
        val resources = DockerApplicationEnvironmentResources(
            logPrefix = "[externalSmokeTest]",
            networkName = networkName.get(),
            postgresContainerName = postgresContainerName.get(),
            appContainerName = appContainerName.get()
        )
        logger.lifecycle("[externalSmokeTest] Tearing down Docker smoke environment.")
        environment.cleanup(resources)
    }
}
