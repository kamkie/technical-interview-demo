package team.jit.technicalinterviewdemo.build

import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "The task only logs coverage details and does not produce cacheable outputs.")
abstract class JacocoCoverageSummaryTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val reportFile: RegularFileProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val htmlReportDirectory: DirectoryProperty

    @get:Input
    abstract val topClassCount: Property<Int>

    @TaskAction
    fun summarize() {
        val xmlReportFile = reportFile.get().asFile
        check(xmlReportFile.exists()) {
            "JaCoCo XML report not found at ${xmlReportFile.absolutePath}. Run jacocoTestReport first."
        }

        val documentBuilderFactory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = false
            setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
            setFeature("http://xml.org/sax/features/external-general-entities", false)
            setFeature("http://xml.org/sax/features/external-parameter-entities", false)
        }
        val document = documentBuilderFactory.newDocumentBuilder().parse(xmlReportFile)
        val counterNodes = document.getElementsByTagName("counter")
        var coveredLines = 0
        var missedLines = 0
        val classCoverage = mutableListOf<ClassCoverage>()

        for (index in 0 until counterNodes.length) {
            val node = counterNodes.item(index)
            val type = node.attributes.getNamedItem("type")?.nodeValue
            val missed = node.attributes.getNamedItem("missed")?.nodeValue?.toInt() ?: 0
            val covered = node.attributes.getNamedItem("covered")?.nodeValue?.toInt() ?: 0

            if (node.parentNode.nodeName == "report" && type == "LINE") {
                missedLines = missed
                coveredLines = covered
            }
            if (node.parentNode.nodeName == "class" && type == "LINE") {
                val className = node.parentNode.attributes.getNamedItem("name").nodeValue.replace('/', '.')
                classCoverage.add(ClassCoverage(className, missed, covered))
            }
        }

        val totalLines = coveredLines + missedLines
        val overallCoverage = if (totalLines == 0) {
            100.0
        } else {
            (coveredLines.toDouble() * 100.0) / totalLines.toDouble()
        }

        logger.lifecycle(
            "JaCoCo line coverage: {}% (covered={}, missed={})",
            formatPercentage(overallCoverage),
            coveredLines,
            missedLines
        )
        logger.lifecycle("JaCoCo HTML report: {}", htmlReportDirectory.get().asFile.absolutePath)
        logger.lifecycle("Lowest-covered classes:")

        classCoverage
            .filter { it.totalLines > 0 }
            .sortedWith(compareBy<ClassCoverage> { it.coverageRatio }.thenByDescending { it.missedLines })
            .take(topClassCount.get())
            .forEach { classSummary ->
                logger.lifecycle(
                    " - {}: {}% (covered={}, missed={})",
                    classSummary.className,
                    formatPercentage(classSummary.coveragePercentage),
                    classSummary.coveredLines,
                    classSummary.missedLines
                )
            }
    }

    private fun formatPercentage(value: Double): String = String.format(Locale.ROOT, "%.1f", value)

    private data class ClassCoverage(
        val className: String,
        val missedLines: Int,
        val coveredLines: Int
    ) {
        val totalLines: Int
            get() = missedLines + coveredLines

        val coverageRatio: Double
            get() = if (totalLines == 0) {
                1.0
            } else {
                coveredLines.toDouble() / totalLines.toDouble()
            }

        val coveragePercentage: Double
            get() = coverageRatio * 100.0
    }
}
