package team.jit.technicalinterviewdemo.build

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class TrivyImageScanTask @Inject constructor(execOperations: ExecOperations) :
    TrivyVulnerabilityScanTask(execOperations) {

    @get:Input
    abstract val imageName: Property<String>

    override fun dockerRuntimeArgs(): List<String> = listOf(
        "-v",
        "//var/run/docker.sock:/var/run/docker.sock",
    )

    override fun scanArguments(format: String, outputPath: String): List<String> =
        standardArguments("image", imageName.get(), format, outputPath)
}
