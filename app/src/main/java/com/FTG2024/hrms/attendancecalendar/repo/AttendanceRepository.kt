package com.FTG2024.hrms.attendancecalendar.repo

import android.util.Log
import com.FTG2024.hrms.uidata.Event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.FTG2024.hrms.attendancecalendar.model.AttendanceCalendarRequest
import com.FTG2024.hrms.uidata.Response

class AttendanceRepository(private val apiService: AttendanceApiService)  {
    private var _attendanceCalendarMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val attendanceCalendarMutableLiveData : LiveData<Event<Response>>
        get() = _attendanceCalendarMutableLiveData

    private var _userDetailsMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val userDetailsMutableLiveData : LiveData<Event<Response>>
        get() = _userDetailsMutableLiveData

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

    suspend fun getEmployeeProfile() {
        val response = apiService.getProfileDetails()
        Log.d("####", "getEmployeeProfile: $response")
        if (response.isSuccessful) {
            val profileEmployeeDetailResponse = response.body()
            if (profileEmployeeDetailResponse!!.code == 200) {
                _userDetailsMutableLiveData.postValue(Event(Response.Success(profileEmployeeDetailResponse)))
            } else {
                _userDetailsMutableLiveData.postValue(Event(Response.Exception("Unable to login")))
            }
        } else {
            _userDetailsMutableLiveData.postValue(Event(Response.Exception("Unable to login")))
        }
    }
}