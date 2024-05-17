package com.FTG2024.hrms.login.repo

import com.FTG2024.hrms.login.model.LoginResponse
import com.FTG2024.hrms.login.model.LoginRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginActivityApiService {
    @POST("/employee/login")
    suspend fun authUser(@Body request : LoginRequest) : LoginResponse
}