package com.handstandsam.kmpready.internal.models

import kotlinx.serialization.Serializable

@Serializable
public data class KmpDependenciesAnalysisResult(
    val all: List<String>,
    val compatible: List<KmpReadyResult.Allowed>,
    val incompatible: List<KmpReadyResult.NotAllowed>,
) {
    val hasOnlyMultiplatformCompatibleDependencies: Boolean
        get() {
            return incompatible.isEmpty()
        }
}