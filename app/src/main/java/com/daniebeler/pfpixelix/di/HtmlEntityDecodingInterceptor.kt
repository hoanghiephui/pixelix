package com.daniebeler.pfpixelix.di

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.jsoup.parser.Parser

class HtmlEntityDecodingInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val originalBody = response.body
        val decodedBody = originalBody?.string()?.let { decodeHtmlEntities(it) }
        return response.newBuilder()
            .body(decodedBody?.toResponseBody(response.body?.contentType()))
            .build()
    }
}

fun decodeHtmlEntities(input: String): String {
    return Parser.unescapeEntities(input, false)
}
