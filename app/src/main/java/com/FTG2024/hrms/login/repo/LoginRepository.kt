package com.FTG2024.hrms.login.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.FTG2024.hrms.login.model.LoginRequest
import com.FTG2024.hrms.uidata.Event
import com.FTG2024.hrms.uidata.Response

class LoginRepository(private val loginApi : LoginActivityApiService) {
    private var _loginUserMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val loginUserMutableLiveData : LiveData<Event<Response>>
        get() = _loginUserMutableLiveData

    suspend fun authUser(request : LoginRequest) {
        val response = loginApi.authUser(request)
        if (request != null) {
            _loginUserMutableLiveData.postValue(Event(Response.Success(response)))
        } else {
            _loginUserMutableLiveData.postValue(Event(Response.Exception("Something Went Wrong")))
        }
    }
}