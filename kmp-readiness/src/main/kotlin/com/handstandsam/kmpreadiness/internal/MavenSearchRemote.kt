package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpreadiness.internal.models.Gav
import com.handstandsam.kmpreadiness.internal.models.KmpReadyResult
import com.handstandsam.kmpreadiness.internal.models.SearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal object JsonSerializer {
    val json: Json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }
}

internal class MavenSearchRemote(
    val httpClient: HttpClient = HttpClient(OkHttp) {
        install(Logging)
        install(ContentNegotiation) {
            json(JsonSerializer.json)
        }
    }
) {

    suspend fun searchFor(gav: Gav): KmpReadyResult {
        val httpResponse = httpClient.get("https://search.maven.org/solrsearch/select") {
            url {
                val q = buildString {
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
                parameter("q", q)
                parameter("rows", 1)
                parameter("wt", "json")
            }
            println("Making Request To: ${this.build().url}")
        }

        if (httpResponse.status.value in 200..299) {
            val searchResponse = httpResponse.body<SearchResponse>()
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
