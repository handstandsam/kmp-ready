package com.handstandsam.kmpready.internal.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

internal interface HasGav {
    val gav: Gav
}

@Serializable
internal sealed class KmpReadyResult {
    @Serializable

    internal sealed class Allowed : HasGav, KmpReadyResult() {

        @Serializable
        internal data class FromRemote(
            override val gav: Gav,
            val metadataUrl: String,
            val kotlinToolingMetadataJson: JsonObject,
        ) : Allowed()

        @Serializable
        internal data class Excluded(
            override val gav: Gav,
        ) : Allowed()
    }

    @Serializable
    internal data class NotAllowed(
        override val gav: Gav,
        val attemptedUrls: List<String> = listOf(),
    ) : HasGav, KmpReadyResult()
}

