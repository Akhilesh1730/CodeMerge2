package com.FTG2024.hrms.dashboard.repo

import com.FTG2024.hrms.base.EmpIdRequest
import com.FTG2024.hrms.base.LocationResponse
import com.FTG2024.hrms.dashboard.model.DashBoardResponse
import com.FTG2024.hrms.profile.model.ProfileEmployeeDetailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DashboardApiService {
    @POST("/api/attendence/getDashboardData")
    suspend fun getDashData(@Body request: EmpIdRequest): Response<DashBoardResponse> // Modify the return type based on your API response

    @POST("/api/attendence/getLocation")
    suspend fun getLocation(
        @Body request: EmpIdRequest
    ): Response<LocationResponse>
}