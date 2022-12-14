package com.handstandsam.kmpready.internal.models

internal enum class ReadyReasonType {
    MultiplatformPluginAlreadyEnabled,
    HasOnlyKotlinFiles,
    HasOnlyMultiplatformCompatibleDependencies,
    KotlinPluginEnabled
}

internal enum class NotReadyReasonType {
    HasJavaFiles,
    IncompatibleDependencies,
    DoesNotHaveKotlinJvmOrMultiplatformPlugin,
    IsAndroidApplication,
    IsAndroidLibrary,
    UsesJavaBaseImports
}

internal sealed class Reason {
    internal data class ReadyReason(val type: ReadyReasonType, val details: String? = null) :
        Reason()

    internal data class NotReadyReason(
        val type: NotReadyReasonType,
        val details: String? = null
    ) : Reason()
}