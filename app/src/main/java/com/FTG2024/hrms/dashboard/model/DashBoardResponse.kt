package com.FTG2024.hrms.dashboard.model

data class DashBoardResponse(
    val attendenceData: List<AttendenceData>,
    val birthDayData: List<Any>,
    val code: Int,
    val eomData: List<EomData>,
    val message: String
)