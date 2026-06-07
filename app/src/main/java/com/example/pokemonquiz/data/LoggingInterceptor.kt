package com.example.pokemonquiz.data

import com.apollographql.apollo3.api.http.HttpRequest
import com.apollographql.apollo3.api.http.HttpResponse
import com.apollographql.apollo3.network.http.HttpInterceptor
import com.apollographql.apollo3.network.http.HttpInterceptorChain
import com.example.pokemonquiz.util.LogUtil
import okio.Buffer

class LoggingInterceptor : HttpInterceptor {

    override suspend fun intercept(
        request: HttpRequest,
        chain: HttpInterceptorChain
    ): HttpResponse {
        val startMs = System.currentTimeMillis()

        // Log request: method, url, headers, and first 500 chars of body
        LogUtil.d(TAG, "--> ${request.method.name} ${request.url}")
        request.headers.forEach { (name, value) ->
            LogUtil.d(TAG, "    $name: $value")
        }
        request.body?.let { body ->
            val preview = body.readPreview(MAX_BODY_PREVIEW)
            LogUtil.d(TAG, "    Body(${body.contentLength}b): $preview")
        }

        // Execute
        val response = chain.proceed(request)

        val elapsed = System.currentTimeMillis() - startMs

        // Log response: status, url, time — only headers, body is consumed by Apollo
        LogUtil.d(TAG, "<-- ${response.statusCode} ${request.url} (${elapsed}ms)")
        response.headers.forEach { (name, value) ->
            LogUtil.d(TAG, "    $name: $value")
        }

        return response
    }

    companion object {
        private const val TAG = "GraphQL"
        private const val MAX_BODY_PREVIEW = 500
    }
}

/**
 * Read up to [maxChars] characters from request body for log preview.
 */
private fun com.apollographql.apollo3.api.http.HttpBody.readPreview(maxChars: Int): String {
    // Write body bytes into a buffer, then read as text
    val buffer = Buffer()
    writeTo(buffer)
    return buffer.readUtf8().take(maxChars)
}

