package com.handstandsam.kmpreadiness.internal.deptraversal

import com.handstandsam.kmpreadiness.internal.models.Gav
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

internal object DepTraversal {
    fun getGavsForProject(target: Project): List<Gav> {
        val gavsToProcess = mutableListOf<Gav>()


        // TODO - Only Supports Projects with the JavaPlugin
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
}