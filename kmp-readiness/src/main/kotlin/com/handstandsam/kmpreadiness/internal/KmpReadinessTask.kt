package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpreadiness.internal.Tasks.declareCompatibilities
import com.handstandsam.kmpreadiness.internal.models.ReadinessResult
import com.jakewharton.picnic.table
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

internal abstract class KmpReadinessTask : DefaultTask() {

    public companion object {
        public const val TASK_NAME: String = "kmpReadiness"
    }

    init {
        group = "KMP Readiness"
        this.declareCompatibilities() // Does not support configuration cache
    }

    private fun executeForProject(target: Project): ReadinessResult {
        return ReadinessCalculator(target).computeReadinessResult()
    }

    /**
     * TODO: Enable this, hard coding to scan all projects for now
     */
    private val runOnSubprojects: Boolean
        get() {
            // return project.isRootProject()
            return true
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

        val terminalOutput = toTerminalOutput(results)
        println(terminalOutput)
    }

    private fun toTerminalOutput(results: MutableMap<String, ReadinessResult>): String {

        return table {
            cellStyle {
                border = true
                paddingLeft = 1
                paddingRight = 1
            }

            header {
                row("Module", "KMP Readiness Result", "Data")
            }
            body {
                results.forEach {
                    val value = it.value
                    if (value is ReadinessResult.Ready) {
                        row {
                            cell(it.key)
                            cell("${it.value::class.java.simpleName}(${value.readyReason})")
                            cell(value.readinessData)
                        }
                    }
                }
                results.forEach {
                    val value = it.value
                    if (value is ReadinessResult.NotReady) {
                        row {
                            cell(it.key)
                            cell("${it.value::class.java.simpleName}(${value.reason})")
                            cell(value.readinessData)
                        }
                    }
                }
            }
        }.toString()
    }
}
