package com.FTG2024.hrms.customers.model

data class ServiceInterestRequest(
    val ID: Int,
    val CUSTOMER_ID: Int,
    val SERVICE_ID: Int,
    val DESCRIPTION: Any,
    val STATUS  : Int,
    val AMOUNT: Int,

)



data class responsePUT(
    val code: Int,
    val message: String,



)