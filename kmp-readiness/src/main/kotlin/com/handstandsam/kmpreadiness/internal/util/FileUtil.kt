package com.handstandsam.kmpreadiness.internal.util

import org.gradle.api.Project
import java.io.File

internal object FileUtil {

    fun projectDirOutputFile(
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
}