package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpreadiness.internal.models.Gav
import com.handstandsam.kmpreadiness.internal.models.KmpReadyResult
import com.handstandsam.kmpreadiness.internal.models.SearchResponse
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

internal object JsonSerializer {
    val json: Json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }
}

internal class MavenSearchRemote(
    val httpClient: OkHttpClient = OkHttpClient.Builder().build()
) {

    suspend fun searchFor(gav: Gav): KmpReadyResult {
        val request = Request.Builder().apply {

            val httpUrl = HttpUrl.Builder().apply {
                scheme("https")
                host("search.maven.org")
                addPathSegment("solrsearch")
                addPathSegment("select")
                val mavenSearchQuery = buildString {
                    append("g:${gav.group}")
                    append(" AND ")
                    append("a:${gav.artifact}")
                    append(" AND ")
                    append("v:${gav.version}")
                    append(" AND ")
                    append("p:jar")
                    append(" AND ")
                    append("l:kotlin-tooling-metadata")
                }
                addQueryParameter("q", mavenSearchQuery)
                addQueryParameter("rows", "1")
                addQueryParameter("wt", "json")
            }.build()
            url(httpUrl)
        }.build()
        val httpResponse = httpClient.newCall(request).execute()

        println("Making Request To: ${request.url.toUrl()}")

        if (httpResponse.code in 200..299) {
            val jsonResponse = httpResponse.body!!.string()
            val searchResponse: SearchResponse =
                JsonSerializer.json.decodeFromString(SearchResponse.serializer(), jsonResponse)
            val isReady = searchResponse.response.numFound == 1
            return KmpReadyResult(
                gav = gav,
                isReady = isReady,
            )
        } else {
            return KmpReadyResult(
                gav = gav,
                isReady = false,
            )
        }
    }
}
