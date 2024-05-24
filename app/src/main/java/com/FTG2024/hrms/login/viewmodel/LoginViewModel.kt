package com.FTG2024.hrms.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.FTG2024.hrms.login.model.LoginRequest
import com.FTG2024.hrms.login.repo.LoginRepository
import com.FTG2024.hrms.uidata.Event
import com.FTG2024.hrms.uidata.Response
import kotlinx.coroutines.launch

class LoginViewModel(private val repo : LoginRepository) : ViewModel(){

     fun authUser(request : LoginRequest) {
        viewModelScope.launch {
            repo.authUser(request)
        }
    }

    fun getEmployeeProfile() {
        viewModelScope.launch {
            repo.getEmployeeProfile()
        }
    }

    fun getLoginUserLiveData(): LiveData<Event<Response>> {
        return repo.loginUserMutableLiveData
    }

    fun getUserLiveData(): LiveData<Event<Response>> {
        return repo.userDetailsMutableLiveData
    }
}