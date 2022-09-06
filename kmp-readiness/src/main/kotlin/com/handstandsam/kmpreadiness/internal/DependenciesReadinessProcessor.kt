package com.handstandsam.kmpreadiness.internal

import java.io.File
import com.handstandsam.kmpreadiness.internal.models.Gav
import com.handstandsam.kmpreadiness.internal.models.KmpDependenciesAnalysisResult
import com.handstandsam.kmpreadiness.internal.models.KmpReadyResult

internal class DependenciesReadinessProcessor(private val tempDir: File) {

    val excludedArtifacts = listOf(
        "org.jetbrains.kotlin:kotlin-stdlib",
        "org.jetbrains.kotlin:kotlin-stdlib-common",
        "org.jetbrains.kotlin:kotlin-stdlib-jdk7",
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8",
        "org.jetbrains.kotlinx:kotlinx-coroutines-core",
        "org.jetbrains.kotlinx:kotlinx-serialization-core",
        "org.jetbrains.kotlinx:kotlinx-serialization-json",
    ).map {
        val tokens = it.split(":")
        Gav(
            group = tokens[0],
            artifact = tokens[1],
            version = ""
        )
    }

    /**
     * Excluding by group + artifact
     */
    fun isExcluded(gav: Gav): Boolean {
        return excludedArtifacts.any { it.group == gav.group && it.artifact == gav.artifact }
    }

    private suspend fun computeReadinessFromMavenRepos(mavenRepoUrls: List<String>, gav: Gav): KmpReadyResult {
        val attemptedUrls = mutableListOf<String>()
        mavenRepoUrls.forEach { mavenRepoUrl ->
            val kotlinToolingMetadataUrlString = SearchRemote.getUrlForKotlinToolingMetadata(mavenRepoUrl, gav)
            val result = SearchRemote().searchForInRepo(
                kotlinToolingMetadataUrlString = kotlinToolingMetadataUrlString,
                gav = gav
            )
            when (result) {
                is SearchRemote.KotlinToolingMetadataResult.Success -> {
                    return KmpReadyResult.Allowed.FromRemote(
                        gav = gav,
                        metadataUrl = kotlinToolingMetadataUrlString
                    )
                }

                is SearchRemote.KotlinToolingMetadataResult.NotFound -> {
                    attemptedUrls.add(kotlinToolingMetadataUrlString)
                }
            }
        }
        return KmpReadyResult.NotAllowed(
            gav = gav,
            attemptedUrls = attemptedUrls
        )
    }

    suspend fun process(mavenRepoUrls: List<String>, depsToProcess: List<Gav>): KmpDependenciesAnalysisResult {
        val readinessRepo = ReadinessRepo(tempDir)
        depsToProcess.forEach { gav ->
            if (isExcluded(gav)) {
                readinessRepo.add(KmpReadyResult.Allowed.Excluded(gav))
            } else {
                val fromReadyCache = readinessRepo.cache.ready[gav.id]
                val fromNotReadyCache = readinessRepo.cache.notReady[gav.id]
                if (fromReadyCache != null) {
                    readinessRepo.add(fromReadyCache)
                } else if (fromNotReadyCache != null) {
                    readinessRepo.add(fromNotReadyCache)
                } else {
                    when (val kmpReadyResult: KmpReadyResult = computeReadinessFromMavenRepos(mavenRepoUrls, gav)) {
                        is KmpReadyResult.Allowed -> {
                            readinessRepo.add(kmpReadyResult)
                        }

                        is KmpReadyResult.NotAllowed -> {
                            readinessRepo.add(kmpReadyResult)
                        }
                    }
                }
            }
            readinessRepo.write()
        }
        return KmpDependenciesAnalysisResult(
            compatible = readinessRepo.cache.ready.values.toMutableList(),
            incompatible = readinessRepo.cache.notReady.values.toMutableList(),
            all = depsToProcess.map { it.id }
        )
    }
}