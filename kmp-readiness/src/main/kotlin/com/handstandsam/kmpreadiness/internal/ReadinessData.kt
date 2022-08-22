package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpreadiness.internal.models.KmpDependenciesAnalysisResult
import com.handstandsam.kmpreadiness.internal.models.ReadinessResult
import com.handstandsam.kmpreadiness.internal.models.ReadyReason
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class AppliedGradlePlugins(
    val kotlin: Boolean,
    val multiplatform: Boolean,
    val java: Boolean,
)

@Serializable
internal data class ReadinessData(
    val projectName: String,
    val dependencyAnalysisResult: KmpDependenciesAnalysisResult,
    val gradlePlugins: AppliedGradlePlugins,
    val sourceSetSearcherResult: SourceSetSearcherResult,
) {

    fun computeReadiness(): ReadinessResult {
        return if (gradlePlugins.multiplatform) {
            ReadinessResult.Ready(readyReason = ReadyReason.AlreadyEnabled, this)
        } else if (gradlePlugins.kotlin) {
            if (sourceSetSearcherResult.hasOnlyKotlinFiles) {
                if (dependencyAnalysisResult.hasOnlyMultiplatformCompatibleDependencies) {
                    ReadinessResult.Ready(readyReason = ReadyReason.Compatible, this)
                } else {
                    ReadinessResult.NotReady("Incompatible Dependencies", this)
                }
            } else {
                ReadinessResult.NotReady("Contains Java Files", this)
            }
        } else {
            ReadinessResult.NotReady("Does Not Have the Kotlin JVM or Multiplatform Plugin", this)
        }
    }

    override fun toString(): String {
        return Json { prettyPrint = true }.encodeToString(serializer(), this)
    }
}