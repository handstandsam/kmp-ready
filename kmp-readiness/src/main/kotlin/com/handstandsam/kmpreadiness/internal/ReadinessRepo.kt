package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpready.internal.JsonSerializer
import com.handstandsam.kmpready.internal.models.KmpReadyResult
import kotlinx.serialization.Serializable
import java.io.File

internal class ReadinessRepo(tempDir: File) {

    private val cacheFile = File(tempDir, "kmp-readiness-cache.json").apply {
        if (!exists()) {
            createNewFile()
        }
    }

    @Serializable
    data class CachedContents(
        val ready: MutableMap<String, KmpReadyResult.Allowed>,
        val notReady: MutableMap<String, KmpReadyResult.NotAllowed>
    )

    val cache: CachedContents = run {
        val fileContents = cacheFile.readText()
        try {
            JsonSerializer.json.decodeFromString(CachedContents.serializer(), fileContents)
        } catch (e: Exception) {
            CachedContents(
                ready = mutableMapOf(),
                notReady = mutableMapOf()
            )
        }
    }

    fun write() {
        val json = JsonSerializer.json.encodeToString(CachedContents.serializer(), cache)
        cacheFile
            .writeText(
                json
            )
    }

    fun add(kmpReadyResult: KmpReadyResult) {
        when (kmpReadyResult) {
            is KmpReadyResult.Allowed -> {
                cache.ready[kmpReadyResult.gav.id] = kmpReadyResult
            }

            is KmpReadyResult.NotAllowed -> {
                cache.notReady[kmpReadyResult.gav.id] = kmpReadyResult
            }
        }
    }
}
