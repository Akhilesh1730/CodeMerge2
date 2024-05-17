package com.FTG2024.hrms.dashboard.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.FTG2024.hrms.base.EmpIdRequest
import com.FTG2024.hrms.uidata.Event
import com.FTG2024.hrms.uidata.Response

class DashBoardRepo(private val apiService : DashboardApiService) {
    private var _dashBoardMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val dashBoardMutableLiveData : LiveData<Event<Response>>
        get() = _dashBoardMutableLiveData

    private var _locationMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val locationLiveData : LiveData<Event<Response>>
        get() = _locationMutableLiveData


    suspend fun getDashBoardData(request: EmpIdRequest) {
        val response = apiService.getDashData(request)
        Log.d("###", "getDashBoardData: $response")
        if (response.isSuccessful) {
            val dashBoardResponse = response.body()
            if (dashBoardResponse!!.code == 200) {
                _dashBoardMutableLiveData.postValue(Event(Response.Success(dashBoardResponse)))
            } else {
                _dashBoardMutableLiveData.postValue(Event(Response.Exception(response.message())))
            }
        } else {
            _dashBoardMutableLiveData.postValue(Event(Response.Exception("Failed To Load Data")))
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