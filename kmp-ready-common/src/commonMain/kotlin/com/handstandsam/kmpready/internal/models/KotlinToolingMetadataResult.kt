package com.handstandsam.kmpready.internal.models

import kotlinx.serialization.json.JsonObject

public sealed class KotlinToolingMetadataResult {
    class Success(val url: String, val json: JsonObject) : KotlinToolingMetadataResult()
    class NotFound() : KotlinToolingMetadataResult()
}