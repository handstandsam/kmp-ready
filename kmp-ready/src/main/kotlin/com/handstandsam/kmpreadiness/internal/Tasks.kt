package com.handstandsam.kmpreadiness.internal

import org.gradle.api.Task

internal object GradleVersion {
    private val current = org.gradle.util.GradleVersion.current()
    val isAtLeast73 = current >= org.gradle.util.GradleVersion.version("7.3")
    val isAtLeast74 = current >= org.gradle.util.GradleVersion.version("7.4")
}

@Suppress("UnstableApiUsage")
internal object Tasks {
    fun Task.declareCompatibilities() {
        if (GradleVersion.isAtLeast73) {
            doNotTrackState("This task only outputs to console")
        } else {
            outputs.upToDateWhen { false }
        }

        // See also https://github.com/dropbox/dependency-guard/issues/4.
        if (GradleVersion.isAtLeast74) {
            notCompatibleWithConfigurationCache("Uses Project at execution time")
        }
    }
}
