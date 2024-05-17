package com.FTG2024.hrms.markattendance.markattendancerepo

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface MapService {
    @GET("staticmap")
    fun getMapImage(@Url url: String): Call<ResponseBody>
}