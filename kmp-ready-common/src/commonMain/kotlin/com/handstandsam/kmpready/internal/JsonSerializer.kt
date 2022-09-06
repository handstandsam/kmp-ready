package com.handstandsam.kmpready.internal

import kotlinx.serialization.json.Json

internal object JsonSerializer {
    internal val json: Json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }
}