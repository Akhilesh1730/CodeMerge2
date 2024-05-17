package com.FTG2024.hrms.retrofit

import com.FTG2024.hrms.application.TokenManager
import com.example.myapplication.apiservice.apiInterface
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitHelper {
    private val BASE_URL = "https://0308-2401-4900-1b8b-fa4f-5446-2909-ade7-24f9.ngrok-free.app"
    private val API_KEY = "w9OAxS4zZXSvub0rVCZf5mt4v6K66pj2"

    fun getRetrofitInstance(tokenManager: TokenManager): Retrofit {

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(ApiKeyInterceptor(API_KEY))
            .addInterceptor(AuthTokenKeyInterceptor(tokenManager))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //val apiServiceCustomer: addCustomerApiservice = retrofit.create(addCustomerApiservice::class.java)

    //val apiService: apiInterface = getRetrofitInstance().create(apiInterface::class.java)
}