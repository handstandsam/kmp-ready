package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpreadiness.internal.models.Gav
import com.handstandsam.kmpreadiness.internal.models.KmpDependenciesAnalysisResult
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

    suspend fun process(depsToProcess: List<Gav>): KmpDependenciesAnalysisResult {
        val readinessRepo = ReadinessRepo(tempDir)
        val compatible = mutableListOf<Gav>()
        val incompatible = mutableListOf<Gav>()
        depsToProcess.forEach { gav ->
            if (isExcluded(gav)) {
                readinessRepo.add(KmpReadyResult(gav, true))
                compatible.add(gav)
            } else {
                if (readinessRepo.readyCache.contains(gav)) {
                    compatible.add(gav)
                } else if (readinessRepo.notReadyCache.contains(gav)) {
                    incompatible.add(gav)
                } else {
                    val kmpReadyResult = MavenSearchRemote().searchFor(gav)
                    readinessRepo.add(kmpReadyResult)
                    if (kmpReadyResult.isReady) {
                        compatible.add(gav)
                    } else {
                        incompatible.add(gav)
                    }
                }
            }
            readinessRepo.write()
        }
        return KmpDependenciesAnalysisResult(
            compatible = compatible.map { it.id },
            incompatible = incompatible.map { it.id }
        )
    }
}