package com.FTG2024.hrms.retrofit

import android.util.Log
import com.FTG2024.hrms.application.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthTokenKeyInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("####", "intercept: ${tokenManager.getToken()}")
        val request = chain.request()
        val newRequest = request.newBuilder()
            .addHeader("token", "${tokenManager.getToken()}")
            .build()
        return chain.proceed(newRequest)
    }
}