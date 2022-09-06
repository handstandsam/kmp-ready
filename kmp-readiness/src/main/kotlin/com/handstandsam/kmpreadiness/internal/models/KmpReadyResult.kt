package com.handstandsam.kmpreadiness.internal.models

import kotlinx.serialization.Serializable

@Serializable
internal sealed class KmpReadyResult() {
    @Serializable

    sealed class Allowed : KmpReadyResult() {

        @Serializable
        data class FromRemote(
            val gav: Gav,
            val metadataUrl: String,
        ) : Allowed()

        @Serializable
        data class Excluded(
            val gav: Gav,
        ) : Allowed()

        @Serializable
        data class FromCache(
            val gav: Gav,
        ) : Allowed()
    }

    @Serializable
    sealed class NotAllowed : KmpReadyResult() {
        @Serializable
        data class FromCache(
            val gav: Gav,
        ) : NotAllowed()

        @Serializable
        data class FromRemote(
            val gav: Gav,
            val attemptedUrls: List<String> = listOf(),
        ) : NotAllowed()
    }
}

