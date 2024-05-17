package com.FTG2024.hrms.attendancecalendar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.FTG2024.hrms.attendancecalendar.model.AttendanceCalendarRequest
import com.FTG2024.hrms.attendancecalendar.repo.AttendanceRepository
import com.FTG2024.hrms.uidata.Event
import com.FTG2024.hrms.uidata.Response
import kotlinx.coroutines.launch

class AttendanceViewModel(private val repository: AttendanceRepository) : ViewModel() {
    fun getAttendanceCalendarData(request: AttendanceCalendarRequest) {
        viewModelScope.launch {
            repository.getAttendanceCalendar(request)
        }
    }

    fun getAttendanceCalendarLiveData(): LiveData<Event<Response>> {
        return repository.attendanceCalendarMutableLiveData
    }
}