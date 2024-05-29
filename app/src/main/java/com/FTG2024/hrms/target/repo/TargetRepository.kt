//package com.FTG2024.hrms.target.repo
//
//import com.FTG2024.hrms.retrofit.RetrofitHelper
//import com.FTG2024.hrms.target.model.targetbodyModel
//import com.FTG2024.hrms.uidata.Response
//import retrofit2.Retrofit
//import targetResponseModel
//
//class TargetRepository(private val retrofit: Retrofit) {
//    private val targetApiService = retrofit.create(targetApiService::class.java)
//
//    suspend fun getTargetData(requestBody: targetbodyModel): Response<targetResponseModel> {
//        return targetApiService.getTarget(requestBody)
//    }
//}
