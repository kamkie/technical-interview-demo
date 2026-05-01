package team.jit.technicalinterviewdemo.build

import java.io.ByteArrayOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.Instant
import java.util.Locale
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.process.ExecOperations

internal data class CommandResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String
)

internal class DockerSupport(
    private val execOperations: ExecOperations,
    private val logger: Logger
) {

    fun docker(vararg args: String, allowFailure: Boolean = false): CommandResult =
        command(listOf("docker", *args), allowFailure)

    fun command(command: List<String>, allowFailure: Boolean = false): CommandResult {
        val stdout = ByteArrayOutputStream()
        val stderr = ByteArrayOutputStream()
        val execResult = execOperations.exec {
            commandLine(command)
            isIgnoreExitValue = true
            standardOutput = stdout
            errorOutput = stderr
        }
        val result = CommandResult(
            exitCode = execResult.exitValue,
            stdout = stdout.toString(Charsets.UTF_8),
            stderr = stderr.toString(Charsets.UTF_8)
        )
        if (!allowFailure && result.exitCode != 0) {
            throw GradleException(
                buildString {
                    appendLine("Command failed (${result.exitCode}): ${command.joinToString(" ")}")
                    if (result.stdout.isNotBlank()) {
                        appendLine("stdout:")
                        appendLine(result.stdout.trimEnd())
                    }
                    if (result.stderr.isNotBlank()) {
                        appendLine("stderr:")
                        appendLine(result.stderr.trimEnd())
                    }
                }.trimEnd()
            )
        }
        return result
    }

    fun removeDockerResource(vararg args: String) {
        docker(*args, allowFailure = true)
    }

    fun waitUntil(
        timeout: Duration,
        description: String,
        pollInterval: Duration = Duration.ofSeconds(2),
        condition: () -> Boolean
    ) {
        val deadline = Instant.now().plus(timeout)
        while (Instant.now().isBefore(deadline)) {
            if (condition()) {
                return
            }
            Thread.sleep(pollInterval.toMillis())
        }
        throw GradleException("Timed out waiting for $description within ${timeout.seconds} seconds.")
    }

    fun waitForReadiness(baseUrl: String, timeout: Duration) {
        val readinessUri = URI.create("${baseUrl.trimEnd('/')}/actuator/health/readiness")
        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build()
        waitUntil(timeout, "application readiness at $readinessUri") {
            try {
                val request = HttpRequest.newBuilder(readinessUri)
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build()
                val response = client.send(request, HttpResponse.BodyHandlers.ofString())
                val body = response.body().lowercase(Locale.ROOT)
                response.statusCode() == 200 && body.contains("\"status\"") && body.contains("up")
            } catch (_: Exception) {
                false
            }
        }
    }

    fun portIsBusy(host: String, port: Int): Boolean = try {
        Socket().use { socket ->
            socket.connect(InetSocketAddress(host, port), 500)
            true
        }
    } catch (_: Exception) {
        false
    }

    fun containerLogs(containerName: String): String {
        val inspect = docker("ps", "-a", "--format", "{{.Names}}", allowFailure = true)
        if (!inspect.stdout.lines().any { it.trim() == containerName }) {
            return ""
        }
        val logs = docker("logs", containerName, allowFailure = true)
        return (logs.stdout + logs.stderr).trim()
    }

    fun logContainerLogs(containerName: String, prefix: String) {
        val logs = containerLogs(containerName)
        if (logs.isNotBlank()) {
            logger.lifecycle("$prefix logs for {}:\n{}", containerName, logs)
        }
    }
}
