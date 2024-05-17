package com.FTG2024.hrms.attendancecalendar.repo

import com.FTG2024.hrms.attendancecalendar.model.AttendanceCalendarRequest
import com.FTG2024.hrms.attendancecalendar.model.AttendanceCalendarResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AttendanceApiService {

    @POST("/api/attendenceCalender/getCalenderData")
    suspend fun getAttendanceCalendar(@Body request: AttendanceCalendarRequest)
            : Response<AttendanceCalendarResponse>
}