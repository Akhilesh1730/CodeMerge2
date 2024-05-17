package com.FTG2024.hrms.markattendance.model

data class DayInRequest(
    val EMP_ID: Int,
    val DAYIN_LOCATION: String,
    val DAYIN_DISTANCE: String,
    val DAYIN_DEVICE_ID: String,
    val DAYIN_IMG_URL: String,
    val DAYIN_REMARK: String
)