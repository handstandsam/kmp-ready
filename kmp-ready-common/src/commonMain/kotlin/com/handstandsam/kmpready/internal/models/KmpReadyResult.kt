package com.handstandsam.kmpready.internal.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

public interface HasGav {
    val gav: Gav
}

@Serializable
public sealed class KmpReadyResult {
    @Serializable

    sealed class Allowed : HasGav, KmpReadyResult() {

        @Serializable
        data class FromRemote(
            override val gav: Gav,
            val metadataUrl: String,
            val kotlinToolingMetadataJson: JsonObject,
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

