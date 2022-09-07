package com.handstandsam.kmpready.internal

import com.handstandsam.kmpready.internal.models.Gav
import com.handstandsam.kmpready.internal.models.KotlinToolingMetadataResult
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readText
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.http.pathComponents
import kotlinx.serialization.json.JsonObject

public class SearchRemote(
    public val httpClient: HttpClient,
) {

    public companion object {
        public fun getUrlForKotlinToolingMetadata(mavenRepository: String, gav: Gav): String {
            val url = Url(mavenRepository)
            return URLBuilder(url).apply {
                val pathSegments = mutableListOf<String>()
                gav.group.split(".").forEach {
                    pathSegments.add(it)
                }
                pathSegments.add(gav.artifact)
                pathSegments.add(gav.version)
                pathSegments.add("${gav.artifact}-${gav.version}-kotlin-tooling-metadata.json")
                appendPathSegments(pathSegments)
            }.buildString()
        }
    }

    public suspend fun searchForInRepo(kotlinToolingMetadataUrlString: String): KotlinToolingMetadataResult {
        println("Looking for kotlin-tooling-metadata.json: $kotlinToolingMetadataUrlString")
        try {
            val httpResponse = httpClient.request(Url(kotlinToolingMetadataUrlString))
            if (httpResponse.status.value in 200..299) {
                val jsonResponse = httpResponse.bodyAsText()
                return KotlinToolingMetadataResult.Success(
                    url = kotlinToolingMetadataUrlString,
                    json = JsonSerializer.json.decodeFromString(JsonObject.serializer(), jsonResponse),
                )
                // TODO Do Something Meaningful with this kotlin-tooling-metadata
                // when (val kotlinToolingMetadataResult = KotlinToolingMetadata.parseJson(jsonResponse)) {
                //     is KotlinToolingMetadataParsingResult.Success -> {
                //         val kotlinToolingMetadata = kotlinToolingMetadataResult.value
                //         println("$gav has $kotlinToolingMetadata")
                //         return KotlinToolingMetadataResult.Success(
                //             url = kotlinToolingMetadataUrlString,
                //         )
                //     }
                //
                //     is KotlinToolingMetadataParsingResult.Failure -> {
                //         println("Tooling Metadata found, but unable to parse.")
                //         // return KotlinToolingMetadataResult.Success(
                //         //     url = kotlinToolingMetadataUrlString,
                //         // )
                //         return KotlinToolingMetadataResult.NotFound()
                //     }
                // }
            }
        } catch (e: Exception) {
            // Timeout, We'll Return NotFound Below
        }
        return KotlinToolingMetadataResult.NotFound()
    }
}
