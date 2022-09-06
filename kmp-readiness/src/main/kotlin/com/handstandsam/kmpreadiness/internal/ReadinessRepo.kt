package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpreadiness.internal.models.Gav
import com.handstandsam.kmpreadiness.internal.models.KmpReadyResult
import java.io.File

internal class ReadinessRepo(tempDir: File) {

    val notReadyCacheFile = File(tempDir, "not-ready.txt").apply {
        if (!exists()) {
            createNewFile()
        }
    }
    val notReadyCache = mutableListOf<ResultDataPair>().also { notReady ->
        notReadyCacheFile.readText().lines().map { gavString ->
            Gav.fromString(gavString)?.let { notReady.add(ResultDataPair(it, KmpReadyResult.NotAllowed.FromCache(it))) }
        }
    }

    val readyCacheFile = File(tempDir, "ready.txt").apply {
        if (!exists()) {
            createNewFile()
        }
    }
    val readyCache = mutableListOf<ResultDataPair>().also { ready ->
        readyCacheFile.readText().lines().map { gavString ->
            Gav.fromString(gavString)?.let { ready.add(ResultDataPair(it, KmpReadyResult.Allowed.FromCache(it))) }
        }
    }

    fun write() {
        readyCacheFile
            .writeText(
                readyCache
                    .map { it.gav.id }
                    .sorted()
                    .distinct()
                    .joinToString("\n")
            )
        notReadyCacheFile
            .writeText(
                notReadyCache
                    .map { it.gav.id }
                    .distinct()
                    .sorted()
                    .joinToString("\n"))
    }

    data class ResultDataPair(val gav: Gav, val kmpReadyResult: KmpReadyResult)

    fun add(gav: Gav, kmpReadyResult: KmpReadyResult) {
        when (kmpReadyResult) {
            is KmpReadyResult.Allowed -> {
                readyCache.add(ResultDataPair(gav, kmpReadyResult))
            }

            is KmpReadyResult.NotAllowed -> {
                notReadyCache.add(ResultDataPair(gav, kmpReadyResult))
            }
        }
    }

    fun hasBeenChecked(gav: Gav): Boolean {
        return readyCache.any { it.gav == gav } || notReadyCache.any { it.gav == gav }
    }
}
