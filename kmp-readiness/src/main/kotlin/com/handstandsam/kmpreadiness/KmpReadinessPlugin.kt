package com.handstandsam.kmpreadiness

import com.handstandsam.kmpreadiness.internal.KmpReadinessTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Kotlin Multiplatform Convention Plugin that is swappable with the JVM Plugin
 */
public class KmpReadinessPlugin : Plugin<Project> {

    internal fun addKmpReadinessTask(target: Project) {
        val isAlreadyApplied = target.tasks.findByName(KmpReadinessTask.TASK_NAME) != null
        if (!isAlreadyApplied) {
            target.tasks.register(
                KmpReadinessTask.TASK_NAME,
                KmpReadinessTask::class.java
            )
        }
    }

    override fun apply(target: Project) {
        if (target == target.rootProject) {
            // Apply everywhere
            target.rootProject.subprojects.forEach {
                addKmpReadinessTask(it)
            }
        } else {
            addKmpReadinessTask(target)
        }
    }
}
