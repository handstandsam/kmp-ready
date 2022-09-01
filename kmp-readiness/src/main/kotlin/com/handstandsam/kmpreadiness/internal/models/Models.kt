package com.handstandsam.kmpreadiness.internal.models

import kotlinx.serialization.Serializable

@Serializable
internal data class Doc(
    val id: String,
    val ec: List<String>,
    val tags: List<String>
)

@Serializable
internal data class SearchResponseBody(
    val numFound: Int,
    val docs: List<Doc>
)

@Serializable
internal data class SearchResponseHeader(
    val status: Int,
    val QTime: Int
)

@Serializable
internal data class SearchResponse(
    val response: SearchResponseBody,
    val responseHeader: SearchResponseHeader
)

