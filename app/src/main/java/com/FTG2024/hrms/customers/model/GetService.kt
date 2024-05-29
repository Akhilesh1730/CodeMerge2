package com.FTG2024.hrms.customers.model



data class Service(
    val ID: Int,
    val NAME: String,
    val STATUS: Int,
    val CREATED_MODIFIED_DATE: String
)

data class GetServiceResponse(
    val code: Int,
    val message: String,
    val pages: Int,
    val count: Int,
    val data: List<Service>
)



