package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpreadiness.internal.models.KmpDependenciesAnalysisResult
import kotlinx.serialization.Serializable

@Serializable
internal data class AppliedGradlePlugins(
    val kotlinJvm: Boolean,
    val kotlinMultiplatform: Boolean,
    val androidLibrary: Boolean,
    val androidApplication: Boolean,
    val plugins: List<String>
) {
    fun hasCompatiblePlugin(): Boolean {
        return kotlinJvm || kotlinMultiplatform
    }

    fun isKnownPlugin(): Boolean {
        return kotlinJvm || kotlinMultiplatform || androidLibrary || androidApplication
    }
}

@Serializable
internal data class ReadinessData(
    val projectName: String,
    val dependencyAnalysis: KmpDependenciesAnalysisResult,
    val gradlePlugins: AppliedGradlePlugins,
    val sourceSets: SourceSetSearcherResult,
) {
    override fun toString(): String {
        return JsonSerializer.json.encodeToString(serializer(), this)
    }
}
