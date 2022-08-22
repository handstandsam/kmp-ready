package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpreadiness.internal.models.Gav
import java.io.File

internal class DependenciesReadinessProcessor(private val tempDir: File) {

    val excludedArtifacts = listOf(
        "org.jetbrains.kotlin:kotlin-stdlib",
        "org.jetbrains.kotlin:kotlin-stdlib-jdk7",
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8",
        "org.jetbrains.kotlinx:kotlinx-coroutines-core",
    ).map {
        val tokens = it.split(":")
        Gav(
            group = tokens[0],
            artifact = tokens[1],
            version = null
        )
    }

    /**
     * Excluding by group + artifact
     */
    fun isExcluded(gav: Gav): Boolean {
        return excludedArtifacts.any { it.group == gav.group && it.artifact == gav.artifact }
    }

    suspend fun process(depsToProcess: List<Gav>) {
        val readinessRepo = ReadinessRepo(tempDir)
        depsToProcess.forEach { gav ->
            if (isExcluded(gav)) {
                readinessRepo.add(kmpReadyResult = KmpReadyResult(gav, true))
            } else {
                if (!readinessRepo.hasBeenChecked(gav)) {
                    val kmpReadyResult = MavenSearchRemote().searchFor(gav)
                    readinessRepo.add(kmpReadyResult)
                }
            }
            readinessRepo.debugPrint()
            readinessRepo.write()
        }
    }
}