package com.FTG2024.hrms.login.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.FTG2024.hrms.login.model.LoginRequest
import com.FTG2024.hrms.uidata.Event
import com.FTG2024.hrms.uidata.Response
import com.example.myapplication.apiservice.apiInterface

class LoginRepository(private val loginApi : LoginActivityApiService) {
    private var _loginUserMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val loginUserMutableLiveData : LiveData<Event<Response>>
        get() = _loginUserMutableLiveData

    private var _userDetailsMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val userDetailsMutableLiveData : LiveData<Event<Response>>
        get() = _userDetailsMutableLiveData
    suspend fun authUser(request : LoginRequest) {
        val response = loginApi.authUser(request)
        Log.d("###", "authUser: $response")
        if (response.isSuccessful) {
            val loginResponse = response.body()
            if (loginResponse!!.code == 200) {
                _loginUserMutableLiveData.postValue(Event(Response.Success(loginResponse)))
            } else if (loginResponse.code == 304) {
                _loginUserMutableLiveData.postValue(Event(Response.Exception(loginResponse.message)))
            } else {
                _loginUserMutableLiveData.postValue(Event(Response.Exception("Unable to reach servers. Retry")))
            }
        } else {
            _loginUserMutableLiveData.postValue(Event(Response.Exception("Unable to reach servers. Retry")))
        }
    }

    suspend fun getEmployeeProfile() {
        val response = loginApi.getProfileDetails()
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