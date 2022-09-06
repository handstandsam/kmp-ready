package com.handstandsam.kmpreadiness.internal.models

import kotlinx.serialization.Serializable

internal interface HasGav {
    val gav: Gav
}

@Serializable
internal sealed class KmpReadyResult {
    @Serializable

    sealed class Allowed : HasGav, KmpReadyResult() {

        @Serializable
        data class FromRemote(
            override val gav: Gav,
            val metadataUrl: String,
        ) : Allowed()

        @Serializable
        data class Excluded(
            override val gav: Gav,
        ) : Allowed()
    }

    @Serializable
    data class NotAllowed(
        override val gav: Gav,
        val attemptedUrls: List<String> = listOf(),
    ) : HasGav, KmpReadyResult()
}

