package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpreadiness.internal.Tasks.declareCompatibilities
import com.handstandsam.kmpreadiness.internal.models.Gav
import com.jakewharton.picnic.table
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import java.io.File

public abstract class KmpReadinessTask : DefaultTask() {

    public companion object {
        public const val TASK_NAME: String = "kmpReadiness"
    }

    init {
        group = "KMP Readiness"
        this.declareCompatibilities() // Does not support configuration cache
    }

    internal sealed class ReadinessResult {

        internal sealed class Ready : ReadinessResult() {
            object AlreadyEnabled : Ready()
            object Compatible : Ready()
        }

        data class NotReady(val reason: String) : ReadinessResult()
    }

    private data class ReadinessData(
        val projectName: String,
        val hasKotlinMultiplatformPlugin: Boolean,
        val hasJavaPlugin: Boolean,
        val hasKotlinWrapperPlugin: Boolean,
        val hasOnlyKotlinFiles: Boolean,
        val hasOnlyMultiplatformCompatibleDependencies: Boolean,
    ) {

        fun computeReadiness(): ReadinessResult {
            return if (hasKotlinMultiplatformPlugin) {
                ReadinessResult.Ready.AlreadyEnabled
            } else if (hasKotlinWrapperPlugin) {
                if (hasOnlyKotlinFiles) {
                    if (hasOnlyMultiplatformCompatibleDependencies) {
                        ReadinessResult.Ready.Compatible
                    } else {
                        ReadinessResult.NotReady("Incompatible Dependencies")
                    }
                } else {
                    ReadinessResult.NotReady("Contains Java Files")
                }
            } else {
                ReadinessResult.NotReady("Does Not Have the Kotlin JVM or Multiplatform Plugin")
            }
        }
    }

    internal fun projectDirOutputFile(
        project: Project,
    ): File {
        return project.layout
            .buildDirectory
            .get()
            .dir("tmp/kmp-readiness")
            .asFile
            .apply {
                if (!exists()) {
                    // Create the parent directory if it does not exist
                    mkdirs()
                }

            }
    }

    private fun Project.toReadinessData(gavsToProcess: List<Gav>): ReadinessData = runBlocking {
        val target = this
        val tempDir = projectDirOutputFile(project)
        DependenciesReadinessProcessor(tempDir).process(gavsToProcess)

        val javaPlugin = plugins.any { it::class.java == org.gradle.api.plugins.JavaPlugin::class.java }
        val multiplatformPlugin = plugins.any { it::class.java == KotlinMultiplatformPluginWrapper::class.java }
        val kotlinPluginWrapper = plugins.any { it::class.java == KotlinPluginWrapper::class.java }
        ReadinessData(
            projectName = path,
            hasKotlinMultiplatformPlugin = multiplatformPlugin,
            hasJavaPlugin = javaPlugin,
            hasKotlinWrapperPlugin = kotlinPluginWrapper,
            hasOnlyKotlinFiles = true, // TODO, compute this
            hasOnlyMultiplatformCompatibleDependencies = true // TODO, compute this
        )
    }

    private fun getGavsForProject(target: Project): List<Gav> {
        val gavsToProcess = mutableListOf<Gav>()
        target.configurations.filter { it.name == "runtimeClasspath" }.forEach { configuration ->
            println("* Configuration: ${configuration.name}")
            configuration.incoming.dependencies.forEach { incomingDependency ->
                println("** Artifact: ${incomingDependency.name} ${incomingDependency::class.java}")
                when (incomingDependency) {
                    is org.gradle.api.artifacts.ProjectDependency -> {
                    }

                    is org.gradle.api.artifacts.ExternalDependency -> {
                        gavsToProcess.add(
                            Gav(
                                group = incomingDependency.group!!,
                                artifact = incomingDependency.name,
                                version = incomingDependency.version
                            )
                        )
                    }

                    else -> {}
                }

            }
        }
        println("depsToProcess $gavsToProcess")
        return gavsToProcess
    }

    private fun executeForProject(target: Project): ReadinessResult = runBlocking {

        println("Project: ${target.name}")

        // println("Plugins for ${target.path}")
        // target.plugins.forEach {
        //     println("* ${it::class.java.name}")
        // }

        val gavsToProcess = getGavsForProject(target)

        val readinessData = target.toReadinessData(gavsToProcess)
        println(" readinessData: ${readinessData}")

        val readinessResult = readinessData.computeReadiness()

        println(" readinessResult: $readinessResult")
        when (readinessResult) {
            is ReadinessResult.Ready -> {
                println(" && READY")
            }

            is ReadinessResult.NotReady -> {
                println(" && NOT READY: ${readinessData::class.java.isAssignableFrom(ReadinessResult.Ready::class.java)}")
            }
        }

        // val deps = DependencyVisitor.traverseDependenciesForConfiguration(configuration)
        // println("Deps: $deps")
        readinessResult
    }

    @Suppress("NestedBlockDepth")
    @TaskAction
    internal fun execute() {

        val results = mutableMapOf<Project, ReadinessResult>()

        // val runOnSubprojects = project.isRootProject()
        val runOnSubprojects = true

        if (runOnSubprojects) {
            project.rootProject.subprojects.forEach { target ->
                results[target] = executeForProject(target)
            }
        } else {
            results[project] = executeForProject(project)
        }
        val table = table {
            cellStyle {
                border = true
                paddingLeft = 1
                paddingRight = 1
            }

            header {
                row("Module Path", "KMP Readiness Result")
            }
            // body {
            //     row {
            //         cell("Hello") {
            //             rowSpan = 2
            //         }
            //         cell("World")
            //     }
            // }
            body {
                results.filter { it.value is ReadinessResult.Ready }.forEach {
                    row(it.key.path, it.value::class.java.simpleName)
                }
                results.filter { it.value is ReadinessResult.NotReady }.forEach {
                    row(it.key.path, it.value::class.java.simpleName)
                }
            }
        }

        println("table \n$table")
    }
}
