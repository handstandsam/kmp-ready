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
    val kotlin: Boolean,
    val multiplatform: Boolean,
    val java: Boolean,
)

@Serializable
internal data class ReadinessData(
    val projectName: String,
    val dependencyAnalysis: KmpDependenciesAnalysisResult,
    val gradlePlugins: AppliedGradlePlugins,
    val sourceSets: SourceSetSearcherResult,
) {

    fun computeReadiness(): ReadinessResult {
        val reasons = mutableListOf<Reason>()
        if (dependencyAnalysis.hasOnlyMultiplatformCompatibleDependencies) {
            reasons.addReadyReason(ReadyReasonType.HasOnlyMultiplatformCompatibleDependencies)
        } else {
            reasons.addNotReadyReason(
                NotReadyReasonType.IncompatibleDependencies,
                buildString {
                    dependencyAnalysis.incompatible.forEach {
                        appendLine("* $it")
                    }
                }
            )
        }

        if (sourceSets.hasOnlyKotlinFiles) {
            reasons.addReadyReason(ReadyReasonType.HasOnlyKotlinFiles)
        } else {
            reasons.addNotReadyReason(
                NotReadyReasonType.HasJavaFiles,
                buildString {
                    sourceSets.javaFiles.forEach { file ->
                        appendLine("* $file")
                    }
                }
            )
        }

        if (gradlePlugins.multiplatform) {
            reasons.addReadyReason(ReadyReasonType.MultiplatformPluginAlreadyEnabled)
        } else if (gradlePlugins.kotlin) {
            reasons.addReadyReason(ReadyReasonType.KotlinPluginEnabled)
        } else {
            reasons.addNotReadyReason(NotReadyReasonType.DoesNotHaveKotlinJvmOrMultiplatformPlugin)
        }

        return ReadinessResult(
            reasons = reasons,
            readinessData = this
        )
    }

    override fun toString(): String {
        return Json { prettyPrint = true }.encodeToString(serializer(), this)
    }
}

private fun MutableList<Reason>.addReadyReason(readyReasonType: ReadyReasonType, details: String? = null) {
    this.add(
        Reason.ReadyReason(
            type = readyReasonType,
            details = details,
        )
    )
}

private fun MutableList<Reason>.addNotReadyReason(notReadyReasonType: NotReadyReasonType, details: String? = null) {
    this.add(
        Reason.NotReadyReason(
            type = notReadyReasonType,
            details = details,
        )
    )
}
