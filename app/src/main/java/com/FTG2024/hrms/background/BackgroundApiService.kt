package com.FTG2024.hrms.background

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BackgroundApiService {
    @POST("/api/location/insertLocations")
    suspend fun sendLocation(
        @Body request: HourLocationRequest?
    ): Response<LocationBackgroundResponse>
}