package com.handstandsam.kmpready.internal.models

import kotlinx.serialization.Serializable

@Serializable
internal data class Gav(val group: String, val artifact: String, val version: String) {
    val id: String = buildString {
        append(group)
        append(":$artifact")
        append(":$version")
    }

    internal companion object {
        internal fun fromString(str: String): Gav? {
            return if (str.isNotBlank()) {
                val gav = str.split(":")
                val group = gav[0]
                val artifact = gav[1]
                val version = gav[2]
                Gav(
                    group = group,
                    artifact = artifact,
                    version = version
                )
            } else {
                null
            }
        }
    }
}