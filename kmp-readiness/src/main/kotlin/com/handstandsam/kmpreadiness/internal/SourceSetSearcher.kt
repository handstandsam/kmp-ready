package com.handstandsam.kmpreadiness.internal

import com.android.build.api.dsl.LibraryExtension
import com.jakewharton.picnic.table
import kotlinx.serialization.Serializable
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

@Serializable
internal data class SourceSetSearcherResult(val sourceSetToFiles: Map<String, List<String>>) {

    fun findFilesEndingWith(suffix: String): List<String> {
        val files = mutableListOf<String>()
        sourceSetToFiles.forEach { entry ->
            entry.value.filter { it.endsWith(suffix) }
                .forEach { file ->
                    files.add(file)
                }
        }
        return files
    }

    val javaFiles: List<String> = findFilesEndingWith(".java")

    val kotlinFiles: List<String> = findFilesEndingWith(".kt")

    val hasOnlyKotlinFiles: Boolean = javaFiles.isEmpty()

    override fun toString(): String {
        return table {
            header {
                row("SourceSet", "Files")
            }
            body {
                sourceSetToFiles.keys.forEach { sourceSetName: String ->
                    val files = sourceSetToFiles[sourceSetName]!!
                    row(sourceSetName, buildString {
                        files.forEach {
                            appendLine("* $it")
                        }
                    })
                }
            }
        }.toString()
    }
}

internal class SourceSetSearcher {

    // list all files from this path
    @Throws(IOException::class)
    private fun listFiles(path: Path?): List<Path> {
        var result = mutableListOf<Path>()
        Files.walk(path).use { walk ->
            result = walk.filter { it.toFile().isFile }
                .collect(Collectors.toList())
        }
        return result
    }

    fun walkSourceDirectorySet(sourceFiles: SourceDirectorySet, endsWith: String): List<Path> {
        val paths = mutableListOf<Path>()
        sourceFiles
            .forEach {
                paths.addAll(listFiles(it.toPath()))
            }
        return paths
    }

    fun searchSourceSets(project: Project): SourceSetSearcherResult {
        val sourceSetToFiles = mutableMapOf<String, List<String>>()

        project.extensions.findByType(KotlinAndroidProjectExtension::class.java)?.let { projectExtension ->
            projectExtension.sourceSets
                .filter { !it.name.toLowerCase().contains("test") }
                .forEach { sourceSet ->
                    val matchingFiles = mutableListOf<Path>().apply {
                        addAll(walkSourceDirectorySet(sourceSet.kotlin, ""))
                        addAll(walkSourceDirectorySet(sourceSet.resources, ")"))
                    }
                    sourceSetToFiles["android-${sourceSet.name}"] = matchingFiles.map { it.toString() }
                }
            println(this)
        }

        project.extensions.findByType(KotlinJvmProjectExtension::class.java)?.apply {
            sourceSets
                .filter { !it.name.toLowerCase().contains("test") }
                .forEach { sourceSet ->
                    val matchingFiles = walkSourceDirectorySet(sourceSet.kotlin, "")
                    sourceSetToFiles["kotlin-${sourceSet.name}"] = matchingFiles.map { it.toString() }
                }
        }

        project.extensions.findByType(JavaPluginExtension::class.java)?.apply {
            sourceSets
                .filter { !it.name.toLowerCase().contains("test") }
                .forEach { sourceSet ->
                    val matchingFiles = walkSourceDirectorySet(sourceSet.java, "")
                    sourceSetToFiles["java-${sourceSet.name}"] = matchingFiles.map { it.toString() }
                }
        }

        return SourceSetSearcherResult(sourceSetToFiles)
    }
}