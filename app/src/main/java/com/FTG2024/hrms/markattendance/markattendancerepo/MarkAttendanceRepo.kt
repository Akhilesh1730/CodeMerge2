package com.FTG2024.hrms.markattendance.markattendancerepo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.FTG2024.hrms.base.EmpIdRequest
import com.FTG2024.hrms.markattendance.model.DayInRequest
import com.FTG2024.hrms.markattendance.model.DayOutRequest
import com.FTG2024.hrms.uidata.Event
import com.FTG2024.hrms.uidata.Response
import okhttp3.MultipartBody

class MarkAttendanceRepo(private val apiService : MarkAttendanceApiService) {
    private var _locationMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val locationLiveData : LiveData<Event<Response>>
        get() = _locationMutableLiveData

    private var _dayInMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val dayInLiveData : LiveData<Event<Response>>
        get() = _dayInMutableLiveData

    private var _dayOutMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val dayOutLiveData : LiveData<Event<Response>>
        get() = _dayOutMutableLiveData

    private var _selfieUploadedMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val selfieUploadedMutableLiveData : LiveData<Event<Response>>
        get() = _selfieUploadedMutableLiveData

    suspend fun uploadSelfie(image: MultipartBody.Part) {
        val response = apiService.uploadDayInSelfie(image)
        if (response.isSuccessful) {
            val uploadImageResponse = response.body()
            if (uploadImageResponse!!.code == 200) {
                _selfieUploadedMutableLiveData.postValue(Event(Response.Success(uploadImageResponse)))
            } else {
                val msg = "Failed to Upload " + uploadImageResponse.message
                _selfieUploadedMutableLiveData.postValue(Event(Response.Exception(msg)))
            }
        } else {
            Log.d("####", "uploadSelfie: ${response.body()}")
            _selfieUploadedMutableLiveData.postValue(Event(Response.Exception("Failed to Upload")))
        }
    }

    suspend fun uploadDayInSelfie(image: MultipartBody.Part) {
        val response = apiService.uploadDayInSelfie(image)
        if (response.isSuccessful) {
            val uploadImageResponse = response.body()
            if (uploadImageResponse!!.code == 200) {
                _selfieUploadedMutableLiveData.postValue(Event(Response.Success(uploadImageResponse)))
            } else {
                val msg = "Failed to Upload " + uploadImageResponse.message
                _selfieUploadedMutableLiveData.postValue(Event(Response.Exception(msg)))
            }
        } else {
            Log.d("####", "uploadSelfie: ${response.body()}")
            _selfieUploadedMutableLiveData.postValue(Event(Response.Exception("Failed to Upload")))
        }
    }

    suspend fun uploadDayOutSelfie(image: MultipartBody.Part) {
        val response = apiService.uploadDayOutSelfie(image)
        if (response.isSuccessful) {
            val uploadImageResponse = response.body()
            if (uploadImageResponse!!.code == 200) {
                _selfieUploadedMutableLiveData.postValue(Event(Response.Success(uploadImageResponse)))
            } else {
                val msg = "Failed to Upload " + uploadImageResponse.message
                _selfieUploadedMutableLiveData.postValue(Event(Response.Exception(msg)))
            }
        } else {
            Log.d("####", "uploadSelfie: ${response.body()}")
            _selfieUploadedMutableLiveData.postValue(Event(Response.Exception("Failed to Upload")))
        }
    }

    suspend fun markDayInEntry(request: DayInRequest, token : String) {
        val headers = HashMap<String, String>()
        val response = apiService.markDayIn(request)
        Log.d("####", "markDayInEntry: ${response.body()}")
        if (response.isSuccessful) {
            val markResponse = response.body()
            if (markResponse!!.code == 200) {
                _dayInMutableLiveData.postValue(Event(Response.Success(markResponse)))
            } else {
                _dayInMutableLiveData.postValue(Event(Response.Exception(markResponse.message)))
            }
        } else {
            Log.d("####", "markDayInEntry: ${response.body()}")
            _dayInMutableLiveData.postValue(Event(Response.Exception("Enable to mark your entry. Please try again")))
        }
    }

    suspend fun markDayOutEntry(request: DayOutRequest, token : String) {
        val headers = HashMap<String, String>()
        headers["AUTHORIZATION"] = "Bearer $token"
        val response = apiService.markDayOut(request)
        Log.d("###", "markDayOutEntry: ${response.body()}")
        if (response.isSuccessful) {
            val markResponse = response.body()
            if (markResponse!!.code == 200) {
                _dayOutMutableLiveData.postValue(Event(Response.Success(markResponse)))
            } else {
                _dayOutMutableLiveData.postValue(Event(Response.Exception(markResponse.message)))
            }
        } else {
            _dayOutMutableLiveData.postValue(Event(Response.Exception("Enable to mark your entry. Please try again")))
        }
    }

    suspend fun getWorkLocation(empIdRequest: EmpIdRequest, token : String) {
        Log.d("####", "getWorkLocation: ${token}")
        val headers = HashMap<String, String>()
        headers["AUTHORIZATION"] = "token"
        val response = apiService.getLocation(empIdRequest)
        Log.d("####", "getWorkLocation: ${response.body()}")
        if (response.isSuccessful) {
            val locationResponse = response.body().let {locationResponse ->
                if (locationResponse!!.code == 200) {
                    _locationMutableLiveData.postValue(Event(Response.Success(locationResponse)))
                } else {
                    _locationMutableLiveData.postValue(Event(Response.Exception("Something Went Wrong")))
                }
            }
        } else {
            _locationMutableLiveData.postValue(Event(Response.Exception("Something Went Wrong")))
        }
    }


}