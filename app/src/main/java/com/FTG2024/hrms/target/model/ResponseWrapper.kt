package com.FTG2024.hrms.target.model

data class ResponseWrapper(
    val code: Int,
    val message: String,
    val pages: Int,
    val count: Int,
    val data: List<Data>
)

data class Data(
    val EMP_ID: Int,
    val EMPLOYEE_NAME: String,
    val TOTAL_AMOUNT: Int,
    val TOTAL_CUSTOMER_CREATED: Int
)

