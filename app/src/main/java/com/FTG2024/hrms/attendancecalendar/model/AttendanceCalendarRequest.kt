package com.FTG2024.hrms.attendancecalendar.model

data class AttendanceCalendarRequest(
    val EMP_ID: Int,
    val MONTH: String,
    val YEAR: String
)