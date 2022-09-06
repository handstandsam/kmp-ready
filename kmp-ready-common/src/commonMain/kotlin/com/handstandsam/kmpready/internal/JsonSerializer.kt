package com.handstandsam.kmpready.internal

import kotlinx.serialization.json.Json

public object JsonSerializer {
    val json: Json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }
}