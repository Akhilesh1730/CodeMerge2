package com.FTG2024.hrms.target.repo

import com.FTG2024.hrms.target.model.ResponseWrapper
import com.FTG2024.hrms.target.model.targetbodyModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface targetApiService {
    @POST("/api/reports/getEmployeeTargetReport")
    suspend fun getTarget(@Body requestBody: targetbodyModel): Response<ResponseWrapper>
}
