package com.handstandsam.kmpready.internal.models

import kotlinx.serialization.Serializable

@Serializable
public data class Doc(
    val id: String,
    val ec: List<String>,
    val tags: List<String>
)

@Serializable
public data class SearchResponseBody(
    val numFound: Int,
    val docs: List<Doc>
)

@Serializable
public data class SearchResponseHeader(
    val status: Int,
    val QTime: Int
)

@Serializable
public data class SearchResponse(
    val response: SearchResponseBody,
    val responseHeader: SearchResponseHeader
)

