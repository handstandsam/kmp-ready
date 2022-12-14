package com.handstandsam.kmpreadiness.internal

import com.jakewharton.picnic.table
import kotlinx.serialization.Serializable
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

/**
 * Reference to something in the Java Base Library
 * java.base
 * https://docs.oracle.com/en/java/javase/11/docs/api/java.base/module-summary.html
 */
@Serializable
internal data class JavaBaseUsage(
    val lineNumber: Int,
    val lineContent: String
)

@Serializable
internal data class JavaBaseUsagesInFile(
    val filePath: String,
    val references: List<JavaBaseUsage>,
)

@Serializable
internal data class SourceSetSearcherResult(
    val sourceSetToFiles: Map<String, List<String>>,
    val javaBaseReferences: List<JavaBaseUsagesInFile>
) {

    fun findFilesEndingWith(suffix: String): List<String> {
        val files = mutableListOf<String>()
        sourceSetToFiles.keys.forEach { sourceSet:String ->
            val filesInSourceSet = sourceSetToFiles[sourceSet]!!
            filesInSourceSet.filter { it.endsWith(suffix) }
                .forEach { file ->
                    files.add(file)
                }
        }
        return files
    }

    val javaFiles: List<String> = findFilesEndingWith(".java")

    val hasOnlyKotlinFiles: Boolean = javaFiles.isEmpty()
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

        val javaBaseUsagesInFile = mutableListOf<JavaBaseUsagesInFile>()
        sourceSetToFiles.forEach { sourceSetName: String, files: List<String> ->
            files.forEach { filePath ->
                val javaBaseReferences = mutableListOf<JavaBaseUsage>()
                File(filePath).readLines().forEachIndexed { idx, lineContents ->
                    listOf(
                        "java.io",
                        "java.lang",
                        "java.math",
                        "java.net",
                        "java.nio",
                        "java.security",
                        "java.text",
                        "java.time",
                        "java.util",
                        "javax.crypto",
                        "javax.net",
                        "javax.security",
                        "javax.inject"
                    ).forEach {
                        if (lineContents.contains(it)) {
                            javaBaseReferences.add(
                                JavaBaseUsage(
                                    lineContent = lineContents.trim(),
                                    lineNumber = idx + 1
                                )
                            )
                        }
                    }
                }
                if (javaBaseReferences.isNotEmpty()) {
                    javaBaseUsagesInFile.add(
                        JavaBaseUsagesInFile(
                            filePath = filePath,
                            references = javaBaseReferences,
                        )
                    )
                }
            }
        }

        return SourceSetSearcherResult(sourceSetToFiles, javaBaseUsagesInFile)
    }
}