package com.handstandsam.kmpreadiness.internal.models

import kotlinx.serialization.Serializable

@Serializable
internal data class KmpDependenciesAnalysisResult(
    val all: List<String>,
    val compatible: List<KmpReadyResult.Allowed>,
    val incompatible: List<KmpReadyResult.NotAllowed>,
) {
    val hasOnlyMultiplatformCompatibleDependencies: Boolean
        get() {
            return incompatible.isEmpty()
        }
}