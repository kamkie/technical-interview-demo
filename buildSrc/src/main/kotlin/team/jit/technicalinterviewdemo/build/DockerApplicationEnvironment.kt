package team.jit.technicalinterviewdemo.build

import java.time.Duration
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger

internal data class DockerApplicationEnvironmentResources(
    val logPrefix: String,
    val networkName: String,
    val postgresContainerName: String,
    val appContainerName: String
)

internal data class DockerApplicationEnvironmentConfig(
    val resources: DockerApplicationEnvironmentResources,
    val imageName: String,
    val postgresImage: String,
    val databaseName: String,
    val databaseUser: String,
    val databasePassword: String,
    val appHostPort: Int,
    val springProfiles: String,
    val postgresHostPort: Int? = null,
    val appEnvironment: Map<String, String> = emptyMap(),
    val applicationDescription: String = "application"
)

internal class DockerApplicationEnvironment(
    private val docker: DockerSupport,
    private val logger: Logger
) {

    fun provision(config: DockerApplicationEnvironmentConfig, readinessBaseUrl: String, timeout: Duration) {
        val resources = config.resources
        logger.lifecycle("{} Provisioning Docker network '{}'.", resources.logPrefix, resources.networkName)
        docker.docker("network", "create", resources.networkName)

        startPostgres(config)
        waitForPostgres(config, timeout)
        startApplication(config)
        logger.lifecycle("{} Waiting for application readiness at {}.", resources.logPrefix, readinessBaseUrl)
        docker.waitForReadiness(readinessBaseUrl, timeout)
    }

    fun verifyFlyway(config: DockerApplicationEnvironmentConfig) {
        val resources = config.resources
        logger.lifecycle("{} Verifying Flyway migration state.", resources.logPrefix)
        val flywayTableCount = docker.docker(
            "exec",
            resources.postgresContainerName,
            "psql",
            "-U",
            config.databaseUser,
            "-d",
            config.databaseName,
            "-tAc",
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'flyway_schema_history';"
        ).stdout.trim()
        if (flywayTableCount != "1") {
            throw GradleException("Flyway schema history table was not created in PostgreSQL.")
        }

        val flywaySuccessCount = docker.docker(
            "exec",
            resources.postgresContainerName,
            "psql",
            "-U",
            config.databaseUser,
            "-d",
            config.databaseName,
            "-tAc",
            "SELECT COUNT(*) FROM flyway_schema_history WHERE success = true;"
        ).stdout.trim().toIntOrNull() ?: 0
        if (flywaySuccessCount < 1) {
            throw GradleException("Flyway did not record successful migrations.")
        }
    }

    fun logContainerLogs(resources: DockerApplicationEnvironmentResources) {
        docker.logContainerLogs(resources.postgresContainerName, resources.logPrefix)
        docker.logContainerLogs(resources.appContainerName, resources.logPrefix)
    }

    fun cleanup(resources: DockerApplicationEnvironmentResources) {
        docker.removeDockerResource("rm", "-f", resources.appContainerName)
        docker.removeDockerResource("rm", "-f", resources.postgresContainerName)
        docker.removeDockerResource("network", "rm", resources.networkName)
    }

    private fun startPostgres(config: DockerApplicationEnvironmentConfig) {
        val resources = config.resources
        logger.lifecycle("{} Starting PostgreSQL container '{}'.", resources.logPrefix, resources.postgresContainerName)
        val args = mutableListOf(
            "run",
            "--detach",
            "--name",
            resources.postgresContainerName,
            "--network",
            resources.networkName
        )
        config.postgresHostPort?.let { hostPort ->
            args += listOf("--publish", "$hostPort:5432")
        }
        args += environmentArgs(
            mapOf(
                "POSTGRES_DB" to config.databaseName,
                "POSTGRES_USER" to config.databaseUser,
                "POSTGRES_PASSWORD" to config.databasePassword
            )
        )
        args += config.postgresImage
        docker.docker(*args.toTypedArray())
    }

    private fun waitForPostgres(config: DockerApplicationEnvironmentConfig, timeout: Duration) {
        val resources = config.resources
        logger.lifecycle("{} Waiting for PostgreSQL readiness.", resources.logPrefix)
        docker.waitUntil(timeout, "PostgreSQL readiness") {
            docker.docker(
                "exec",
                resources.postgresContainerName,
                "pg_isready",
                "-U",
                config.databaseUser,
                "-d",
                config.databaseName,
                allowFailure = true
            ).exitCode == 0
        }
    }

    private fun startApplication(config: DockerApplicationEnvironmentConfig) {
        val resources = config.resources
        logger.lifecycle(
            "{} Starting {} container '{}'.",
            resources.logPrefix,
            config.applicationDescription,
            resources.appContainerName
        )
        val appEnvironment = linkedMapOf(
            "SPRING_PROFILES_ACTIVE" to config.springProfiles,
            "DATABASE_HOST" to resources.postgresContainerName,
            "DATABASE_PORT" to "5432",
            "DATABASE_NAME" to config.databaseName,
            "DATABASE_USER" to config.databaseUser,
            "DATABASE_PASSWORD" to config.databasePassword
        )
        appEnvironment.putAll(config.appEnvironment)

        val args = mutableListOf(
            "run",
            "--detach",
            "--name",
            resources.appContainerName,
            "--network",
            resources.networkName,
            "--publish",
            "${config.appHostPort}:8080"
        )
        args += environmentArgs(appEnvironment)
        args += config.imageName
        docker.docker(*args.toTypedArray())
    }

    private fun environmentArgs(environment: Map<String, String>): List<String> =
        environment.flatMap { (key, value) -> listOf("--env", "$key=$value") }
}
