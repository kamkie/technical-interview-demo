package team.jit.technicalinterviewdemo.build

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.math.BigDecimal

class JacocoCoverageConventionsPlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        pluginManager.withPlugin("jacoco") {
            pluginManager.withPlugin("java") {
                configureJacocoCoverage()
            }
        }
    }

    private fun Project.configureJacocoCoverage() {
        val javaExtension = extensions.getByType<JavaPluginExtension>()
        val mainSourceSet = javaExtension.sourceSets.getByName("main")
        val testTask = tasks.named<Test>(JavaPlugin.TEST_TASK_NAME)
        val jacocoExecutionData = fileTree(layout.buildDirectory.dir("jacoco")).include("*.exec")

        val reportTask = tasks.named<JacocoReport>("jacocoTestReport") {
            sourceDirectories.setFrom(mainSourceSet.allJava.srcDirs)
            classDirectories.setFrom(mainSourceSet.output.asFileTree)
            executionData.setFrom(jacocoExecutionData)
            dependsOn(testTask)
            reports {
                xml.required.set(true)
                html.required.set(true)
            }
        }

        tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
            dependsOn(testTask)
            classDirectories.setFrom(mainSourceSet.output.asFileTree)
            executionData.setFrom(jacocoExecutionData)
            violationRules {
                rule {
                    element = "BUNDLE"
                    limit {
                        counter = "LINE"
                        value = "COVEREDRATIO"
                        minimum = LINE_COVERAGE_MINIMUM
                    }
                    limit {
                        counter = "BRANCH"
                        value = "COVEREDRATIO"
                        minimum = BRANCH_COVERAGE_MINIMUM
                    }
                }
            }
        }

        val summaryTask = tasks.register<JacocoCoverageSummaryTask>("jacocoCoverageSummary") {
            group = VERIFICATION_TASK_GROUP
            description = "Prints an overall JaCoCo summary and the lowest-covered classes from the latest report."
            dependsOn(reportTask)
            reportFile.set(layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml"))
            htmlReportDirectory.set(layout.buildDirectory.dir("reports/jacoco/test/html"))
            topClassCount.convention(LOWEST_COVERED_CLASS_COUNT)
        }

        testTask.configure {
            finalizedBy(summaryTask)
        }

        tasks.named(CHECK_TASK_NAME) {
            dependsOn("jacocoTestCoverageVerification")
        }
    }

    private companion object {
        const val CHECK_TASK_NAME: String = "check"
        val LINE_COVERAGE_MINIMUM: BigDecimal = BigDecimal("0.90")
        val BRANCH_COVERAGE_MINIMUM: BigDecimal = BigDecimal("0.70")
        const val LOWEST_COVERED_CLASS_COUNT: Int = 10
        const val VERIFICATION_TASK_GROUP: String = "verification"
    }
}
