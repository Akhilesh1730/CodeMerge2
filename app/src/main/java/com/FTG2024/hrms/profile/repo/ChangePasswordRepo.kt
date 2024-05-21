package com.FTG2024.hrms.profile.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.FTG2024.hrms.profile.model.ChangePasswordData
import com.FTG2024.hrms.uidata.Event
import com.FTG2024.hrms.uidata.Response

class ChangePasswordRepo(private val changePasswordService: ChangePasswordService) {
    private var _changePasswordMutableLiveData: MutableLiveData<Event<Response>> = MutableLiveData()
    val changePassMutableLiveData : LiveData<Event<Response>>
        get() = _changePasswordMutableLiveData

    suspend fun changePass(request : ChangePasswordData) {
        val response = changePasswordService.changePassword(request)
        Log.d("###", "changePass: $response")
        if (response.isSuccessful) {
            val changePasswordResponse = response.body()
            if (changePasswordResponse!!.code == 200) {
                _changePasswordMutableLiveData.postValue(Event(Response.Success(changePasswordResponse)))
            } else if (changePasswordResponse!!.code == 304){
                _changePasswordMutableLiveData.postValue(Event(Response.Exception("Please enter correct password")))
            } else {
                _changePasswordMutableLiveData.postValue(Event(Response.Exception("Unable to reach servers")))
            }

        } else {
            _changePasswordMutableLiveData.postValue(Event(Response.Exception("Unable to reach servers")))
        }
    }
}