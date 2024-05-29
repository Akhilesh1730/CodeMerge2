package com.FTG2024.hrms.customers

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    val code: Int,
    val message: String,
    val pages: Int,
    val count: Int,
    val data: List<ServiceInterest>
)

data class ServiceInterest(
    @SerializedName("ID") val id: Int,
    @SerializedName("CUSTOMER_ID") val customerId: Int?,
    @SerializedName("SERVICE_ID") val serviceId: Int?,
    @SerializedName("AMOUNT") val amount: Double?,
    @SerializedName("DESCRIPTION") val description: String?,
    @SerializedName("STATUS") val status: Int?,
    @SerializedName("INTERESTED_DATETIME") val interestedDatetime: String?,
    @SerializedName("SERVICE_NAME") val serviceName: String?,
    @SerializedName("CREATED_MODIFIED_DATE") val createdModifiedDate: String?,
    @SerializedName("CUSTOMER_NAME") val customerName: String?,
    @SerializedName("IS_CONVERTED") val is_converted: String?
)
