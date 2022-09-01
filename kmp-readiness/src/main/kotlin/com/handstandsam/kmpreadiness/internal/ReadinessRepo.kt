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
    val notReadyCache = mutableListOf<Gav>().also { notReady ->
        notReadyCacheFile.readText().lines().map { gavString ->
            Gav.fromString(gavString)?.let { notReady.add(it) }
        }
    }

    val readyCacheFile = File(tempDir, "ready.txt").apply {
        if (!exists()) {
            createNewFile()
        }
    }
    val readyCache = mutableListOf<Gav>().also { ready ->
        readyCacheFile.readText().lines().map { gavString ->
            Gav.fromString(gavString)?.let { ready.add(it) }
        }
    }

    fun write() {
        readyCacheFile
            .writeText(
                readyCache
                    .map { it.id }
                    .sorted()
                    .distinct()
                    .joinToString("\n")
            )
        notReadyCacheFile
            .writeText(
                notReadyCache
                    .map { it.id }
                    .distinct()
                    .sorted()
                    .joinToString("\n"))
    }

    fun add(kmpReadyResult: KmpReadyResult) {
        if (kmpReadyResult.isReady) {
            readyCache.add(kmpReadyResult.gav)
        } else {
            notReadyCache.add(kmpReadyResult.gav)
        }
    }

    fun hasBeenChecked(gav: Gav): Boolean {
        return readyCache.any { it == gav } || notReadyCache.any { it == gav }
    }
}
