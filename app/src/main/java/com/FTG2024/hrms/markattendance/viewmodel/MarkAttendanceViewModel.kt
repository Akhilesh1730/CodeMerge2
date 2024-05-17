package com.FTG2024.hrms.markattendance.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.FTG2024.hrms.base.EmpIdRequest
import com.FTG2024.hrms.markattendance.markattendancerepo.MarkAttendanceRepo
import com.FTG2024.hrms.markattendance.model.DayInRequest
import com.FTG2024.hrms.markattendance.model.DayOutRequest
import com.FTG2024.hrms.uidata.Event
import com.FTG2024.hrms.uidata.Response
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class MarkAttendanceViewModel(private val repo : MarkAttendanceRepo) : ViewModel(){
    fun markDayInEntry(request: DayInRequest, token : String) {
        viewModelScope.launch {
            repo.markDayInEntry(request, token)
        }
    }

     fun uploadSelfie(image: MultipartBody.Part) {
         viewModelScope.launch {
             repo.uploadSelfie(image)
         }
    }

    fun uploadDayInSelfie(image: MultipartBody.Part) {
        viewModelScope.launch {
            repo.uploadDayInSelfie(image)
        }
    }

    fun uploadDayOutSelfie(image: MultipartBody.Part) {
        viewModelScope.launch {
            repo.uploadDayOutSelfie(image)
        }
    }

    fun markDayOutEntry(request: DayOutRequest, token : String) {
        viewModelScope.launch {
            repo.markDayOutEntry(request, token)
        }
    }

    fun getWorkLocation(empIdRequest: EmpIdRequest, token : String) {
        viewModelScope.launch {
            repo.getWorkLocation(empIdRequest, token)
        }
    }

    fun getMarkInEntryLiveData(): LiveData<Event<Response>> {
        return repo.dayInLiveData
    }

    fun getMarkOutEntryLiveData(): LiveData<Event<Response>> {
        return repo.dayOutLiveData
    }

    fun getLocationLiveData(): LiveData<Event<Response>> {
        return repo.locationLiveData
    }

    fun getSelfieUploadedLiveData(): LiveData<Event<Response>> {
        return repo.selfieUploadedMutableLiveData
    }
}