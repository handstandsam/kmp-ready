package com.handstandsam.kmpreadiness.internal.deptraversal

import com.handstandsam.kmpreadiness.internal.models.Gav
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

internal object DepTraversal {
    fun getGavsForProject(target: Project): List<Gav> {
        val gavsToProcess = mutableListOf<Gav>()

        // TODO - Only Supports Projects with runtimeClasspath as a configuration
        target.configurations.filter { it.name == JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME }
            .forEach { configuration ->
                configuration.incoming.dependencies.forEach { incomingDependency ->
                    when (incomingDependency) {
                        is org.gradle.api.artifacts.ProjectDependency -> {
                            // TODO Handle Local Modules/Projects
                        }

                        is org.gradle.api.artifacts.ExternalDependency -> {
                            gavsToProcess.add(
                                Gav(
                                    group = incomingDependency.group!!,
                                    artifact = incomingDependency.name,
                                    version = incomingDependency.version!!
                                )
                            )
                        }

                        else -> {}
                    }

                }
            }
        return gavsToProcess
    }
}