package com.handstandsam.kmpready.internal.models

import kotlinx.serialization.Serializable

@Serializable
public data class Gav(val group: String, val artifact: String, val version: String) {
    val id: String = buildString {
        append(group)
        append(":$artifact")
        append(":$version")
    }

    public companion object {
        public fun fromString(str: String): Gav? {
            return if (str.isNotBlank()) {
                try {
                    val gav = str.split(":")
                    val group = gav[0]
                    val artifact = gav[1]
                    val version = gav[2]
                    Gav(
                        group = group,
                        artifact = artifact,
                        version = version
                    )
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }
}