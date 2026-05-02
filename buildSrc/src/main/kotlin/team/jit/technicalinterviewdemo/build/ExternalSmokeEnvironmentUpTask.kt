package team.jit.technicalinterviewdemo.build

import java.time.Duration
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
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
        val timeout = Duration.ofSeconds(timeoutSeconds.get().toLong())
        val smokeBaseUrl = "http://127.0.0.1:${hostPort.get()}"

        cleanup(docker)

        try {
            logger.lifecycle("[externalSmokeTest] Provisioning Docker network '{}'.", networkName.get())
            docker.docker("network", "create", networkName.get())

            logger.lifecycle("[externalSmokeTest] Starting PostgreSQL container '{}'.", postgresContainerName.get())
            docker.docker(
                "run",
                "--detach",
                "--name",
                postgresContainerName.get(),
                "--network",
                networkName.get(),
                "--publish",
                "${postgresHostPort.get()}:5432",
                "--env",
                "POSTGRES_DB=${databaseName.get()}",
                "--env",
                "POSTGRES_USER=${databaseUser.get()}",
                "--env",
                "POSTGRES_PASSWORD=${databasePassword.get()}",
                postgresImage.get()
            )

            logger.lifecycle("[externalSmokeTest] Waiting for PostgreSQL readiness.")
            docker.waitUntil(timeout, "PostgreSQL readiness") {
                docker.docker(
                    "exec",
                    postgresContainerName.get(),
                    "pg_isready",
                    "-U",
                    databaseUser.get(),
                    "-d",
                    databaseName.get(),
                    allowFailure = true
                ).exitCode == 0
            }

            logger.lifecycle("[externalSmokeTest] Starting application container '{}'.", appContainerName.get())
            docker.docker(
                "run",
                "--detach",
                "--name",
                appContainerName.get(),
                "--network",
                networkName.get(),
                "--publish",
                "${hostPort.get()}:8080",
                "--env",
                "SPRING_PROFILES_ACTIVE=prod",
                "--env",
                "DATABASE_HOST=${postgresContainerName.get()}",
                "--env",
                "DATABASE_PORT=5432",
                "--env",
                "DATABASE_NAME=${databaseName.get()}",
                "--env",
                "DATABASE_USER=${databaseUser.get()}",
                "--env",
                "DATABASE_PASSWORD=${databasePassword.get()}",
                imageName.get()
            )

            logger.lifecycle("[externalSmokeTest] Waiting for readiness at {}.", smokeBaseUrl)
            docker.waitForReadiness(smokeBaseUrl, timeout)

            logger.lifecycle("[externalSmokeTest] Verifying Flyway migration state.")
            val flywayTableCount = docker.docker(
                "exec",
                postgresContainerName.get(),
                "psql",
                "-U",
                databaseUser.get(),
                "-d",
                databaseName.get(),
                "-tAc",
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'flyway_schema_history';"
            ).stdout.trim()
            if (flywayTableCount != "1") {
                throw GradleException("Flyway schema history table was not created in PostgreSQL.")
            }

            val flywaySuccessCount = docker.docker(
                "exec",
                postgresContainerName.get(),
                "psql",
                "-U",
                databaseUser.get(),
                "-d",
                databaseName.get(),
                "-tAc",
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true;"
            ).stdout.trim().toIntOrNull() ?: 0
            if (flywaySuccessCount < 1) {
                throw GradleException("Flyway did not record successful migrations.")
            }

            logger.lifecycle("[externalSmokeTest] Docker smoke environment is ready.")
        } catch (exception: Exception) {
            logger.error("[externalSmokeTest] Failed to provision smoke environment.")
            docker.logContainerLogs(postgresContainerName.get(), "[externalSmokeTest]")
            docker.logContainerLogs(appContainerName.get(), "[externalSmokeTest]")
            cleanup(docker)
            throw exception
        }
    }

    private fun cleanup(docker: DockerSupport) {
        docker.removeDockerResource("rm", "-f", appContainerName.get())
        docker.removeDockerResource("rm", "-f", postgresContainerName.get())
        docker.removeDockerResource("network", "rm", networkName.get())
    }
}
