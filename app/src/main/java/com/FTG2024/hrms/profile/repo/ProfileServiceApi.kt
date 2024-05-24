package com.FTG2024.hrms.profile.repo

import com.FTG2024.hrms.markattendance.UploadImageResponse
import com.FTG2024.hrms.profile.model.ProfileEmployeeDetailResponse
import com.FTG2024.hrms.uidata.Event
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProfileServiceApi {
    @Multipart
    @POST("/upload/employeeProfile")
    suspend fun uploadProfileImage(
        @Part image: MultipartBody.Part
    ): Response<UploadImageResponse>

    @GET("/static/employeeProfile/20240521.jpg")
    suspend fun getProfilePic()


}