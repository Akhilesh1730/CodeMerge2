package com.FTG2024.hrms.markattendance.markattendancerepo

import com.FTG2024.hrms.background.HourLocationRequest
import com.FTG2024.hrms.background.LocationBackgroundResponse
import com.FTG2024.hrms.base.EmpIdRequest
import com.FTG2024.hrms.base.LocationResponse
import com.FTG2024.hrms.markattendance.UploadImageResponse
import com.FTG2024.hrms.markattendance.model.DayInRequest
import com.FTG2024.hrms.markattendance.model.DayOutRequest
import com.FTG2024.hrms.markattendance.model.MarkResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MarkAttendanceApiService {

    @Multipart
    @POST("/upload/dayinImg")
    suspend fun uploadDayInSelfie(
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse>

    @Multipart
    @POST("/upload/dayoutImg")
    suspend fun uploadDayOutSelfie(
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse>

    @POST("/api/attendence/dayOut")
    suspend fun markDayOut(
        @Body request: DayOutRequest
    ): Response<MarkResponse>

    @POST("/api/attendence/dayIn")
    suspend fun markDayIn(
        @Body request: DayInRequest
    ): Response<MarkResponse>

    @POST("/api/attendence/getLocation")
    suspend fun getLocation(
        @Body request: EmpIdRequest
    ): Response<LocationResponse>


}