package com.example.myapplication.apiservice

import com.example.myapplication.models.AssetRequestBody
import com.example.myapplication.models.assetCategory
import com.example.myapplication.models.assets
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface apiInterface {

    @POST("api/asset/get")
    suspend fun getAsset() : Response<assets>

    @POST("api/assetCategory/get")
        suspend fun getassetCategory() : Response<assetCategory>


    @POST("api/asset/create")
    suspend fun postAsset(@Body requestBody: AssetRequestBody): Response<assets>



}