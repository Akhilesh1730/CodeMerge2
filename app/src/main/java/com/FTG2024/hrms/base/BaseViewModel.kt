package com.FTG2024.hrms.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.FTG2024.hrms.uidata.Event

class BaseViewModel() : ViewModel() {
    private var _locationPermissionLiveData = MutableLiveData<Event<Boolean>>()
    val locationPermissionLiveData : LiveData<Event<Boolean>>
        get() = _locationPermissionLiveData

    fun setLocationPermissionLiveData(value: Boolean) {
        _locationPermissionLiveData.postValue(Event(value))
    }
}