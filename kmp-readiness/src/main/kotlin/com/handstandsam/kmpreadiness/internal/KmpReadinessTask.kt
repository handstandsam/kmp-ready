package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpreadiness.internal.Tasks.declareCompatibilities
import com.handstandsam.kmpreadiness.internal.models.Gav
import com.handstandsam.kmpreadiness.internal.models.ReadinessResult
import com.jakewharton.picnic.table
import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

internal abstract class KmpReadinessTask : DefaultTask() {

    public companion object {
        public const val TASK_NAME: String = "kmpReadiness"
    }

    init {
        group = "KMP Readiness"
        this.declareCompatibilities() // Does not support configuration cache
    }

    private fun Project.toReadinessData(gavsToProcess: List<Gav>): ReadinessData {

        // println("Plugins for ${target.path}")
        val appliedPlugins = plugins.map { it::class.java.name }
        // target.plugins.forEach {
        //     println("* ${it::class.java.name}")
        // }

        val appliedGradlePlugins = AppliedGradlePlugins(
            java = plugins.any { it::class.java == org.gradle.api.plugins.JavaPlugin::class.java },
            multiplatform = plugins.any { it::class.java == KotlinMultiplatformPluginWrapper::class.java },
            kotlin = plugins.any { it::class.java == KotlinPluginWrapper::class.java }
        )

        val sourceSetSearcher = SourceSetSearcher()
        val sourceSetSearcherResult = sourceSetSearcher.searchSourceSets(project)
        val tempDir = FileUtil.projectDirOutputFile(project)
        val kmpDependenciesAnalysisResult = runBlocking {
            DependenciesReadinessProcessor(tempDir).process(gavsToProcess)
        }

        return ReadinessData(
            projectName = path,
            dependencyAnalysisResult = kmpDependenciesAnalysisResult,
            gradlePlugins = appliedGradlePlugins,
            sourceSetSearcherResult = sourceSetSearcherResult
        )
    }

    private fun getGavsForProject(target: Project): List<Gav> {
        val gavsToProcess = mutableListOf<Gav>()
        target.configurations.filter { it.name == JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME }
            .forEach { configuration ->
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
        val gavsToProcess = getGavsForProject(target)
        val readinessData = target.toReadinessData(gavsToProcess)
        val readinessResult = readinessData.computeReadiness()
        readinessResult
    }

    @Suppress("NestedBlockDepth")
    @TaskAction
    internal fun execute() {
        val results = mutableMapOf<String, ReadinessResult>()

        // val runOnSubprojects = project.isRootProject()
        val runOnSubprojects = true

        if (runOnSubprojects) {
            project.rootProject.subprojects
                .filter { subproject ->
                    // Only if we have a classpath ending in "runtimeClasspath"
                    subproject.configurations.any { it.name.toLowerCase().endsWith("runtimeclasspath") }
                }
                .forEach { target ->
                    results[target.path] = executeForProject(target)
                }
        } else {
            results[project.path] = executeForProject(project)
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
            body {
                results.forEach {
                    val value = it.value
                    if (value is ReadinessResult.Ready) {
                        row(it.key, it.value::class.java.simpleName + "\n" + value.readinessData)
                    }
                }
                results.forEach {
                    val value = it.value
                    if (value is ReadinessResult.NotReady) {
                        row(it.key, it.value::class.java.simpleName + "\n" + value.readinessData)
                    }
                }
            }

        }

        println("table \n$table")
    }
}
