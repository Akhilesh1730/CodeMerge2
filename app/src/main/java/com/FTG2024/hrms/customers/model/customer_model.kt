package com.FTG2024.hrms.customers.model

data class customer_model(
    val code: Int,
    val count: Int,
    val `data`: List<Data>,
    val message: String,
    val pages: Int
)


data class Data(
    val ADDRESS: String,
    val CITY: String,
    val CREATED_EMP_ID: Int,
    val CREATED_MODIFIED_DATE: String?,
    val DESCRIPTION: String,
    val DISTRICT: String,
    val EMAIL_ID: String,
    val FIRST_NAME: String,
    val ID: Int,
    val LAST_NAME: String,
    val MOBILE_NO: String,
    val PINCODE: String,
    val STATE_ID: Int,
    val STATUS: Int
)