package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpreadiness.internal.Tasks.declareCompatibilities
import com.jakewharton.picnic.table
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

internal abstract class KmpReadinessTask : DefaultTask() {

    public companion object {
        public const val TASK_NAME: String = "kmpReady"
    }

    init {
        group = "KMP Ready"
        this.declareCompatibilities() // Does not support configuration cache
    }

    private fun executeForProject(target: Project): ReadinessResult {
        return ReadinessDataCalculator(target).computeReadinessResult()
    }

    /**
     * TODO: Enable this, hard coding to scan all projects for now
     */
    private val runOnSubprojects: Boolean
        get() {
            return project.isRootProject()
        }

    @Suppress("NestedBlockDepth")
    @TaskAction
    internal fun execute() {
        val results = mutableMapOf<String, ReadinessResult>()
        if (runOnSubprojects) {
            project.rootProject.subprojects
                .filter { subproject ->
                    // Only if we have a classpath ending in "runtimeClasspath"
                    subproject.configurations.any { it.name.toLowerCase().endsWith("runtimeclasspath") }
                }
                .forEach { subproject ->
                    results[subproject.path] = executeForProject(subproject)
                }
        } else {
            results[project.path] = executeForProject(project)
        }

        val terminalOutput = toTerminalOutput(results, logger.isInfoEnabled)
        println(terminalOutput)
    }

    private fun toTerminalOutput(results: MutableMap<String, ReadinessResult>, isInfoLoggingEnabled: Boolean): String {

        return table {
            cellStyle {
                border = true
                paddingLeft = 1
                paddingRight = 1
            }

            header {
                row {
                    cell("Module")
                    cell("KMP Ready Result")
                    if (isInfoLoggingEnabled) {
                        cell("Collected Data")
                    }
                }
            }
            body {
                results.forEach {
                    val readinessResult = it.value
                    val projectPath = it.key
                    val reasonsText = buildString {
                        readinessResult.readyReasons.forEach { reason ->
                            appendLine("✅ ${reason.type}")
                            reason.details?.let {
                                append(reason.details)
                            }
                        }
                        readinessResult.notReadyReasons.forEach { reason ->
                            appendLine("❌ ${reason.type}")
                            reason.details?.let {
                                append(reason.details)
                            }
                        }
                    }
                    row {
                        cell(projectPath)
                        cell(buildString {
                            appendLine(readinessResult.headline)
                            appendLine("---")
                            appendLine(reasonsText)
                        })
                        if (isInfoLoggingEnabled) {
                            cell(readinessResult.readinessData)
                        }
                    }
                }
            }
        }.toString()
    }
}
