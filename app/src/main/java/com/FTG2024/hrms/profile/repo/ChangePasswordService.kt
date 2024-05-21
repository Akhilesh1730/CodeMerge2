package com.FTG2024.hrms.profile.repo

import com.FTG2024.hrms.profile.model.ChangePasswordData
import com.FTG2024.hrms.profile.model.ChangePasswordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChangePasswordService {
    @POST("/api/employee/changePassword")
    suspend fun changePassword(
        @Body details: ChangePasswordData
    ): Response<ChangePasswordResponse>
}