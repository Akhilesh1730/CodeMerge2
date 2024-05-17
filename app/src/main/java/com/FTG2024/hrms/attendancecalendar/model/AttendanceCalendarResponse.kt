package com.FTG2024.hrms.attendancecalendar.model

data class AttendanceCalendarResponse(
    val code: Int,
    val data: List<Data>,
    val holidays: List<Holiday>,
    val message: String
)