package com.example.myapplication.addCustomer.repo

import com.FTG2024.hrms.customers.ApiResponse
import com.FTG2024.hrms.customers.model.Data
import com.FTG2024.hrms.customers.model.GetServiceIntrestModel
import com.FTG2024.hrms.customers.model.GetServiceResponse
import com.FTG2024.hrms.customers.model.ServiceInterestRequest
import com.FTG2024.hrms.customers.model.createcutomers
import com.FTG2024.hrms.customers.model.customer_model
import com.FTG2024.hrms.customers.model.responsePUT
import com.example.myapplication.addCustomer.model.GetID
import okhttp3.ResponseBody
import retrofit2.Call


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

interface addCustomerApiservice {


    @POST("/api/customer/get")
    suspend fun getCustomers(@Body requestBody: GetID): Response<customer_model>

    @POST("/api/customer/create")
    suspend fun PostCustomers(@Body requestBody: createcutomers): Response<customer_model>


    @POST("/api/serviceInterest/cancelInterest")
    suspend fun cancelServiceInterest(@Body requestBody: Map<String, Int>): Response<Any>

    @POST("/api/serviceInterest/cancelInterest")
    suspend fun approvedServiceInterest(@Body requestBody: Map<String, Int>): Response<Any>

    @Headers("Content-Type: application/json")
    @POST("/api/serviceInterest/get")
    suspend fun getServiceInterests(@Body requestBody: Map<String, Int>): Response<ApiResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/service/get")
    suspend fun getServicedrop(): Response<GetServiceResponse>

    @Headers("Content-Type: application/json")
    @POST("/api/serviceInterest/create")
    suspend fun createServiceInterests(@Body request: ServiceInterestRequest): Response<ServiceInterestRequest>

    @Headers("Content-Type: application/json")
    @PUT("/api/customer/update")
    suspend fun updateCustomer(@Body requestBody: Data): Response<Data>

    @Headers("Content-Type: application/json")
    @PUT("/api/serviceInterest/update")
    suspend fun updateServiceInterest(@Body request: ServiceInterestRequest): Response<responsePUT>

}
