package team.jit.technicalinterviewdemo.build

import javax.inject.Inject
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.process.ExecOperations

abstract class TrivyImageScanTask @Inject constructor(
    execOperations: ExecOperations
) : TrivyVulnerabilityScanTask(execOperations) {

    @get:Input
    abstract val imageName: Property<String>

    override fun dockerRuntimeArgs(): List<String> = listOf(
        "-v",
        "//var/run/docker.sock:/var/run/docker.sock"
    )

    override fun scanArguments(format: String, outputPath: String): List<String> =
        standardArguments("image", imageName.get(), format, outputPath)
}
