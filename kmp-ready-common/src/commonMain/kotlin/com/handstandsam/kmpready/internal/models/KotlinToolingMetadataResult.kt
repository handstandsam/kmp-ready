package com.handstandsam.kmpready.internal.models

import kotlinx.serialization.json.JsonObject

public sealed class KotlinToolingMetadataResult {
    public class Success(public val url: String, public val json: JsonObject) : KotlinToolingMetadataResult()
    public class NotFound() : KotlinToolingMetadataResult()
}