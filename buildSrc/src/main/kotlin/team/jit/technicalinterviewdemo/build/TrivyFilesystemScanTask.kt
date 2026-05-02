package team.jit.technicalinterviewdemo.build

import javax.inject.Inject
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.process.ExecOperations

abstract class TrivyFilesystemScanTask @Inject constructor(
    execOperations: ExecOperations
) : TrivyVulnerabilityScanTask(execOperations) {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputDirectory: DirectoryProperty

    override fun dockerRuntimeArgs(): List<String> = listOf(
        "-v",
        "${inputDirectory.get().asFile.absolutePath.replace('\\', '/')}:/workspace/input:ro"
    )

    override fun scanArguments(format: String, outputPath: String): List<String> =
        standardArguments("fs", "/workspace/input", format, outputPath)
}
