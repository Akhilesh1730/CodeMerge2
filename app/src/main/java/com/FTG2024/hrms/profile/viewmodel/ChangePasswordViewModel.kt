package com.FTG2024.hrms.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.FTG2024.hrms.profile.model.ChangePasswordData
import com.FTG2024.hrms.profile.repo.ChangePasswordRepo
import com.FTG2024.hrms.uidata.Event
import com.FTG2024.hrms.uidata.Response
import kotlinx.coroutines.launch

class ChangePasswordViewModel(private val repo : ChangePasswordRepo) : ViewModel(){

    fun changePass(request : ChangePasswordData) {
        viewModelScope.launch {
            repo.changePass(request)
        }
    }

    fun getChangePassLiveData(): LiveData<Event<Response>> {
        return repo.changePassMutableLiveData
    }
}