package com.FTG2024.hrms.login.repo

import com.FTG2024.hrms.login.model.LoginResponse
import com.FTG2024.hrms.login.model.LoginRequest
import com.FTG2024.hrms.profile.model.ProfileEmployeeDetailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginActivityApiService {
    @POST("/employee/login")
    suspend fun authUser(@Body request : LoginRequest) : Response<LoginResponse>

    @POST("/api/employee/get")
    suspend fun getProfileDetails(): Response<ProfileEmployeeDetailResponse>

}