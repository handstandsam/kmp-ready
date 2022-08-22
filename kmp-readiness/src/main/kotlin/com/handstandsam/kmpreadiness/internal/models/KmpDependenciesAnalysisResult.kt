package com.handstandsam.kmpreadiness.internal.models

import kotlinx.serialization.Serializable

@Serializable
internal data class KmpDependenciesAnalysisResult(
    val compatible: List<String>,
    val incompatible: List<String>,
) {
    val hasOnlyMultiplatformCompatibleDependencies: Boolean
        get() {
            return incompatible.isEmpty()
        }
}