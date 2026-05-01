package team.jit.technicalinterviewdemo.build

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.net.URI
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlin.math.roundToInt
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Runs Docker-backed Gatling benchmarks and compares runtime measurements.")
abstract class GatlingBenchmarkTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {

    @get:Classpath
    abstract val gatlingRuntimeClasspath: ConfigurableFileCollection

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
    abstract val baseUrl: Property<String>

    @get:Input
    abstract val timeoutSeconds: Property<Int>

    @get:Input
    abstract val oauthClientId: Property<String>

    @get:Input
    abstract val oauthClientSecret: Property<String>

    @get:Input
    abstract val updateBaseline: Property<Boolean>

    @get:Input
    abstract val responseTimeToleranceMultiplier: Property<Double>

    @get:Input
    abstract val successRateTolerancePercentage: Property<Double>

    @get:InputFile
    @get:Optional
    abstract val baselineFile: RegularFileProperty

    @get:OutputFile
    abstract val latestResultFile: RegularFileProperty

    @get:OutputDirectory
    abstract val reportDirectory: DirectoryProperty

    init {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun runBenchmarks() {
        val docker = DockerSupport(execOperations, logger)
        val timeout = Duration.ofSeconds(timeoutSeconds.get().toLong())
        val normalizedBaseUrl = baseUrl.get().trimEnd('/')
        val baseUri = URI.create(normalizedBaseUrl)
        val host = baseUri.host ?: "127.0.0.1"
        val port = when {
            baseUri.port != -1 -> baseUri.port
            baseUri.scheme.equals("https", ignoreCase = true) -> 443
            else -> 80
        }

        if (docker.portIsBusy(host, port)) {
            throw GradleException(
                "Port $port on host '$host' is already in use. Stop the conflicting process before running gatlingBenchmark."
            )
        }

        cleanup(docker)
        try {
            logger.lifecycle("[gatlingBenchmark] Provisioning Docker network '{}'.", networkName.get())
            docker.docker("network", "create", networkName.get())

            logger.lifecycle("[gatlingBenchmark] Starting PostgreSQL container '{}'.", postgresContainerName.get())
            docker.docker(
                "run",
                "--detach",
                "--name",
                postgresContainerName.get(),
                "--network",
                networkName.get(),
                "--env",
                "POSTGRES_DB=${databaseName.get()}",
                "--env",
                "POSTGRES_USER=${databaseUser.get()}",
                "--env",
                "POSTGRES_PASSWORD=${databasePassword.get()}",
                postgresImage.get()
            )

            logger.lifecycle("[gatlingBenchmark] Waiting for PostgreSQL readiness.")
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

            logger.lifecycle("[gatlingBenchmark] Starting benchmark application container '{}'.", appContainerName.get())
            docker.docker(
                "run",
                "--detach",
                "--name",
                appContainerName.get(),
                "--network",
                networkName.get(),
                "--publish",
                "${port}:8080",
                "--env",
                "SPRING_PROFILES_ACTIVE=local,oauth",
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
                "--env",
                "GITHUB_CLIENT_ID=${oauthClientId.get()}",
                "--env",
                "GITHUB_CLIENT_SECRET=${oauthClientSecret.get()}",
                "--env",
                "SESSION_COOKIE_SECURE=false",
                imageName.get()
            )

            logger.lifecycle("[gatlingBenchmark] Waiting for application readiness at {}.", normalizedBaseUrl)
            docker.waitForReadiness(normalizedBaseUrl, timeout)

            val publicReportDirectory = runSimulation(
                simulationClassName = PUBLIC_API_SIMULATION_CLASS,
                reportLabel = "PublicApiSimulation",
                baseUrlValue = normalizedBaseUrl
            )
            val oauthRedirectReportDirectory = runSimulation(
                simulationClassName = AUTH_REDIRECT_SIMULATION_CLASS,
                reportLabel = "AuthenticationRedirectSimulation",
                baseUrlValue = normalizedBaseUrl
            )

            val benchmarkResult = captureBenchmarkResult(publicReportDirectory, oauthRedirectReportDirectory, normalizedBaseUrl, docker)
            writeResult(latestResultFile.get().asFile, benchmarkResult)
            logger.lifecycle(
                "[gatlingBenchmark] Latest benchmark result written to {}.",
                latestResultFile.get().asFile.absolutePath
            )

            decideBaseline(benchmarkResult)
        } catch (exception: Exception) {
            logger.error("[gatlingBenchmark] Benchmark run failed.")
            docker.logContainerLogs(postgresContainerName.get(), "[gatlingBenchmark]")
            docker.logContainerLogs(appContainerName.get(), "[gatlingBenchmark]")
            throw exception
        } finally {
            logger.lifecycle("[gatlingBenchmark] Tearing down Docker benchmark environment.")
            cleanup(docker)
        }
    }

    private fun runSimulation(simulationClassName: String, reportLabel: String, baseUrlValue: String): File {
        val previousReports = listReportDirectories().map(File::getAbsolutePath).toSet()
        logger.lifecycle("[gatlingBenchmark] Starting simulation {}.", reportLabel)

        val executionResult = execOperations.javaexec {
            mainClass.set(GATLING_MAIN_CLASS)
            classpath = gatlingRuntimeClasspath
            systemProperty("app.baseUrl", baseUrlValue)
            jvmArgs(
                "--enable-native-access=ALL-UNNAMED",
                "--add-opens=java.base/java.lang=ALL-UNNAMED",
                "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
                "--add-opens=java.base/java.io=ALL-UNNAMED",
                "-Xshare:off"
            )
            args(
                "-s",
                simulationClassName,
                "-rf",
                reportDirectory.get().asFile.absolutePath,
                "-rd",
                "gatlingBenchmark"
            )
            isIgnoreExitValue = true
        }

        when (executionResult.exitValue) {
            0 -> {
            }

            2 -> throw GradleException("Gatling assertions failed for simulation '$simulationClassName'.")
            else -> throw GradleException(
                "Gatling simulation '$simulationClassName' crashed with exit code ${executionResult.exitValue}."
            )
        }

        val newReports = listReportDirectories()
            .filterNot { previousReports.contains(it.absolutePath) }
            .sortedByDescending(File::lastModified)
        val reportDirectoryFile = newReports.firstOrNull()
            ?: throw GradleException("Could not find the report directory generated for simulation '$simulationClassName'.")
        logger.lifecycle(
            "[gatlingBenchmark] Finished simulation {}. Report directory: {}",
            reportLabel,
            reportDirectoryFile.absolutePath
        )
        return reportDirectoryFile
    }

    private fun listReportDirectories(): List<File> {
        val rootDirectory = reportDirectory.get().asFile
        if (!rootDirectory.exists()) {
            return emptyList()
        }
        return rootDirectory.listFiles()?.filter { it.isDirectory } ?: emptyList()
    }

    private fun captureBenchmarkResult(
        publicReportDirectory: File,
        oauthRedirectReportDirectory: File,
        normalizedBaseUrl: String,
        docker: DockerSupport
    ): BenchmarkBaseline {
        val publicIndex = File(publicReportDirectory, "index.html")
        val oauthRedirectIndex = File(oauthRedirectReportDirectory, "index.html")

        val requests = listOf(
            requestStats(publicIndex, "list-books"),
            requestStats(publicIndex, "search-books"),
            requestStats(publicIndex, "lookup-localization-message", aliases = listOf("filter-localizations")),
            requestStats(oauthRedirectIndex, "oauth2-github-redirect")
        )

        val gitCommit = docker.command(listOf("git", "rev-parse", "HEAD"), allowFailure = true)
            .stdout
            .trim()
            .ifBlank { "unknown" }

        return BenchmarkBaseline(
            capturedAt = Instant.now().toString(),
            gitCommit = gitCommit,
            baseUrl = normalizedBaseUrl,
            javaVersion = System.getProperty("java.runtime.version"),
            activeProfiles = listOf("local", "oauth"),
            simulations = listOf(
                BenchmarkSimulation(
                    simulation = "PublicApiSimulation",
                    description = "List books, search books, and localization lookup against the public API.",
                    injectionProfile = "5 users at once, ramp 1->6 users/s for 20s, then hold 6 users/s for 20s"
                ),
                BenchmarkSimulation(
                    simulation = "AuthenticationRedirectSimulation",
                    description = "Start the GitHub OAuth redirect flow without following the redirect.",
                    injectionProfile = "3 users at once, ramp 1->4 users/s for 15s, then hold 4 users/s for 15s"
                )
            ),
            requests = requests,
            notes = listOf(
                "This benchmark uses the packaged Docker image and a Dockerized PostgreSQL container.",
                "The OAuth benchmark uses fake GitHub credentials because only client registration is needed for redirects.",
                "AuthenticatedUserProfileSimulation remains excluded from automation because it requires a real authenticated session cookie."
            )
        )
    }

    private fun requestStats(indexFile: File, requestName: String, aliases: List<String> = emptyList()): BenchmarkRequestStats {
        val allNames = listOf(requestName) + aliases
        val indexContent = indexFile.readText()
        for (candidate in allNames) {
            val parsedStats = parseRequestStats(indexContent, candidate)
            if (parsedStats != null) {
                return if (candidate == requestName) {
                    parsedStats
                } else {
                    parsedStats.copy(requestName = requestName)
                }
            }
        }
        throw GradleException("Could not parse request '$requestName' from ${indexFile.absolutePath}.")
    }

    private fun parseRequestStats(indexContent: String, requestName: String): BenchmarkRequestStats? {
        val escapedRequestName = Regex.escape(requestName)
        val rowPattern = Regex(
            "(?s)<tr id=\"req_[^\"]+\" data-parent=\"ROOT\">.*?<span[^>]*class=\"ellipsed-name\">$escapedRequestName</span>.*?</tr>"
        )
        val rowMatch = rowPattern.find(indexContent) ?: return null

        val cellsPattern = Regex("<td class=\"value [^\"]+ col-(\\d+)\">([^<]+)</td>")
        val valuesByColumn = mutableMapOf<String, String>()
        cellsPattern.findAll(rowMatch.value).forEach { match ->
            valuesByColumn[match.groupValues[1]] = match.groupValues[2]
        }

        val requiredColumns = listOf("2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14")
        if (!requiredColumns.all { valuesByColumn.containsKey(it) }) {
            return null
        }

        val requestCount = parseInt(valuesByColumn.getValue("2"))
        val okCount = parseInt(valuesByColumn.getValue("3"))
        val koCount = parseInt(valuesByColumn.getValue("4"))
        val successfulRequestPercentage = if (requestCount == 0) {
            100.0
        } else {
            ((okCount * 100.0) / requestCount.toDouble())
        }

        return BenchmarkRequestStats(
            requestName = requestName,
            requestCount = requestCount,
            okCount = okCount,
            koCount = koCount,
            koPercentage = parseDouble(valuesByColumn.getValue("5")),
            successfulRequestPercentage = successfulRequestPercentage,
            countPerSecond = parseDouble(valuesByColumn.getValue("6")),
            minMs = parseInt(valuesByColumn.getValue("7")),
            p50Ms = parseInt(valuesByColumn.getValue("8")),
            p75Ms = parseInt(valuesByColumn.getValue("9")),
            p95Ms = parseInt(valuesByColumn.getValue("10")),
            p99Ms = parseInt(valuesByColumn.getValue("11")),
            maxMs = parseInt(valuesByColumn.getValue("12")),
            meanMs = parseInt(valuesByColumn.getValue("13")),
            stdDevMs = parseInt(valuesByColumn.getValue("14"))
        )
    }

    private fun parseInt(value: String): Int = value
        .replace("%", "")
        .replace(",", "")
        .trim()
        .toDouble()
        .roundToInt()

    private fun parseDouble(value: String): Double = value
        .replace("%", "")
        .replace(",", "")
        .trim()
        .toDouble()

    private fun decideBaseline(currentResult: BenchmarkBaseline) {
        val baselineFilePath = baselineFile.asFile.orNull
            ?: throw GradleException("A baseline file path is required for gatlingBenchmark.")

        if (updateBaseline.get()) {
            writeResult(baselineFilePath, currentResult)
            logger.lifecycle(
                "[gatlingBenchmark] Baseline decision: update requested, baseline overwritten at {}.",
                baselineFilePath.absolutePath
            )
            return
        }

        if (!baselineFilePath.exists()) {
            throw GradleException(
                "Baseline file not found at ${baselineFilePath.absolutePath}. Run with -Pbenchmark.updateBaseline=true to create it."
            )
        }

        val previousResult: BenchmarkBaseline = OBJECT_MAPPER.readValue(baselineFilePath)
        val previousRequestsByName = previousResult.requests.associateBy(BenchmarkRequestStats::requestName)
        val failures = mutableListOf<String>()

        currentResult.requests.forEach { current ->
            val previous = previousRequestsByName[current.requestName]
            if (previous == null) {
                val message = "No baseline entry exists for request '${current.requestName}'."
                failures += message
                logger.lifecycle("[gatlingBenchmark] Baseline decision {}: {}", current.requestName, message)
                return@forEach
            }

            val maxAllowedP95 = (previous.p95Ms.toDouble() * responseTimeToleranceMultiplier.get()).roundToInt()
            val minAllowedSuccess = previous.successfulRequestPercentage - successRateTolerancePercentage.get()
            val p95Pass = current.p95Ms <= maxAllowedP95
            val successPass = current.successfulRequestPercentage >= minAllowedSuccess

            if (p95Pass && successPass) {
                val currentSuccess = "%.2f".format(current.successfulRequestPercentage)
                val minSuccess = "%.2f".format(minAllowedSuccess)
                logger.lifecycle(
                    "[gatlingBenchmark] Baseline decision {}: PASS (p95 {}ms <= {}ms, success {}% >= {}%).",
                    current.requestName,
                    current.p95Ms,
                    maxAllowedP95,
                    currentSuccess,
                    minSuccess
                )
            } else {
                val failure = buildString {
                    append("Request '${current.requestName}' regressed")
                    if (!p95Pass) {
                        append(" (p95 ${current.p95Ms}ms > ${maxAllowedP95}ms)")
                    }
                    if (!successPass) {
                        append(" (success ${"%.2f".format(current.successfulRequestPercentage)}% < ")
                        append("${"%.2f".format(minAllowedSuccess)}%)")
                    }
                }
                failures += failure
                logger.lifecycle("[gatlingBenchmark] Baseline decision {}: {}", current.requestName, failure)
            }
        }

        if (failures.isNotEmpty()) {
            throw GradleException(
                "Benchmark regression detected:\n${failures.joinToString(separator = "\n") { "- $it" }}\n" +
                    "Run with -Pbenchmark.updateBaseline=true only after intentional baseline review."
            )
        }

        logger.lifecycle("[gatlingBenchmark] Baseline decision: current run is within allowed tolerance; baseline unchanged.")
    }

    private fun writeResult(file: File, content: BenchmarkBaseline) {
        file.parentFile.mkdirs()
        OBJECT_MAPPER.writeValue(file, content)
    }

    private fun cleanup(docker: DockerSupport) {
        docker.removeDockerResource("rm", "-f", appContainerName.get())
        docker.removeDockerResource("rm", "-f", postgresContainerName.get())
        docker.removeDockerResource("network", "rm", networkName.get())
    }

    private companion object {
        const val GATLING_MAIN_CLASS: String = "io.gatling.app.Gatling"
        const val PUBLIC_API_SIMULATION_CLASS: String = "team.jit.technicalinterviewdemo.performance.PublicApiSimulation"
        const val AUTH_REDIRECT_SIMULATION_CLASS: String =
            "team.jit.technicalinterviewdemo.performance.AuthenticationRedirectSimulation"

        val OBJECT_MAPPER = jacksonObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }
}

internal data class BenchmarkBaseline(
    val capturedAt: String,
    val gitCommit: String,
    val baseUrl: String,
    val javaVersion: String,
    val activeProfiles: List<String>,
    val simulations: List<BenchmarkSimulation>,
    val requests: List<BenchmarkRequestStats>,
    val notes: List<String>
)

internal data class BenchmarkSimulation(
    val simulation: String,
    val description: String,
    val injectionProfile: String
)

internal data class BenchmarkRequestStats(
    val requestName: String,
    val requestCount: Int,
    val okCount: Int,
    val koCount: Int,
    val koPercentage: Double,
    val successfulRequestPercentage: Double,
    val countPerSecond: Double,
    val minMs: Int,
    val p50Ms: Int,
    val p75Ms: Int,
    val p95Ms: Int,
    val p99Ms: Int,
    val maxMs: Int,
    val meanMs: Int,
    val stdDevMs: Int
)
