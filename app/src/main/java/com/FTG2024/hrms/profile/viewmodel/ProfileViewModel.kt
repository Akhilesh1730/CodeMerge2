package com.FTG2024.hrms.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.FTG2024.hrms.profile.repo.ProfileRepo
import com.FTG2024.hrms.uidata.Event
import com.FTG2024.hrms.uidata.Response
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel(private val repo : ProfileRepo) : ViewModel() {

    fun setProfileImage(image: MultipartBody.Part) {
        viewModelScope.launch {
            repo.setProfileImage(image)
        }
    }

    fun getProfileDetails() {
        viewModelScope.launch {
            repo.getProfileDetails()
        }
    }


    fun getImageUploadedLivedata(): LiveData<Event<Response>> {
        return repo.imageUploadedMutableLiveData
    }


}