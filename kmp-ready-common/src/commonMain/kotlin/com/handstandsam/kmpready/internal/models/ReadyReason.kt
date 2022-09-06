package com.handstandsam.kmpready.internal.models

public enum class ReadyReasonType {
    MultiplatformPluginAlreadyEnabled,
    HasOnlyKotlinFiles,
    HasOnlyMultiplatformCompatibleDependencies,
    KotlinPluginEnabled
}

public enum class NotReadyReasonType {
    HasJavaFiles,
    IncompatibleDependencies,
    DoesNotHaveKotlinJvmOrMultiplatformPlugin,
    IsAndroidApplication,
    IsAndroidLibrary,
    UsesJavaBaseImports
}

public sealed class Reason {
    data class ReadyReason(val type: ReadyReasonType, val details: String? = null) :
        Reason()

    data class NotReadyReason(
        val type: NotReadyReasonType,
        val details: String? = null
    ) : Reason()
}