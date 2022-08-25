package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpreadiness.internal.models.KmpDependenciesAnalysisResult
import com.handstandsam.kmpreadiness.internal.models.NotReadyReasonType
import com.handstandsam.kmpreadiness.internal.models.ReadinessResult
import com.handstandsam.kmpreadiness.internal.models.ReadyReasonType
import com.handstandsam.kmpreadiness.internal.models.Reason
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class AppliedGradlePlugins(
    val kotlinJvm: Boolean,
    val kotlinMultiplatform: Boolean,
    val androidLibrary: Boolean,
    val androidApplication: Boolean,
    val java: Boolean,
)

@Serializable
internal data class AndroidLibraryInfo(
    val resFolder: Boolean,
    val assetFolder: Boolean,
    val referenceAndroidPackages: Boolean,
)

@Serializable
internal data class ReadinessData(
    val projectName: String,
    val dependencyAnalysis: KmpDependenciesAnalysisResult,
    val gradlePlugins: AppliedGradlePlugins,
    val sourceSets: SourceSetSearcherResult,
    val androidLibraryInfo: AndroidLibraryInfo,
) {
    override fun toString(): String {
        return JsonSerializer.json.encodeToString(serializer(), this)
    }
}
