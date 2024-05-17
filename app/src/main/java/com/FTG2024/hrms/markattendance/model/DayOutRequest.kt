package com.FTG2024.hrms.markattendance.model

data class DayOutRequest(
    val EMP_ID: Int,
    val DAYOUT_LOCATION: String,
    val DAYOUT_DISTANCE: String,
    val DAYOUT_DEVICE_ID: String,
    val DAYOUT_IMG_URL: String,
    val DAYOUT_REMARK: String,

)