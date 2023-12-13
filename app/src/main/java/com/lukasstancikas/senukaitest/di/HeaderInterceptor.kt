package com.lukasstancikas.senukaitest.di

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestWithHeaders = chain.request().newBuilder()
            .addHeader(ACCEPT_HEADER_KEY, ACCEPT_HEADER_VALUE)
            .addHeader(CONTENT_TYPE_HEADER_KEY, CONTENT_TYPE_HEADER_VALUE)
            .build()

        return chain.proceed(requestWithHeaders)
    }

    companion object {
        private const val ACCEPT_HEADER_KEY = "accept"
        private const val CONTENT_TYPE_HEADER_KEY = "content-type"
        private const val ACCEPT_HEADER_VALUE = "application/json"
        const val CONTENT_TYPE_HEADER_VALUE = "application/json"
    }
}