package com.handstandsam.kmpreadiness.internal

import com.handstandsam.kmpreadiness.internal.models.Gav
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.kotlin.tooling.KotlinToolingMetadata
import org.jetbrains.kotlin.tooling.KotlinToolingMetadataParsingResult
import org.jetbrains.kotlin.tooling.parseJson
import java.net.SocketTimeoutException
import java.time.Duration

internal object JsonSerializer {
    val json: Json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }
}

internal class SearchRemote(
    val httpClient: OkHttpClient = OkHttpClient.Builder().callTimeout(Duration.ofSeconds(10)).build()
) {

    companion object {
        fun getUrlForKotlinToolingMetadata(mavenRepository: String, gav: Gav): String {
            return mavenRepository.toHttpUrl().newBuilder().apply {
                gav.group.split(".").forEach {
                    addPathSegment(it)
                }
                addPathSegment(gav.artifact)
                addPathSegment(gav.version)
                addPathSegment("${gav.artifact}-${gav.version}-kotlin-tooling-metadata.json")
            }.build().toUrl().toString()
        }
    }

    internal sealed class KotlinToolingMetadataResult {
        internal class Success(val url: String) : KotlinToolingMetadataResult()
        internal class NotFound() : KotlinToolingMetadataResult()
    }

    suspend fun searchForInRepo(kotlinToolingMetadataUrlString: String, gav: Gav): KotlinToolingMetadataResult {
        println("Looking for kotlin-tooling-metadata.json: $kotlinToolingMetadataUrlString")

        val request = Request.Builder().apply { url(kotlinToolingMetadataUrlString.toHttpUrl()) }.build()

        try {
            val httpResponse = httpClient.newCall(request).execute()
            if (httpResponse.code in 200..299) {
                val jsonResponse = httpResponse.body!!.string()
                when (val kotlinToolingMetadataResult = KotlinToolingMetadata.parseJson(jsonResponse)) {
                    is KotlinToolingMetadataParsingResult.Success -> {
                        val kotlinToolingMetadata = kotlinToolingMetadataResult.value
                        // TODO Do Something Meaningful with this kotlin-tooling-metadata
                        println("$gav has $kotlinToolingMetadata")
                        return KotlinToolingMetadataResult.Success(
                            url = kotlinToolingMetadataUrlString,
                        )
                    }

                    is KotlinToolingMetadataParsingResult.Failure -> {
                        println("Tooling Metadata found, but unable to parse.")
                        // return KotlinToolingMetadataResult.Success(
                        //     url = kotlinToolingMetadataUrlString,
                        // )
                        return KotlinToolingMetadataResult.NotFound()
                    }
                }
            }
        } catch (e: SocketTimeoutException) {
            // Timeout, We'll Return NotFound Below
        }
        return KotlinToolingMetadataResult.NotFound()
    }
}
