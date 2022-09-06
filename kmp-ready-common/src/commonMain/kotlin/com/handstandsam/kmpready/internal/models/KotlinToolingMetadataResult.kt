package com.handstandsam.kmpready.internal.models

import kotlinx.serialization.json.JsonObject

internal sealed class KotlinToolingMetadataResult {
    internal class Success(internal val url: String, internal val json: JsonObject) : KotlinToolingMetadataResult()
    internal class NotFound() : KotlinToolingMetadataResult()
}