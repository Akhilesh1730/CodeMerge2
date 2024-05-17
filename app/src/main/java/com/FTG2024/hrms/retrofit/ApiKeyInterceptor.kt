package com.FTG2024.hrms.retrofit

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class ApiKeyInterceptor(private val apiKey: String?) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        val request: Request = chain.request()
        val newRequest: Request = request.newBuilder()
            .addHeader("apikey", "$apiKey")
            .build()
        return chain.proceed(newRequest)
    }
}