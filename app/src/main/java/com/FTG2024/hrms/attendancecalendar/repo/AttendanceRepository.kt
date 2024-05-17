package com.FTG2024.hrms.attendancecalendar.repo

import android.util.Log
import com.FTG2024.hrms.uidata.Event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.FTG2024.hrms.attendancecalendar.model.AttendanceCalendarRequest
import com.FTG2024.hrms.uidata.Response

class AttendanceRepository(private val apiService: AttendanceApiService)  {
    private var _attendanceCalendarMutableLiveData: MutableLiveData<com.FTG2024.hrms.uidata.Event<Response>> = MutableLiveData()
    val attendanceCalendarMutableLiveData : LiveData<com.FTG2024.hrms.uidata.Event<Response>>
        get() = _attendanceCalendarMutableLiveData

    suspend fun getAttendanceCalendar(request: AttendanceCalendarRequest) {
        val response = apiService.getAttendanceCalendar(request)
        Log.d("####", "getAttendanceCalendar: ${response.body()} ${request.MONTH} ${request.YEAR}")
        if (response.isSuccessful) {
            val attendanceCalendarResponse = response.body()
            if (attendanceCalendarResponse!!.code == 200) {
                _attendanceCalendarMutableLiveData.postValue(Event(Response.Success(attendanceCalendarResponse)))
            } else {
                _attendanceCalendarMutableLiveData.postValue(Event(Response.Exception(attendanceCalendarResponse.message)))
            }
        } else {
            _attendanceCalendarMutableLiveData.postValue(Event(Response.Exception("Failed to load Data")))
        }
    }
}