package com.handstandsam.kmpreadiness.internal

import com.jakewharton.picnic.table
import kotlinx.serialization.Serializable
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import java.nio.file.Path

@Serializable
internal data class SourceSetSearcherResult(val sourceSetToFiles: Map<String, List<String>>) {
    val hasOnlyKotlinFiles: Boolean
        get() {
            val anyJavaFiles = sourceSetToFiles.entries
                .any { entry ->
                    entry.value.any { filePath ->
                        filePath.endsWith(".java")
                    }
                }
            !anyJavaFiles
            return true
        }

    override fun toString(): String {
        return table {
            header {
                row("SourceSet", "Files")
            }
            body {
                sourceSetToFiles.forEach {
                    row(it.key, buildString {
                        it.value.forEach {
                            appendLine("* " + it)
                        }
                    })
                }
            }
        }.toString()
    }
}

internal class SourceSetSearcher() {

    private fun lookInJavaPlugin() {
    }

    fun walkSourceDirectorySet(sourceFiles: SourceDirectorySet, endsWith: String): List<Path> {
        return sourceFiles
            .filter {
                it.exists()
            }
            .map { it.toPath() }
    }

    fun searchSourceSets(project: Project): SourceSetSearcherResult {
        val sourceSetToFiles = mutableMapOf<String, List<String>>()

        project.extensions.findByType(KotlinJvmProjectExtension::class.java)?.apply {
            sourceSets
                .filter { it.name == "main" }
                .forEach { sourceSet ->
                    val matchingFiles = walkSourceDirectorySet(sourceSet.kotlin, "")
                    sourceSetToFiles["kotlin-${sourceSet.name}"] = matchingFiles.map { it.toString() }
                }
        }

        project.extensions.findByType(JavaPluginExtension::class.java)?.apply {
            sourceSets
                .filter { it.name == "main" }
                .forEach { sourceSet ->
                    val matchingFiles = walkSourceDirectorySet(sourceSet.java, "")
                    sourceSetToFiles["java-${sourceSet.name}"] = matchingFiles.map { it.toString() }
                }
        }

        return SourceSetSearcherResult(sourceSetToFiles)
    }
}