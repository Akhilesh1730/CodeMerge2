package com.FTG2024.hrms.customers.repo

import com.FTG2024.hrms.customers.model.createcutomers
import com.FTG2024.hrms.customers.model.customer_model
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface addCustomerApiservice {


    @POST("/api/customer/get")
    suspend fun getCustomers(): Response<customer_model>


    @POST("/api/customer/create")
    suspend fun PostCustomers(@Body requestBody: createcutomers): Response<customer_model>


}